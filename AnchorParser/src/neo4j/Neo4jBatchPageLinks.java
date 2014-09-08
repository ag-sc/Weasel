package neo4j;

import java.io.IOException;
import java.util.HashMap;
import java.util.TreeSet;

import neo4j.Neo4jCore.RelTypes;
import fileparser.WikiParser;

public class Neo4jBatchPageLinks extends Neo4jBatchInsert {

	private WikiParser parser;
	
	public Neo4jBatchPageLinks(String dbPath, String pageLinksFilePath) throws IOException {
		super(dbPath, pageLinksFilePath);
	}

	@Override
	protected void assignParser(String pageLinksFilePath) throws IOException{
		parser = new WikiParser(pageLinksFilePath);
	}
	
	@Override
	public void run() throws IOException{
		long timeStart = System.nanoTime();
		int lineCounter = 0;
		String tuple[];
		int relationsCounter = 0;
		
		TreeSet<Long> set = new TreeSet<Long>();
		long lastSeen = -1;
		
		long intervalStart = System.nanoTime(), intervalEnd;;
		while ((tuple = parser.parseTuple()) != null) {
			lineCounter++;
			if (tuple.length == 2) {
				Long source, sink;
				source = getId(tuple[0], wikiLinkLabel);
				sink = getId(tuple[1], wikiLinkLabel);
				
				if(lastSeen == -1) lastSeen = source;
				if(source != lastSeen){
					for(Long l: set){
						inserter.createRelationship(source, l, RelTypes.LINK_TO, null);
						relationsCounter++;
					}
					
					set = new TreeSet<Long>();
					lastSeen = source;
					set.add(sink);
				}else{
					set.add(sink);
				}
			}
			
			if (lineCounter % 1000000 == 0){
				intervalEnd = System.nanoTime();
				double passedTime = (intervalEnd - intervalStart) / 60000000000.0;
				System.out.println("processed lines: " + lineCounter + "\ttime since last: " + passedTime + "\tmins");
				intervalStart = System.nanoTime();
			}	
		}
		
		// last remaining one
		for(Long l: set){
			inserter.createRelationship(lastSeen, l, RelTypes.LINK_TO, null);
			relationsCounter++;
		}
		
		System.out.println("Create index and shut down...");
		inserter.createDeferredSchemaIndex( wikiLinkLabel ).on( "name" ).create();
		inserter.shutdown();
		long timeEnd = System.nanoTime();
		double passedTime = (timeEnd - timeStart) / 60000000000.0;
		System.out.println("Passed time: " + passedTime + " mins");
		System.out.println("Nr of nodes: " + nodeCounter + "	 - nr of relations: " + relationsCounter);
	}
	
	public static void main(String[] args) {
		
		try {
			Neo4jBatchPageLinks inserter = new Neo4jBatchPageLinks("../../data/DBs/BatchPageLinksTest",
																"../../data/Wikipedia/Pagelinks/test/page_links_en.nt");
			inserter.run();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
