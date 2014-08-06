import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;

import datatypes.TermFrequency;


public class AnchorParserMain {

	public static void main(String[] args) {
		System.out.println("Hello World!");

		try{
			HashMap<String, LinkedList<TermFrequency>> anchorMap = new HashMap<String, LinkedList<TermFrequency>>();
			
			
			anchorMap = AnchorMapGenerator.generateURIKeyMap("data/anchors.txt");
			System.out.println("AnchorMap generated, writing to disk...");
			AnchorMapGenerator.saveMapToTextFile(anchorMap, "data/1_URIKeyMap.txt");
			
			//anchorMap = AnchorMapGenerator.loadURIKeyMapFromFile("data/anchors.ukm");
			//System.out.println("Object generated, looking up...");
			//LinkedList<TermFrequency> tmp = anchorMap.get("http://dbpedia.org/resource/Lisbon_Treaty");
			//System.out.println(tmp);
			
			anchorMap = null;
			System.gc();
			
			System.out.println("Generate AnchorKeyMap text file...");
			anchorMap = AnchorMapGenerator.generateAnchorKeyMapFromURIKeyMapTextFile("data/1_URIKeyMap.txt");
			AnchorMapGenerator.saveMapToTextFile(anchorMap, "data/2_AnchorKeyMap.txt");
			anchorMap = null;
			System.gc();
			
			System.out.println("Generate PartialAnchorKeyMap text file...");
			HashMap<String, LinkedList<String>> partialAnchorKeyMap;
			partialAnchorKeyMap = AnchorMapGenerator.generatePartialAnchorKeyMapFromAnchorKeyMapTextFile("data/2_AnchorKeyMap.txt");
			System.out.println("partialAnchorKeyMap generated, writing to disk...");
			AnchorMapGenerator.savePartialKeyMapToTextFile(partialAnchorKeyMap, "data/3_PartialAnchorKeyMap.txt");
			
			
		}catch(FileNotFoundException e){
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//catch (ClassNotFoundException e) {
		//	e.printStackTrace();
		//}
		
		System.out.println("All done :)");
	}

}
