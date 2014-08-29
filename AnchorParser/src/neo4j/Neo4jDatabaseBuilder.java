package neo4j;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.schema.IndexDefinition;
import org.neo4j.graphdb.schema.Schema;
import org.neo4j.tooling.GlobalGraphOperations;

import fileparser.WikiParser;


public class Neo4jDatabaseBuilder extends Neo4jPrototype {
	

	long start, end;
	
	String[] tuple;
	String current = "nothing";
	
	int nrOfNodes = 0;
	int nrOfEdges = 0;
	
	public Neo4jDatabaseBuilder(String dbPath) {
		super(dbPath);
		deleteFileOrDirectory(new File(dbPath));
	}
	

	@Override
	void buildIndex(){
		IndexDefinition indexDefinition;
		try (Transaction tx = graphDB.beginTx()) {
			Schema schema = graphDB.schema();
			indexDefinition = schema.indexFor(entityLabel).on("name").create();
			tx.success();
		}
		try (Transaction tx = graphDB.beginTx()) {
			Schema schema = graphDB.schema();
			schema.awaitIndexOnline(indexDefinition, 10, TimeUnit.SECONDS);
		}
	}
	
	@Override
	void _run(String[] args) {
		start = System.nanoTime();
		
		current = "read file 1";
		changeCounter = 0;
		// Read File 1
		try{
			WikiParser infoboxParser = new WikiParser(args[0]);
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
			WikiParser infoboxParser = new WikiParser(args[1]);
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
		current = "calculate and add weights";
		GlobalGraphOperations global = GlobalGraphOperations.at(graphDB);
		for (Relationship rel : global.getAllRelationships()) {
			Node source = rel.getStartNode();
			Node sink = rel.getEndNode();
			double weight = 1;

			for (Relationship r1 : sink.getRelationships(Direction.OUTGOING, RelTypes.CONNECTION)) {
				for (Relationship r2 : r1.getEndNode().getRelationships(Direction.OUTGOING, RelTypes.CONNECTION)) {
					Node tmp = r2.getEndNode();
					if (tmp.equals(source)) {
						weight += 1;
					}
				}
			}

			setWeight(rel, weight);
		}
		System.out.println("Nr of nodes: " + nrOfNodes + " - nr of edges: " + nrOfEdges);
	}
	
	private void setWeight(Relationship r, double weight){
		r.setProperty("weight", weight);
		
		changeCounter++;
		checkTransactions();
	}
	
	private void addRelation(String[] tuple){
		//if(!treeSet.contains(tuple[0]) && !treeSet.contains(tuple[1])) return;
		nrOfEdges++;
		
		Node sourceNode = null;
		for ( Node node : graphDB.findNodesByLabelAndProperty( entityLabel, "name", tuple[0] ) ){
			sourceNode = node;
		}
		if(sourceNode == null){
			sourceNode = graphDB.createNode(entityLabel);
			sourceNode.setProperty("name", tuple[0]);
			nrOfNodes++;
		}
		
		Node sinkNode = null;
		for ( Node node : graphDB.findNodesByLabelAndProperty( entityLabel, "name", tuple[1] ) ){
			sinkNode = node;
		}
		if(sinkNode == null){
			sinkNode = graphDB.createNode(entityLabel);
			sinkNode.setProperty("name", tuple[1]);
			nrOfNodes++;
		}
		
		if(!sourceNode.equals(sinkNode)) sourceNode.createRelationshipTo(sinkNode, RelTypes.CONNECTION);
		
		changeCounter++;
		checkTransactions();
	}
	
}
