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


public class Neo4jSandbox {
	
	private static final String DB_PATH = "../../data/Mappingbased Properties/testDB";
	
	static GraphDatabaseService graphDb;
	static Node firstNode;
	static Node secondNode;
	static Relationship relationship;
	
	static Label entityLabel = DynamicLabel.label( "Entity" );
	
	private static enum RelTypes implements RelationshipType {
		IN_SIGNATURE
	}
	
	public static void main(String[] args) {
//		deleteFileOrDirectory(new File(DB_PATH));
//		
//		HashMap<String, HashMap<String, Integer>> semanticSignature = new HashMap<String, HashMap<String, Integer>>();
//		try {
//			FileInputStream fileInputStream = new FileInputStream("semantic signature.binary");
//			ObjectInputStream objectReader = new ObjectInputStream(fileInputStream);
//			semanticSignature = (HashMap<String, HashMap<String, Integer>>) objectReader.readObject(); 
//			objectReader.close();
//			fileInputStream.close();
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			System.out.println("failure opening semantic signature binary file");
//			return;
//		}
		
//		TreeSet<String> tmpSet1 = new TreeSet<String>();
//		tmpSet1.add("B");
//		tmpSet1.add("C");
//		TreeSet<String> tmpSet2 = new TreeSet<String>();
//		tmpSet2.add("A");
//		TreeSet<String> tmpSet3 = new TreeSet<String>();
//		tmpSet3.add("B");
//		tmpSet3.add("D");
//		TreeSet<String> tmpSet4 = new TreeSet<String>();
//		tmpSet4.add("A");
//		tmpSet4.add("B");
//		
//		semanticSignature.put("A", tmpSet1);
//		semanticSignature.put("B", tmpSet2);
//		semanticSignature.put("C", tmpSet3);
//		semanticSignature.put("D", tmpSet4);
		
		
		graphDb = new GraphDatabaseFactory().newEmbeddedDatabase( DB_PATH );
		registerShutdownHook( graphDb );
		
//		IndexDefinition indexDefinition;
//		try (Transaction tx = graphDb.beginTx()) {
//			Schema schema = graphDb.schema();
//			indexDefinition = schema.indexFor(entityLabel).on("name").create();
//			tx.success();
//		}
//		// END SNIPPET: createIndex
//		// START SNIPPET: wait
//		try (Transaction tx = graphDb.beginTx()) {
//			Schema schema = graphDb.schema();
//			schema.awaitIndexOnline(indexDefinition, 10, TimeUnit.SECONDS);
//		}
//		
//		System.out.println("Create all the entity nodes...");
//		try (Transaction tx = graphDb.beginTx()) {
//			for (Entry<String, HashMap<String, Integer>> entry : semanticSignature.entrySet()) {
//				Node entity = graphDb.createNode(entityLabel);
//				entity.setProperty("name", entry.getKey());
//			}
//			tx.success();
//		}
//		
//		System.out.println("Create all the node relations...");
//		try (Transaction tx = graphDb.beginTx()) {
//			for (Entry<String, HashMap<String, Integer>> entry : semanticSignature.entrySet()) {
//				Node entity = null; 
//				for ( Node node : graphDb.findNodesByLabelAndProperty( entityLabel, "name", entry.getKey() ) ){
//					entity = node;
//				}
//				
//				for(String signatureEntityName: entry.getValue().keySet()){
//					Node signatureEntity = null;
//					for ( Node node : graphDb.findNodesByLabelAndProperty( entityLabel, "name", signatureEntityName ) ){
//						signatureEntity = node;
//						entity.createRelationshipTo(signatureEntity, RelTypes.IN_SIGNATURE);
//					}
//					
//				}
//			}
//			tx.success();
//		}
		
		System.out.println("Done! Perform test query for 'David_Beckham'.");
		
		try (Transaction tx = graphDb.beginTx()) {
			for ( Node node : graphDb.findNodesByLabelAndProperty( entityLabel, "name", "David_Beckham" ) ){
				Node entity = node;
				for(Relationship r: entity.getRelationships(Direction.OUTGOING)){
					System.out.println("out: " + r.getEndNode().getProperty("name"));
				}
//				for(Relationship r: entity.getRelationships(Direction.INCOMING)){
//					System.out.println("in:  " + r.getStartNode().getProperty("name"));
//				}
			}
			tx.success();
		}
		
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
	
//	private static void deleteFileOrDirectory(File file) {
//		if (file.exists()) {
//			if (file.isDirectory()) {
//				for (File child : file.listFiles()) {
//					deleteFileOrDirectory(child);
//				}
//			}
//			file.delete();
//		}
//	}
}
