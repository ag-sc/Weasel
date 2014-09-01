import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.TreeSet;

import jdbm.PrimaryHashMap;
import jdbm.RecordManager;
import jdbm.RecordManagerFactory;

import org.junit.Test;

import datatypes.TermFrequency;
import fileparser.AnchorFileParser;


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
	
	@Test
	public void dbTest(){
		try {
			AnchorFileParser anchorReader = new AnchorFileParser("data/testFiles/anchors_test.txt");
			AnchorMapGenerator.generateDatabase(anchorReader, "data/jdbm/testDB");
			
			RecordManager recman = RecordManagerFactory.createRecordManager("data/jdbm/testDB");
			
			PrimaryHashMap<String, TreeSet<TermFrequency>> uriMap = recman.hashMap("uri");
			assertEquals("Number of key-value pairs is 5 in loaded map.", 5, uriMap.entrySet().size());
			assertEquals("Number of entries for key 'uri4' is 2.", 2, uriMap.get("uri4").size());
			
			PrimaryHashMap<String, TreeSet<TermFrequency>> anchorMap = recman.hashMap("anchor");
			assertEquals("Number of key-value pairs is 7 in loaded map.", 7, anchorMap.entrySet().size());
			assertEquals("Number of entries for key 'anch or3' is 2.", 2, anchorMap.get("anch or3").size());
			
			PrimaryHashMap<String, TreeSet<TermFrequency>> partialAnchorMap = recman.hashMap("partial");
			assertEquals("Number of key-value pairs is 3 in loaded map.", 3, partialAnchorMap.entrySet().size());
			assertEquals("Number of entries for key 'anch' is 2.", 2, partialAnchorMap.get("anch").size());
			assertEquals("First entry for 'anch' is 'anch or2_2'", "anch or2_2", partialAnchorMap.get("anch").first());
			
		}catch(FileNotFoundException e){
			e.printStackTrace();
			fail("File not found exception");
		} catch (IOException e) {
			e.printStackTrace();
			fail("IO exception");
		}
	}
}















