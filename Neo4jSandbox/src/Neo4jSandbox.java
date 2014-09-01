import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.Map.Entry;
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


public class Neo4jSandbox {
	
	private static final String DB_PATH = "../../data/DBs/Anchors";
	
	static GraphDatabaseService graphDb;
	static Node firstNode;
	static Node secondNode;
	static Relationship relationship;
	
	static Label entityLabel = DynamicLabel.label( "Entity" );
	
	static enum RelTypes implements RelationshipType {
		CONNECTION,
		PARTIALMATCH,
		SEMANTIC_SIGNATURE,
		ANCHOR
	}
	
	public static void main(String[] args) {
		
		System.out.println("about to open db");
		graphDb = new GraphDatabaseFactory().newEmbeddedDatabase( DB_PATH );
		registerShutdownHook( graphDb );
// Anchor Test	
		Label anchorLabel = DynamicLabel.label( "Anchor" );
		Label partialAnchorLabel = DynamicLabel.label( "PartialAnchor" );
		
		try (Transaction tx = graphDb.beginTx()) {
			GlobalGraphOperations global = GlobalGraphOperations.at(graphDb);
			for(Node n: global.getAllNodesWithLabel(partialAnchorLabel)){
				System.out.println(n.getProperty("name"));
				for(Relationship toAnchor: n.getRelationships(Direction.OUTGOING, RelTypes.PARTIALMATCH)){
					Node anchor = toAnchor.getEndNode();
					System.out.println("	" + anchor.getProperty("name"));
				}
			}
			tx.success();
		}
		
// SemSig Test
//		System.out.println("Done! Perform test query for 'David_Beckham'.");
//		
//		try (Transaction tx = graphDb.beginTx()) {
//			for ( Node node : graphDb.findNodesByLabelAndProperty( entityLabel, "name", "Red_Army_Faction" ) ){
//				Node entity = node;
//				int counter = 0;
//				for(Relationship r: entity.getRelationships(Direction.OUTGOING, RelTypes.SEMANTIC_SIGNATURE)){
//					System.out.println("out: " + r.getEndNode().getProperty("name"));
//					counter++;
//				}
//				System.out.println("number of rels in semsig: " + counter);
//				for(Relationship r: entity.getRelationships(Direction.INCOMING)){
//					System.out.println("in:  " + r.getStartNode().getProperty("name"));
//				}
//			}
//			tx.success();
//		}
		
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
	
}
