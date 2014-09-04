package neo4j;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.tooling.GlobalGraphOperations;
import org.neo4j.unsafe.batchinsert.BatchInserter;
import org.neo4j.unsafe.batchinsert.BatchInserters;

import fileparser.AnchorFileParser;
import fileparser.FileParser;
import fileparser.StopWordParser;

public class Neo4jBatchInsert extends Neo4jCore{
	//TODO: assign nonglobal variables in functions
	String dbPath;
	
	BatchInserter inserter;
	HashMap<String, HashMap<Label, Long>> idMap;
	TreeSet<String> stopWords;
	String triplet[];
	AnchorFileParser anchorParser;
	
	int nodeCounter = 0;
	
	protected void assignParser(String anchorFilePath) throws IOException{
		anchorParser = new AnchorFileParser(anchorFilePath);
	}
	
	public Neo4jBatchInsert(String dbPath, String anchorFilePath) throws IOException {
		this.dbPath = dbPath;
		deleteFileOrDirectory(new File(dbPath));
		inserter = BatchInserters.inserter(dbPath);
		assignParser(anchorFilePath);
		
		idMap = new HashMap<String, HashMap<Label, Long>>();
		stopWords = new TreeSet<String>();
	}
	
	public Neo4jBatchInsert(String dbPath, String anchorFilePath, String stopWordsPath) throws IOException{
		this(dbPath, anchorFilePath);
		
		stopWords = StopWordParser.parseStopwords(stopWordsPath);
	}
	
	protected long getId(String name, Label label){
		Long id = null;
		HashMap<Label, Long> tmp;
		tmp = idMap.get(name);
		if(tmp != null) id = tmp.get(label);

		if(id != null){
			return id;
		}else{
			HashMap<String, Object> properties = new HashMap<>();
			properties.put( "name", name );
			id = inserter.createNode( properties, label );
			tmp = new HashMap<Label,Long>();
			tmp.put(label, id);
			idMap.put(name, tmp);
			nodeCounter++;
			return id;
		}
		
	}
	
	public void run() throws IOException{
		long timeStart = System.nanoTime();
		int lineCounter = 0;
		
		while ((triplet = anchorParser.parseTriplet()) != null) {
			lineCounter++;
			Long source, sink;
			
			source = getId(triplet[0], anchorLabel);
			sink   = getId(triplet[1], entityLabel);
			HashMap<String, Object> frequency = new HashMap<String, Object>();
			frequency.put("frequency", Integer.parseInt(triplet[2]));
			
			inserter.createRelationship( source, sink, RelTypes.ANCHOR_OF, frequency);
			
			String[] splitAnchor = triplet[0].split(" ");
			if (splitAnchor.length > 1) {
				for (String partialAnchor : splitAnchor) {
					if (!stopWords.contains(partialAnchor.toLowerCase())) {
						Long newSource = getId(partialAnchor, partialAnchorLabel);
						inserter.createRelationship( newSource, source, RelTypes.PARTIALMATCH_OF, null);
					}
				}
			}

			if (lineCounter % 100000 == 0)
				System.out.println("	processed lines: " + lineCounter);
		}
		
		System.out.println("Create index and shut down...");
		inserter.createDeferredSchemaIndex( entityLabel ).on( "name" ).create();
		inserter.createDeferredSchemaIndex( anchorLabel ).on( "name" ).create();
		inserter.createDeferredSchemaIndex( partialAnchorLabel ).on( "name" ).create();
		inserter.shutdown();
		
		long timeEnd = System.nanoTime();
		double passedTime = (timeEnd - timeStart) / 60000000000.0;
		System.out.println("Passed time: " + passedTime + " mins");
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
	
	public static void main(String[] args) {
		
//		try {
//			Neo4jBatchInsert inserter = new Neo4jBatchInsert("BatchAnchors",
//															"anchors.txt",
//															"stopwords.txt");
//			inserter.run();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
}
