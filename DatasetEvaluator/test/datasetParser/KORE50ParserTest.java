package datasetParser;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import databaseConnectors.DatabaseConnector;
import datatypes.AnnotatedSentence;
import evaluation.RandomEvaluator;

public class KORE50ParserTest {

	BufferedReader br;
	
	@Before
	public void setup(){
		br = EasyMock.createMock(BufferedReader.class);
		try {
			EasyMock.expect(br.readLine()).andReturn("-DOCSTART- (6 CEL06)");
			EasyMock.expect(br.readLine()).andReturn("David	B	David	David_Beckham");
			EasyMock.expect(br.readLine()).andReturn("and");
			EasyMock.expect(br.readLine()).andReturn("Victoria	B	Victoria	Victoria_Beckham");
			EasyMock.expect(br.readLine()).andReturn("added");
			EasyMock.expect(br.readLine()).andReturn("spice");
			EasyMock.expect(br.readLine()).andReturn(".");
			EasyMock.expect(br.readLine()).andReturn(null);
		} catch (IOException e) {
			e.printStackTrace();
			fail("test setup failed");
		}
		EasyMock.replay(br);
	}
	
	@Test
	public void test() {
		try {
			KORE50Parser parser = new KORE50Parser(br);
			AnnotatedSentence sentence = parser.parse();
			assertEquals("Sentence correct", "David and Victoria added spice", sentence.getSentence());
			assertEquals("David = David_Beckham", "David", sentence.getToken(0));
			assertEquals("David = David_Beckham", "David_Beckham", sentence.getEntity(0));
			assertEquals("length = 5", 5, sentence.length());
			
		} catch (IOException e) {
			e.printStackTrace();
			fail("IOException");
		}
	}

}
