import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;

import org.junit.Test;

import datatypes.TermFrequency;


public class AnchorMapGeneratorTest {

	@Test
	public void generateURIKeyMaptest() {
		HashMap<String, LinkedList<TermFrequency>> URIKeyMap;
		try{
			URIKeyMap = AnchorMapGenerator.generateURIKeyMap("data/testFiles/anchors_test.txt");
			
			assertEquals("Number of key-value pairs is 5.", 5, URIKeyMap.size());
			assertEquals("key 'uri2' has 3 entries.", 3, URIKeyMap.get("uri2").size());
			
			AnchorMapGenerator.saveURIKeyMapToFile(URIKeyMap, "data/testFiles/URIKeyMapSave.ukm");
			URIKeyMap = AnchorMapGenerator.loadURIKeyMapFromFile("data/testFiles/URIKeyMapSave.ukm");
			
			assertEquals("Number of key-value pairs is 5 in loaded map.", 5, URIKeyMap.size());
			assertEquals("key 'uri2' has 3 entries in loaded map.", 3, URIKeyMap.get("uri2").size());
			
			AnchorMapGenerator.saveMapToTextFile(URIKeyMap, "data/testFiles/URIKeyMapSave.txt");
			
		}catch(FileNotFoundException e){
			e.printStackTrace();
			fail("File not found exception");
		}catch(IOException e){
			e.printStackTrace();
			fail("IO exception");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			fail("ClassNotFoundException");
		}
	}
	
	@Test
	public void generateAnchorKeyMaptest() {
		HashMap<String, LinkedList<TermFrequency>> AnchorKeyMap;
		try{
			AnchorKeyMap = AnchorMapGenerator.generateAnchorKeyMapFromURIKeyMapTextFile("data/testFiles/URIKeyMapSave.txt");
			
			assertEquals("Number of key-value pairs is 7 in loaded map.", 7, AnchorKeyMap.size());
			assertEquals("key 'anchor3' has 2 entries in loaded map.", 2, AnchorKeyMap.get("anchor3").size());
			
			AnchorMapGenerator.saveMapToTextFile(AnchorKeyMap, "data/testFiles/AnchorKeyMapSave.txt");
			
		}catch(FileNotFoundException e){
			e.printStackTrace();
			fail("File not found exception");
		}catch(IOException e){
			e.printStackTrace();
			fail("IO exception");
		}
	}
}
