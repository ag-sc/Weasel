package neo4j;

import graphAccess.GraphAccess;

import java.io.File;
import java.util.HashMap;
import java.util.TreeSet;
import java.util.Map.Entry;
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

import datatypes.TinyEdge;

public class Neo4jSemSigBuilder {
	static long start, end;
	private static Label entityLabel = DynamicLabel.label( "Entity" );
	private static GraphDatabaseService sourceDB;
	private static GraphDatabaseService semsigDB;
	private static final int transactionBuffer = 500;
	private static Transaction currentTransaction;
	private static int changeCounter = 0;
	private static int totalCounter = 0;
	
	private static double restartProbability = 0.85; // 0.85
	private static int frequencyThreshold = 10; // 100
	private static int numberOfSteps = 100000; // 1000000
	
	private static enum RelTypes implements RelationshipType {
		CONNECTION
	}
	
	private static void explore(Node n, double currentProbability, HashMap<Node,Double> signature, double continueProb){
		double totalWeight = 0.0;
		for(Relationship r: n.getRelationships(Direction.OUTGOING)){
			totalWeight += (double) r.getProperty("weight", 0);
		}
		
		for(Relationship r: n.getRelationships(Direction.OUTGOING)){
			double tmp = (((double) r.getProperty("weight", 0)) / totalWeight) * continueProb * currentProbability;	
			if(tmp > (double)frequencyThreshold / (double)numberOfSteps){
				signature.put(r.getEndNode(), tmp);
				explore(r.getEndNode(), tmp, signature, (1.0 - restartProbability));
			}
		}
	}
	
	private static void randomWalkProbability(Node source){
		HashMap<Node, Double> tmpSignature = new HashMap<Node, Double>();
		
		explore(source, 1, tmpSignature, 1);
		
		for(Entry<Node, Double> e: tmpSignature.entrySet()){
			addRelation((String)source.getProperty("name"), (String)e.getKey().getProperty("name"), e.getValue());
		}
	}
	
	private static void randomWalk(Node source){
		Node currentNode = source;
		HashMap<Node, Integer> tmpSignature = new HashMap<Node, Integer>();
		
		for(int i = 0; i < numberOfSteps; i++){
			if(Math.random() > restartProbability){
				
				if(currentNode.getDegree(Direction.OUTGOING) == 0){
					currentNode = source;
					continue;
				}
				
				double totalWeight = 0.0;
				for(Relationship r: currentNode.getRelationships(Direction.OUTGOING)){
					totalWeight += (double) r.getProperty("weight", 0);
				}
				
				double random = Math.random();
				double accumulatedWeight = 0.0;
				
				for(Relationship r: currentNode.getRelationships(Direction.OUTGOING)){
					accumulatedWeight += ((double) r.getProperty("weight", 0)) / totalWeight;
					if(random < accumulatedWeight){
						if(tmpSignature.containsKey(r.getEndNode())){
							tmpSignature.put(r.getEndNode(), tmpSignature.get(r.getEndNode()) + 1);
						}else{
							tmpSignature.put(r.getEndNode(), 1);
						}
						currentNode = r.getEndNode();
						break;
					}
				}
				
			}else{ // restart walk
				currentNode = source;
			}
		}
		
		for(Entry<Node, Integer> e: tmpSignature.entrySet()){
			if(e.getValue() > frequencyThreshold){
				addRelation((String)source.getProperty("name"), (String)e.getKey().getProperty("name"), e.getValue());
			}
		}
	}
	
	private static void checkTransactions(){
		if(changeCounter >= transactionBuffer){
			totalCounter += changeCounter;
			currentTransaction.success();
			currentTransaction.close();
			changeCounter = 0;
			currentTransaction = semsigDB.beginTx();
			
			end = System.nanoTime();
			
//			if (totalCounter % 10000 == 0) {
//				double passedTime = (end - start) / 1000000000.0;
//				System.out.println("processed: " + totalCounter + " passed time: " + passedTime + " s");
//				start = System.nanoTime();
//			}
		}
	}
	
	private static void addRelation(String source, String sink, double count){
		if(source.equals(sink)) return;
		
		Node sourceNode = null;
		for ( Node node : semsigDB.findNodesByLabelAndProperty( entityLabel, "name", source ) ){
			sourceNode = node;
		}
		if(sourceNode == null){
			sourceNode = semsigDB.createNode(entityLabel);
			sourceNode.setProperty("name", source);
		}
		
		Node sinkNode = null;
		for ( Node node : semsigDB.findNodesByLabelAndProperty( entityLabel, "name", sink ) ){
			sinkNode = node;
		}
		if(sinkNode == null){
			sinkNode = semsigDB.createNode(entityLabel);
			sinkNode.setProperty("name", sink);
		}
		
		Relationship r = sourceNode.createRelationshipTo(sinkNode, RelTypes.CONNECTION);
		r.setProperty("count", count);
		
		changeCounter++;
		checkTransactions();
	}
	
	
	public static void build(String sourceDBPath, String semsigDBPath){
		start = System.nanoTime();
		sourceDB = new GraphDatabaseFactory().newEmbeddedDatabase( sourceDBPath );
		registerShutdownHook( sourceDB );
		
		deleteFileOrDirectory(new File(semsigDBPath));
		semsigDB = new GraphDatabaseFactory().newEmbeddedDatabase( semsigDBPath );
		registerShutdownHook( semsigDB );
		
		IndexDefinition indexDefinition;
		try (Transaction tx = semsigDB.beginTx()) {
			Schema schema = semsigDB.schema();
			indexDefinition = schema.indexFor(entityLabel).on("name").create();
			tx.success();
		}
		try (Transaction tx = semsigDB.beginTx()) {
			Schema schema = semsigDB.schema();
			schema.awaitIndexOnline(indexDefinition, 10, TimeUnit.SECONDS);
		}

		changeCounter = 0;
		currentTransaction = semsigDB.beginTx();
		try (Transaction tx = sourceDB.beginTx()) {
			GlobalGraphOperations global = GlobalGraphOperations.at(sourceDB);
			for (Node n : global.getAllNodes()) {
				//randomWalk(n);
				randomWalkProbability(n);
			}

			tx.success();
		}
		
		GlobalGraphOperations global = GlobalGraphOperations.at(semsigDB);
		int tmp = 0;
		for (Node n : global.getAllNodes()) {
			System.out.println("node: " + n.getProperty("name", "FAILURE"));
			for(Relationship r: n.getRelationships(Direction.OUTGOING)){
				System.out.println("	" + (String)r.getEndNode().getProperty("name"));
			}
			if(tmp++ > 20) break;
		}
		
		currentTransaction.success();
		currentTransaction.close();
		semsigDB.shutdown();
		sourceDB.shutdown();
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
