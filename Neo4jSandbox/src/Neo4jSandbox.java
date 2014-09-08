import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

import neo4j.Neo4jCore;

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


public class Neo4jSandbox extends Neo4jCore{
	
	private static final String DB_PATH = "../../data/DBs/test/AnchorsDavid";
	
	static GraphDatabaseService graphDb;
	static Node firstNode;
	static Node secondNode;
	static Relationship relationship;
	
	public static void main(String[] args) {
		
		System.out.println("about to open db");
		graphDb = new GraphDatabaseFactory().newEmbeddedDatabase( DB_PATH );
		registerShutdownHook( graphDb );
// Anchor Test	
		
//		try (Transaction tx = graphDb.beginTx()) {
//			GlobalGraphOperations global = GlobalGraphOperations.at(graphDb);
//			for(Node n: global.getAllNodesWithLabel(partialAnchorLabel)){
//				System.out.println(n.getProperty("name"));
//				for(Relationship toAnchor: n.getRelationships(Direction.OUTGOING, RelTypes.PARTIALMATCH)){
//					Node anchor = toAnchor.getEndNode();
//					System.out.println("	" + anchor.getProperty("name"));
//				}
//			}
//			tx.success();
//		}
		
// SemSig Test
		String search = "David";
		System.out.println("Done! Perform test query for '" + search + "'.");
		System.out.println("Entity:");
		try (Transaction tx = graphDb.beginTx()) {
			for ( Node node : graphDb.findNodesByLabelAndProperty( Neo4jCore.partialAnchorLabel, "name", search ) ){
				Node entity = node;
				int partialcount = 0;
				for(Relationship r: entity.getRelationships(Direction.OUTGOING)){
					//System.out.println("out: " + r.getEndNode().getProperty("name"));
					partialcount++;
				}
				
				System.out.println("nr of out relations for " + search + " - partial: " + partialcount);
//				for(Relationship r: entity.getRelationships(Direction.INCOMING)){
//					System.out.println("in:  " + r.getStartNode().getProperty("name"));
//				}
			}
			for ( Node node : graphDb.findNodesByLabelAndProperty( Neo4jCore.entityLabel, "name", search ) ){
				Node entity = node;
				int anchorcount = 0;
				for(Relationship r: entity.getRelationships(Direction.INCOMING)){
					System.out.println("out: " + r.getStartNode().getProperty("name"));
					anchorcount++;
				}
				System.out.println("nr of out relations for " + search + " - entity: " + anchorcount);
			}
			
//			System.out.println("Anchor");
//			for ( Node node : graphDb.findNodesByLabelAndProperty( anchorLabel, "name", search ) ){
//				Node entity = node;
//				for(Relationship r: entity.getRelationships(Direction.OUTGOING)){
//					System.out.println("anchor of: " + r.getEndNode().getProperty("name"));
//				}
//				for(Relationship r: entity.getRelationships(Direction.INCOMING)){
//					System.out.println("in:  " + r.getStartNode().getProperty("name"));
//				}
//			}
			tx.success();
		}
		System.out.println("all done");
	}
	
	
}
