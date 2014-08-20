import static org.junit.Assert.*;

import java.io.IOException;
import java.util.LinkedList;

import org.junit.Before;
import org.junit.Test;
import org.easymock.*;

import datasetParser.KORE50Parser;
import entityLinker.EntityLinker;


public class DatasetEvaluatorTest {

	private KORE50Parser parser;
	private EntityLinker linker;
	
	@Before
	public void setup(){
		parser = EasyMock.createMock(KORE50Parser.class);
		linker = EasyMock.createMock(EntityLinker.class);
	}
	
	@Test
	public void test() {
		try {
			LinkedList<String> entityList = new LinkedList<String>();
			entityList.add("Romeo");
			entityList.add("Julia");
			EasyMock.expect(parser.goToNext()).andReturn(true);
			EasyMock.expect(parser.getSentence()).andReturn(
					"Romeo and Julia were happy.");
			EasyMock.expect(parser.getEntities()).andReturn(entityList);
			EasyMock.expect(parser.goToNext()).andReturn(false);
			EasyMock.replay(parser);
			
			// ------- Mocking done ---------------
			
			//DatasetEvaluator evaluator = new DatasetEvaluator(parser, linker);
			fail("Not yet implemented");
		} catch (IOException e) {
			e.printStackTrace();
			fail("IOException");
		}
	}

}
