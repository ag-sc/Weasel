import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;

import jdbm.PrimaryHashMap;
import jdbm.PrimaryStoreMap;
import jdbm.RecordManager;
import jdbm.RecordManagerFactory;
import datatypes.TermFrequency;

public class AnchorParserMain {

	public static void main(String[] args) {
		System.out.println("Hello World!");

		try{
			
			HashMap<String, LinkedList<TermFrequency>> anchorMap = new HashMap<String, LinkedList<TermFrequency>>();
			
			
			anchorMap = AnchorMapGenerator.generateURIKeyMap("../../data/Wikipedia Anchor/anchors.txt");
			
			System.out.println("AnchorMap generated, writing to disk...");
			AnchorMapGenerator.saveMapToTextFile(anchorMap, "../../data/Wikipedia Anchor/1_URIKeyMap.txt");
			/*
			AnchorMapGenerator.saveMapToJDMB(anchorMap, "../../data/Wikipedia Anchor/db/db_01", "uriKeyMap");
			*/
			//anchorMap = AnchorMapGenerator.loadURIKeyMapFromFile("../../data/Wikipedia Anchor/anchors.ukm");
			//System.out.println("Object generated, looking up...");
			//LinkedList<TermFrequency> tmp = anchorMap.get("http://dbpedia.org/resource/Lisbon_Treaty");
			//System.out.println(tmp);
			
			//anchorMap = null;
			//System.gc();
			
			System.out.println("Generate AnchorKeyMap from text file...");
			anchorMap = AnchorMapGenerator.generateAnchorKeyMapFromURIKeyMapTextFile("../../data/Wikipedia Anchor/1_URIKeyMap.txt");
			AnchorMapGenerator.saveMapToJDMB(anchorMap, "../../data/Wikipedia Anchor/db/anchorKeyMap", "anchorKeyMap");
			
			int counter = 0;
			for(Entry<String, LinkedList<TermFrequency>> e: anchorMap.entrySet()){
				System.out.println(counter++ + ": " + e.getKey());
				System.out.println("/t" + e.getValue());
				if(counter > 25) break;
			}
			
			AnchorMapGenerator.saveMapToTextFile(anchorMap, "../../data/Wikipedia Anchor/2_AnchorKeyMap.txt");
//			anchorMap = null;
//			System.gc();
			
			
			/*
			System.out.println("Generate PartialAnchorKeyMap text file...");
			
//			RecordManager recman = RecordManagerFactory.createRecordManager("../../data/Wikipedia Anchor/db/db_01");
//			PrimaryHashMap<String, LinkedList<String>> dbMap = recman.hashMap("partialAnchorKeyMap");
//			
//			LinkedList<String> list = dbMap.get("of");
//			if(list != null) System.out.println(list.size());
//			
//			recman.close();
			
			HashMap<String, LinkedList<String>> partialAnchorKeyMap;
			partialAnchorKeyMap = AnchorMapGenerator.generatePartialAnchorKeyMapFromAnchorKeyMapTextFile("../../data/Wikipedia Anchor/2_AnchorKeyMap.txt");
			
			AnchorMapGenerator.saveMapToJDMB(partialAnchorKeyMap, "../../data/Wikipedia Anchor/db/db_01", "partialAnchorKeyMap");
			
			System.out.println("partialAnchorKeyMap generated, writing to disk...");
			AnchorMapGenerator.savePartialKeyMapToTextFile(partialAnchorKeyMap, "../../data/Wikipedia Anchor/3_PartialAnchorKeyMap.txt");
			*/
			// ----------------------------------------------------------
			
//			System.out.println("Writing partialAnchorKeyMap to DB...");
//			recman = RecordManagerFactory.createRecordManager("data/jdbm/partialMap");
//			
//			PrimaryHashMap<String, LinkedList<String>> partialAnchorMap = recman.hashMap("partialAnchorMap");
//			
//			
//			counter = 0;
//			for(Entry<String, LinkedList<String>> entry: partialAnchorKeyMap.entrySet()){
//				partialAnchorMap.put(entry.getKey(), entry.getValue());
//				if(counter++ % 100000 == 0){
//					System.out.println(counter-1);
//					recman.commit();
//				}
//			}
//			recman.commit();
//			
//			
//			System.out.println(partialAnchorMap.get("lisbon"));
//			
//			
//			recman.close();
			
		}catch(FileNotFoundException e){
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
//		catch (ClassNotFoundException e) {
//			e.printStackTrace();
//		}
		
		System.out.println("All done :)");
	}

}
