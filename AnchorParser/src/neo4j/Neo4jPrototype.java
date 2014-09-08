package neo4j;

import java.io.File;

import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

public abstract class Neo4jPrototype extends Neo4jCore{
	
	final int transactionBuffer = 500;
	String dbPath;
	
	GraphDatabaseService graphDB;
	Transaction currentTransaction;
	int changeCounter = 0;
	int totalCounter = 0;
	long timeStart, timeEnd;
	
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
			timeStart = System.nanoTime();
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
			
			if(totalCounter %1000000 == 0){
				timeEnd = System.nanoTime();
				double passedTime = (timeEnd - timeStart) / 60000000000.0;
				System.out.println("Processed: " + totalCounter + "\t - time: " + passedTime + " mins");
				timeStart = System.nanoTime();
			}
		}
		
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
