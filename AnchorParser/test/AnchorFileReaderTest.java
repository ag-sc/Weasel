import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Test;

import fileparser.AnchorFileParser;


public class AnchorFileReaderTest {
	
	@Test
	public void testGetTriplet() {
		try{
			AnchorFileParser reader = new AnchorFileParser("data/testFiles/anchors_test.txt");
			String[] output = reader.parseTuple();
			assertEquals("Retur valid tuple", 2, output.length);
			output = reader.parseTuple();
			assertEquals("Retur valid tuple", 2, output.length);
			output = reader.parseTuple();
			assertEquals("Retur valid tuple", 2, output.length);
			output = reader.parseTuple();
			assertEquals("Retur valid tuple", 2, output.length);
			output = reader.parseTuple();
			assertEquals("Retur valid tuple", 2, output.length);
			output = reader.parseTuple();
			assertEquals("Retur valid tuple", 2, output.length);
			output = reader.parseTuple();
			assertEquals("Retur valid tuple", 2, output.length);
			output = reader.parseTuple();
			assertEquals("Retur valid tuple", 2, output.length);
			output = reader.parseTuple();
			assertNull("Last line was empty", output);
		}catch(IOException e){
			e.printStackTrace();
			fail("IOexception");
		}
	}
	

}
