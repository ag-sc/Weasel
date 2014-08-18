import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.easymock.*;

import datasetParser.KORE50Parser;


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
		EasyMock.expect(parser.next()).andReturn(true);
		EasyMock.expect(parser.getSentence()).andReturn("Romeo and Julia were happy.");
		EasyMock.expect(parser.getEntities()).andReturn(new String[]{"Romeo", "Julia"});
		EasyMock.expect(parser.next()).andReturn(false);
		EasyMock.replay(parser);
		
		DatasetEvaluator evaluator = new DatasetEvaluator(parser);
		
		fail("Not yet implemented");
	}

}
