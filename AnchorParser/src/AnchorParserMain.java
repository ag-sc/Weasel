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
			AnchorMapGenerator.saveMapToTextFile(anchorMap, "data/anchors_ukm.txt");
			
			//anchorMap = AnchorMapGenerator.loadURIKeyMapFromFile("data/anchors.ukm");
			//System.out.println("Object generated, looking up...");
			//LinkedList<TermFrequency> tmp = anchorMap.get("http://dbpedia.org/resource/Lisbon_Treaty");
			//System.out.println(tmp);
			
			
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
