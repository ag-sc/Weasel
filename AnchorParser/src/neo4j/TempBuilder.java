package neo4j;

import java.io.IOException;
import java.util.LinkedList;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.tooling.GlobalGraphOperations;

public class TempBuilder extends Neo4jCore{

	public static void main(String[] args) {
		long timeStart = System.nanoTime();
		
//		deleteSemSigRelations();
		
		
		
		try {
			Neo4jBatchInsert anchorsinserter = new Neo4jBatchInsert("Anchors",
									"anchors.txt",
									"stopwords.txt");
			anchorsinserter.run();
			
			System.out.println("\nFinished Anchors.\n");
			
			Neo4jBatchPageLinks inserter = new Neo4jBatchPageLinks("BatchPageLinks", "page_links_en.nt");
			inserter.run();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		long timeEnd = System.nanoTime();
		double passedTime = (timeEnd - timeStart) / 60000000000.0;
		System.out.println("Total passed time: " + passedTime + " mins");
	}
	
	public static void deleteSemSigRelations(){
		System.out.println("about to open db");
		GraphDatabaseService graphDb = new GraphDatabaseFactory().newEmbeddedDatabase( "BatchPageLinks" );
		registerShutdownHook( graphDb );
		
		int nodeCounter = 0;
		int relCounter = 0;
		try (Transaction tx = graphDb.beginTx()) {
			GlobalGraphOperations global = GlobalGraphOperations.at(graphDb);
			LinkedList<Relationship> list = new LinkedList<Relationship>();
			for (Node n : global.getAllNodes()) {
				nodeCounter++;

				for(Relationship r: n.getRelationships(RelTypes.SEMANTIC_SIGNATURE_OF)) list.add(r);
				for(Relationship r: list){
					r.delete();
					relCounter++;
				}
				
				if(nodeCounter % 1000000 == 0) System.out.println("Nodes looked at: " + nodeCounter + "	 - relationships deleted: " + relCounter);
			}
			
			tx.success();
		}
		graphDb.shutdown();
	}
	


}
