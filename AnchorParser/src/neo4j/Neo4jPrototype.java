package neo4j;

import java.io.File;

import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

public abstract class Neo4jPrototype {
	
	final int transactionBuffer = 500;
	String dbPath;
	
	GraphDatabaseService graphDB;
	Transaction currentTransaction;
	Label entityLabel = DynamicLabel.label( "Entity" );
	int changeCounter = 0;
	int totalCounter = 0;
	
	static enum RelTypes implements RelationshipType {
		CONNECTION,
		PARTIALMATCH,
		SEMANTIC_SIGNATURE
	}
	
	public Neo4jPrototype(String dbPath){
		this.dbPath = dbPath;
	}
	
	void buildIndex(){
		// empty so subclasses can overwrite
	}
	
	public final void run(String[] args){
		graphDB = new GraphDatabaseFactory().newEmbeddedDatabase( dbPath );
		registerShutdownHook( graphDB );
		buildIndex();
		currentTransaction = graphDB.beginTx();
		try{
			_run(args);
		}finally{
			currentTransaction.success();
			currentTransaction.close();
			graphDB.shutdown();
		}
	}
	
	abstract void _run(String[] args);
	
	void checkTransactions(){
		if(changeCounter >= transactionBuffer){
			totalCounter += changeCounter;
			currentTransaction.success();
			currentTransaction.close();
			changeCounter = 0;
			currentTransaction = graphDB.beginTx();
		}
		
	}

	void registerShutdownHook(final GraphDatabaseService graphDb) {
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
	
	void deleteFileOrDirectory(File file) {
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
