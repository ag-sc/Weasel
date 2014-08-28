package neo4j;
import java.io.File;
import java.io.IOException;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.schema.IndexDefinition;
import org.neo4j.graphdb.schema.Schema;
import org.neo4j.tooling.GlobalGraphOperations;

import fileparser.WikiParser;


public class Neo4jDatabaseBuilder {
	static long start, end;
	private static String DB_PATH;
	private static int transactionBuffer = 500;
	
	static GraphDatabaseService graphDb;
	static Label entityLabel = DynamicLabel.label( "Entity" );
	static String[] tuple;
	static Transaction currentTransaction;
	static int changeCounter = 0;
	static int totalCounter = 0;
	static String current = "nothing";
	
	static TreeSet<String> treeSet;
	static int nrOfNodes = 0;
	static int nrOfEdges = 0;
	
	private static enum RelTypes implements RelationshipType {
		CONNECTION
	}
	
	public static void run(String dbPath, String infoboxPath, String categoriesPath) {
		// Setup
		start = System.nanoTime();
		DB_PATH = dbPath;
		deleteFileOrDirectory(new File(DB_PATH));
		graphDb = new GraphDatabaseFactory().newEmbeddedDatabase( DB_PATH );
		registerShutdownHook( graphDb );
		
		IndexDefinition indexDefinition;
		try (Transaction tx = graphDb.beginTx()) {
			Schema schema = graphDb.schema();
			indexDefinition = schema.indexFor(entityLabel).on("name").create();
			tx.success();
		}
		try (Transaction tx = graphDb.beginTx()) {
			Schema schema = graphDb.schema();
			schema.awaitIndexOnline(indexDefinition, 10, TimeUnit.SECONDS);
		}
		
		current = "read file 1";
		currentTransaction = graphDb.beginTx();
		changeCounter = 0;
		// Read File 1
		try{
			WikiParser infoboxParser = new WikiParser(infoboxPath);
			while((tuple = infoboxParser.parseTuple()) != null){
				addRelation(tuple);
			}
			infoboxParser.close();
		}catch (IOException e){
			e.printStackTrace();
			return;
		}

		current = "read file 2";
		// Read File 2 - no new transactions needed
		try {
			WikiParser infoboxParser = new WikiParser(categoriesPath);
			infoboxParser.setPatters("<.*?resource/([^>]+)>", "<.*?resource/Category:([^>]+)>");
			while ((tuple = infoboxParser.parseTuple()) != null) {
				addRelation(tuple);
			}
			infoboxParser.close();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		// Calculate weights
		transactionBuffer = 10000;
		current = "calculate and add weights";
		GlobalGraphOperations global = GlobalGraphOperations.at(graphDb);
		for (Relationship rel : global.getAllRelationships()) {
			Node source = rel.getStartNode();
			Node sink = rel.getEndNode();
			double weight = 1;

			for (Relationship r1 : sink.getRelationships(Direction.OUTGOING)) {
				for (Relationship r2 : r1.getEndNode().getRelationships(Direction.OUTGOING)) {
					Node tmp = r2.getEndNode();
					if (tmp.equals(source)) {
						weight += 1;
					}
				}
			}

			setWeight(rel, weight);
		}
		
		currentTransaction.success();
		currentTransaction.close();
		
		graphDb.shutdown();
		
		System.out.println("Nr of nodes: " + nrOfNodes + " - nr of edges: " + nrOfEdges);
	}
	
	private static void checkTransactions(){
		if(changeCounter >= transactionBuffer){
			totalCounter += changeCounter;
			currentTransaction.success();
			currentTransaction.close();
			changeCounter = 0;
			currentTransaction = graphDb.beginTx();
			
//			if (totalCounter % 10000 == 0) {
//				end = System.nanoTime();
//				double passedTime = (end - start) / 1000000000.0;
//				System.out.println("processed: " + totalCounter + " passed time: " + passedTime + " s - currently: " + current);
//				start = System.nanoTime();
//			}
		}
	}
	
	private static void setWeight(Relationship r, double weight){
		r.setProperty("weight", weight);
		
		changeCounter++;
		checkTransactions();
	}
	
	private static void addRelation(String[] tuple){
		//if(!treeSet.contains(tuple[0]) && !treeSet.contains(tuple[1])) return;
		nrOfEdges++;
		
		Node sourceNode = null;
		for ( Node node : graphDb.findNodesByLabelAndProperty( entityLabel, "name", tuple[0] ) ){
			sourceNode = node;
		}
		if(sourceNode == null){
			sourceNode = graphDb.createNode(entityLabel);
			sourceNode.setProperty("name", tuple[0]);
			nrOfNodes++;
		}
		
		Node sinkNode = null;
		for ( Node node : graphDb.findNodesByLabelAndProperty( entityLabel, "name", tuple[1] ) ){
			sinkNode = node;
		}
		if(sinkNode == null){
			sinkNode = graphDb.createNode(entityLabel);
			sinkNode.setProperty("name", tuple[1]);
			nrOfNodes++;
		}
		
		sourceNode.createRelationshipTo(sinkNode, RelTypes.CONNECTION);
		
		changeCounter++;
		checkTransactions();
	}
	
	private static void registerShutdownHook(final GraphDatabaseService graphDb) {
		// Registers a shutdown hook for the Neo4j instance so that it
		// shuts down nicely when the VM exits (even if you "Ctrl-C" the
		// running application).
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				graphDb.shutdown();
			}
		});
	}
	
	private static void deleteFileOrDirectory(File file) {
		if (file.exists()) {
			if (file.isDirectory()) {
				for (File child : file.listFiles()) {
					deleteFileOrDirectory(child);
				}
			}
			file.delete();
		}
	}
	
}
