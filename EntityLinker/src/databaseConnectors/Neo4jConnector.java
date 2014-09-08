package databaseConnectors;

import java.util.LinkedList;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

public class Neo4jConnector extends DatabaseConnector{

	private String dbPath = null;
	private GraphDatabaseService graphDB;
	private Label label;
	
	public Neo4jConnector(String dbPath, Label label){
		this.dbPath = dbPath;
		graphDB = new GraphDatabaseFactory().newEmbeddedDatabase( dbPath );
		registerShutdownHook(graphDB);
		this.label = label;
	}
	
	@Override
	public LinkedList<String> lookUpFragment(String fragment) {
		LinkedList<String> list = new LinkedList<String>();
		
		try (Transaction tx = graphDB.beginTx()) {
			Node entity = null;
			for ( Node node : graphDB.findNodesByLabelAndProperty( label, "name", fragment ) ){
				entity = node;
			}
			if(entity != null) for (Relationship r : entity.getRelationships(Direction.OUTGOING)) {
				list.add((String) r.getEndNode().getProperty("name"));
			}

			tx.success();
		}
		
		return list;
	}
	
	private void registerShutdownHook(final GraphDatabaseService graphDb) {
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
