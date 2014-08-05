import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;

import org.junit.Test;


public class AnchorMapGeneratorTest {

	@Test
	public void generateURIKeyMaptest() {
		HashMap<String, LinkedList<String>> URIKeyMap;
		try{
			URIKeyMap = AnchorMapGenerator.generateURIKeyMap("data/anchorMapGenerator_test.txt");
			
			assertEquals("Number of key-value pairs is 5.", 5, URIKeyMap.size());
			assertEquals("key 'link02' has 3 entries.", 3, URIKeyMap.get("link02").size());
			
			AnchorMapGenerator.saveURIKeyMapToFile(URIKeyMap, "data/URIKeyMapSave.ukm");
			URIKeyMap = AnchorMapGenerator.loadURIKeyMapFromFile("data/URIKeyMapSave.ukm");
			
			assertEquals("Number of key-value pairs is 5 in loaded map.", 5, URIKeyMap.size());
			assertEquals("key 'link02' has 3 entries in loaded map.", 3, URIKeyMap.get("link02").size());
			
		}catch(FileNotFoundException e){
			fail("File not found exception");
			e.printStackTrace();
		}catch(IOException e){
			fail("IO exception");
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			fail("ClassNotFoundException");
			e.printStackTrace();
		}
	}

}
