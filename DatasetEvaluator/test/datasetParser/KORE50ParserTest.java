package datasetParser;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;

public class KORE50ParserTest {

	@Test
	public void test() {
		KORE50Parser parser = new KORE50Parser(new File("../data/DatasetParser/parserTests"));
		assertNull("No current sentence", parser.getSentence());
		assertTrue("Parser can find next entry", parser.next());
		assertEquals("Get correct sentence", 0, parser.getSentence().compareTo("David and Victoria named their children Brooklyn, Romeo, Cruz, and Harper Seven."));
		assertEquals("Two entities", 2, parser.getEntities().length);
		assertTrue("Parser can find next entry", parser.next());
		
		assertFalse("Parser can not find next entry", parser.next());
	}

}
