import static org.junit.Assert.*;

import java.util.LinkedList;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import evaluation.EvaluationEngine;


public class EntityLinkerTest {
	
	EvaluationEngine evaluator = null;

	@Before
	public void setup(){
		evaluator = EasyMock.createMock(EvaluationEngine.class);
	}
	
	@Test
	public void linkTest() {

		
		EntityLinker linker = new EntityLinker(evaluator);
		LinkedList<EntityOccurance> entityList = linker.link("Rome and Julia are happy.");
		
		fail("Not yet implemented");
	}

}
