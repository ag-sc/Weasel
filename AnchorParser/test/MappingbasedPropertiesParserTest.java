import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import jdbm.PrimaryHashMap;
import jdbm.RecordManager;
import jdbm.RecordManagerFactory;

import org.junit.Test;

import datatypes.Edge;


public class MappingbasedPropertiesParserTest {

	@Test
	public void generateDBTest() {
		try {
			String testDBPath = "../../data/Mappingbased Properties/test/testDB";
			
			MappingbasedPropertiesParser.generateDB("../../data/Mappingbased Properties/test/mappingbased_properties_cleaned_en.nt",testDBPath);
			
			
			RecordManager recman = RecordManagerFactory.createRecordManager(testDBPath);		
			PrimaryHashMap<String, LinkedList<Edge<String, String>>> tupleMap = recman
					.hashMap("tuples");
			assertEquals("Number of key-value pairs is 2 in loaded map.", 2,
					tupleMap.entrySet().size());
			assertEquals("Number of entries for key 'Aristotle' is 7.", 7,
					tupleMap.get("Aristotle").size());
			assertEquals("Number of entries for key 'Alabama' is 2.", 2,
					tupleMap.get("Alabama").size());
		}catch(FileNotFoundException e){
			e.printStackTrace();
			fail("File not found exception");
		}catch (IOException e) {
			e.printStackTrace();
			fail("IO exception");
		}
	}

}
