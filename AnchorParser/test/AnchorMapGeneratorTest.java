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
		HashMap<String, LinkedList<TermFrequency>> uriKeyMap;
		try{
			uriKeyMap = AnchorMapGenerator.generateURIKeyMap("data/testFiles/anchors_test.txt");
			
			assertEquals("Number of key-value pairs is 5.", 5, uriKeyMap.size());
			assertEquals("key 'uri2' has 3 entries.", 3, uriKeyMap.get("uri2").size());
			
			AnchorMapGenerator.saveURIKeyMapToFile(uriKeyMap, "data/testFiles/URIKeyMapSave.ukm");
			uriKeyMap = AnchorMapGenerator.loadURIKeyMapFromFile("data/testFiles/URIKeyMapSave.ukm");
			
			assertEquals("Number of key-value pairs is 5 in loaded map.", 5, uriKeyMap.size());
			assertEquals("key 'uri2' has 3 entries in loaded map.", 3, uriKeyMap.get("uri2").size());
			
			AnchorMapGenerator.saveMapToTextFile(uriKeyMap, "data/testFiles/URIKeyMapSave.txt");
			
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
		HashMap<String, LinkedList<TermFrequency>> anchorKeyMap;
		try{
			anchorKeyMap = AnchorMapGenerator.generateAnchorKeyMapFromURIKeyMapTextFile("data/testFiles/URIKeyMapSave.txt");
			
			assertEquals("Number of key-value pairs is 7 in loaded map.", 7, anchorKeyMap.size());
			assertEquals("key 'anch or3' has 2 entries in loaded map.", 2, anchorKeyMap.get("anch or3").size());
			
			AnchorMapGenerator.saveMapToTextFile(anchorKeyMap, "data/testFiles/AnchorKeyMapSave.txt");
			
		}catch(FileNotFoundException e){
			e.printStackTrace();
			fail("File not found exception");
		}catch(IOException e){
			e.printStackTrace();
			fail("IO exception");
		}
	}
	
	@Test
	public void generatePartialAnchorKeyMaptest() {
		HashMap<String, LinkedList<String>> partialAnchorKeyMap;
		try{
			partialAnchorKeyMap = AnchorMapGenerator.generatePartialAnchorKeyMapFromAnchorKeyMapTextFile("data/testFiles/AnchorKeyMapSave.txt");
			
			assertEquals("Number of key-value pairs is 3 in loaded map.", 3, partialAnchorKeyMap.size());
			assertEquals("key 'anch' has 2 entries in loaded map.", 2, partialAnchorKeyMap.get("anch").size());
			
			AnchorMapGenerator.savePartialKeyMapToTextFile(partialAnchorKeyMap, "data/testFiles/PartialAnchorKeyMapSave.txt");
			
		}catch(FileNotFoundException e){
			e.printStackTrace();
			fail("File not found exception");
		}catch(IOException e){
			e.printStackTrace();
			fail("IO exception");
		}
	}
}
