package databaseConnectors;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.LinkedList;

import org.junit.Test;

public class JDBMConnectorTest {

	@Test
	public void test() {
		try {
			JDBMConnector connector = new JDBMConnector("../../data/Wikipedia Anchor/db/db_01", "anchorKeyMap");
			
			LinkedList<String> lookup = connector.lookUpFragment("romeo");
			
			for(String s: lookup){
				System.out.println(s);
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("IOException");
		}	
	}

}
