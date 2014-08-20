package datasetParser;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

public class KORE50ParserTest {

	@Test
	public void test() {
		try {
			KORE50Parser parser = new KORE50Parser(new File(
					"../../data/DatasetParser/parserTests/kore50.tsv"));
			assertNull("No current sentence", parser.getSentence());
			assertTrue("Parser can find next entry", parser.goToNext());
			assertTrue(
					"Get correct sentence",
					parser.getSentence()
							.equals(
									"David and Victoria named their children Brooklyn, Romeo, Cruz, and Harper Seven."));
			assertEquals("Two entities", 2, parser.getEntities().size());
			assertTrue("Parser can find next entry", parser.goToNext());
			assertFalse("Parser can not find next entry", parser.goToNext());
		} catch (IOException e) {
			e.printStackTrace();
			fail("IOException");
		}
	}

}
