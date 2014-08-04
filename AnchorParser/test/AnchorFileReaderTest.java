import static org.junit.Assert.*;

import java.io.FileNotFoundException;

import org.junit.Test;


public class AnchorFileReaderTest {
	
	@Test
	public void testGetTriplet() {
		try{
			AnchorFileReader reader = new AnchorFileReader("data/anchors_test.txt");
			String[] output = reader.getTriplet();
			assertEquals("Retur valid triplets", 3, output.length);
			output = reader.getTriplet();
			assertEquals("Retur valid triplets", 3, output.length);
			output = reader.getTriplet();
			assertEquals("Retur valid triplets", 3, output.length);
			output = reader.getTriplet();
			assertEquals("Retur valid triplets", 3, output.length);
			output = reader.getTriplet();
			assertNull("Last line was empty", output);
		}catch(FileNotFoundException e){
			fail("File not found exception");
		}
	}

}
