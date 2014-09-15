import static org.junit.Assert.*;

import java.util.LinkedList;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import databaseConnectors.DatabaseConnector;
import datatypes.EntityOccurance;
import entityLinker.EntityLinker;
import evaluation.EvaluationEngine;
import evaluation.RandomEvaluator;


public class EntityLinkerTest {
	
	EvaluationEngine evaluator;
	DatabaseConnector connector;

	@Before
	public void setup(){
		//evaluator = EasyMock.createMock(EvaluationEngine.class);
		evaluator = new RandomEvaluator();
		connector = EasyMock.createMock(DatabaseConnector.class);
	}
	
	@Test
	public void linkTest() {
		LinkedList<String> romeoList = new LinkedList<String>();
		romeoList.add("Romeo");
		romeoList.add("Romeo Must Die");
		LinkedList<String> juliaList = new LinkedList<String>();
		juliaList.add("Julia");
		juliaList.add("Julia Roberts");
		
		EasyMock.expect(connector.getFragmentTargets("Romeo")).andReturn(romeoList);
		EasyMock.expect(connector.getFragmentTargets("and")).andReturn(new LinkedList<String>());
		EasyMock.expect(connector.getFragmentTargets("Julia")).andReturn(juliaList);
		EasyMock.expect(connector.getFragmentTargets("are")).andReturn(new LinkedList<String>());
		EasyMock.expect(connector.getFragmentTargets("happy")).andReturn(new LinkedList<String>());
		EasyMock.replay(connector);
		
		EntityLinker linker = new EntityLinker(evaluator, connector);
		LinkedList<EntityOccurance> entityList = linker.link("Romeo and Julia are happy.");
		assertEquals("Two candidates found.", 2, entityList.size());
		
		
		for(EntityOccurance eo: entityList) System.out.println(eo);
		
		//fail("Not yet implemented");
	}

}
