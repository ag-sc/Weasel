package databaseConnectors;

import static org.junit.Assert.*;
import java.io.IOException;
import java.util.LinkedList;

import jdbm.PrimaryHashMap;
import jdbm.RecordManager;
import jdbm.RecordManagerFactory;

import org.junit.Before;
import org.junit.Test;

import datatypes.TermFrequency;

public class JDBMConnectorTest {

	String dbName = "../../data/Wikipedia Anchor/test/db_01";
	String tableName = "anchorKeyMap";
	
	@Before
	public void setup(){
		RecordManager recman;
		try {
			recman = RecordManagerFactory.createRecordManager(dbName);
			PrimaryHashMap<String, LinkedList<TermFrequency>> dbMap = recman.hashMap(tableName);
		
			LinkedList<TermFrequency> tmp = new LinkedList<TermFrequency>();
			tmp.add(new TermFrequency("Romeo", 10));
			tmp.add(new TermFrequency("Romeo Must Die", 1));
			dbMap.put("romeo", tmp);
			
			tmp = new LinkedList<TermFrequency>();
			tmp.add(new TermFrequency("Julia", 10));
			tmp.add(new TermFrequency("Julia Roberts", 1));
			dbMap.put("julia", tmp);
			
			recman.close();
		} catch (IOException e) {
			e.printStackTrace();
			fail("IOException");
		}
	}
	
	@Test
	public void test() {
		try {
			JDBMConnector connector = new JDBMConnector(dbName, tableName);
			
			LinkedList<String> lookup = connector.lookUpFragment("romeo");
			
			assertTrue("First entry 'Romeo'", lookup.getFirst().equals("Romeo"));
			assertTrue("Last entry 'Romeo Must Die'", lookup.getLast().equals("Romeo Must Die"));
			
		} catch (IOException e) {
			e.printStackTrace();
			fail("IOException");
		}	
	}

}
