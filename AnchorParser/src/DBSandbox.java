import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.TreeSet;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.schema.IndexDefinition;
import org.neo4j.graphdb.schema.Schema;

import datatypes.TinyEdge;
import graphAccess.InMemoryGraphAccess;


public class DBSandbox {

	public static void main(String[] args) {
		long start, end;
		double passedTime;
		InMemoryGraphAccess graphAccess = new InMemoryGraphAccess();
		MPFileParser parser = new MPFileParser(graphAccess);
		HashMap<String, TreeSet<TinyEdge>> map;
		
		try {
			parser.parse("../../data/Mappingbased Properties/mappingbased_properties_cleaned_en.nt");
			//parser.parse("../../data/Mappingbased Properties/test/mappingbased_smallish.nt");
			//parser.parse("../../data/Mappingbased Properties/test/weightsTest.nt");
			
			//parser.parse("mappingbased_properties_cleaned_en.nt"); // for execution
			
			map = graphAccess.getMap();
			System.out.println(map.entrySet().size() + " map entries. Perform that many random walks.");
			
			start = System.nanoTime();
			WeightCalculator.setTriangleWeights(graphAccess);
			end = System.nanoTime();
			passedTime = (end - start) / 1000000000.0;
			System.out.println("weights set - Calculation time: " + passedTime + " s");
			
			TreeSet<TinyEdge> treeSet = graphAccess.query("David_Beckham");
			for(TinyEdge e: treeSet){
				System.out.println(e);
			}
			
//			HashMap<String, Integer> testmap = SemanticSignatureCalculator.randomWalk("David_Beckham", graphAccess);
//			for(Entry<String, Integer> e: testmap.entrySet()){
//				System.out.println(e.getKey() + ":		" + e.getValue());
//			}
			
//			System.out.println("Write weighted graph to disk.");
//			ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("weighted graph.binary"));
//			out.writeObject(map);
//			out.close();
//			System.out.println("Graph written to file 'weighted graph.binary'.");
			
//			for(Entry<String, TreeSet<TinyEdge>> e: map.entrySet()){
//				System.out.println(e.getKey() + ":");
//				for(TinyEdge s: e.getValue()){
//					System.out.println("	" + s);
//				}
//			}
			
			start = System.nanoTime();
//			GraphDatabaseService graphDb = new GraphDatabaseFactory().newEmbeddedDatabase( "testDB2" );
//			Label entityLabel = DynamicLabel.label( "Entity" );
//			registerShutdownHook( graphDb );
//			IndexDefinition indexDefinition;
//			try (Transaction tx = graphDb.beginTx()) {
//				Schema schema = graphDb.schema();
//				indexDefinition = schema.indexFor(entityLabel).on("name").create();
//				tx.success();
//			}
//			try (Transaction tx = graphDb.beginTx()) {
//				Schema schema = graphDb.schema();
//				schema.awaitIndexOnline(indexDefinition, 10, TimeUnit.SECONDS);
//			}
//			
//			int counter = 0;
//			LinkedList<String> tmpList = new LinkedList<String>();
//			for(String entity: map.keySet()){
//				tmpList.add(entity);
//				counter++;
//				if(counter % 100000 == 0){
//					System.out.println("Nodes: " + counter);
//					try (Transaction tx = graphDb.beginTx()) {
//						for (String s : tmpList) {
//							Node node = graphDb.createNode(entityLabel);
//							node.setProperty("name", s);
//						}
//						tx.success();
//					}
//					tmpList = new LinkedList<String>();
//				}
//			}
//			try (Transaction tx = graphDb.beginTx()) {
//				for (String s : tmpList) {
//					Node node = graphDb.createNode(entityLabel);
//					node.setProperty("name", s);
//				}
//				tx.success();
//			}
			start = System.nanoTime();
//			int counter = 0;
//			HashMap<String, HashMap<String, Integer>> tmp2 = new HashMap<String, HashMap<String, Integer>>();
//			for (String entity : map.keySet()) {
//				counter++;
//				// test start
//				if(entity.equals("David_Beckham")){
//					HashMap<String, Integer> testmap = SemanticSignatureCalculator.randomWalk(entity, graphAccess);
//					for(Entry<String, Integer> e: testmap.entrySet()){
//						System.out.println(e.getKey() + ":		" + e.getValue());
//					}
//				}
				// test end
				//tmp2.put(entity, SemanticSignatureCalculator.randomWalk(entity, graphAccess));
				
//				if(counter % 10000 == 0){
					
//					try (Transaction tx = graphDb.beginTx()) {
//						for (Entry<String, HashMap<String, Integer>> tmp3: tmp2.entrySet()) {
//							Node entityNode = null;
//							for (Node node : graphDb.findNodesByLabelAndProperty(entityLabel, "name", tmp3.getKey())) {
//								entityNode = node;
//							}
//							for (String s : tmp3.getValue().keySet()) {
//								Node signatureEntity = null;
//								for (Node node : graphDb.findNodesByLabelAndProperty(entityLabel, "name", s)) {
//									signatureEntity = node;
//									entityNode.createRelationshipTo(signatureEntity, RelTypes.SIGNATURE);
//								}
//
//							}
//						}
//						tx.success();
//					}
//					tmp2 = new HashMap<String, HashMap<String, Integer>>();
//					end = System.nanoTime();
//					System.out.println("nodes with connections: " + counter);
//					passedTime = (end - start) / 60000000000.0;
//					System.out.println("Remaining time: " + (passedTime * ( (double)map.keySet().size() / (double)counter)) + " mins");
//					
//					start = System.nanoTime();
//				}
//				
//			}
			
//			try (Transaction tx = graphDb.beginTx()) {
//				for (Entry<String, HashMap<String, Integer>> tmp3: tmp2.entrySet()) {
//					Node entityNode = null;
//					for (Node node : graphDb.findNodesByLabelAndProperty(entityLabel, "name", tmp3.getKey())) {
//						entityNode = node;
//					}
//					for (String s : tmp3.getValue().keySet()) {
//						Node signatureEntity = null;
//						for (Node node : graphDb.findNodesByLabelAndProperty(entityLabel, "name", s)) {
//							signatureEntity = node;
//							entityNode.createRelationshipTo(signatureEntity, RelTypes.SIGNATURE);
//						}
//
//					}
//				}
//				tx.success();
//			}
			
			//HashMap<String, HashMap<String, Integer>> signatures = SemanticSignatureCalculator.calcSignature(graphAccess);
			
//			for(Entry<String, HashMap<String, Integer>> e: signatures.entrySet()){
//				System.out.println(e.getKey() + ":");
//				for(Entry<String, Integer> e2: e.getValue().entrySet()){
//					System.out.println("	" + e2.getKey() + ": " + e2.getValue());
//				}
//			}
			end = System.nanoTime();
			passedTime = (end - start) / 60000000000.0;
			System.out.println("Calculation time: " + passedTime + " minutes");
			
//			System.out.println("Write semantic signatures to disk.");
//			out = new ObjectOutputStream(new FileOutputStream("semantic signature.binary"));
//			out.writeObject(signatures);
//			out.close();
//			System.out.println("Graph written to file 'semantic signature.binary'.");
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println("Done! :D");
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
	
	private static enum RelTypes implements RelationshipType {
		SIGNATURE
	}

}
