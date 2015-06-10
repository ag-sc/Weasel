package entityLinker;
import static org.junit.Assert.*;

import java.util.LinkedList;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import datatypes.EntityOccurance;
import datatypes.databaseConnectors.DatabaseConnector;
import entityLinker.EntityLinker;
import entityLinker.evaluation.EvaluationEngine;
import entityLinker.evaluation.RandomEvaluation;


public class EntityLinkerTest extends EntityLinkerBaseTest {

	
	@Test
	public void linkTest() {
		evaluator = new RandomEvaluation();
		anchors = EasyMock.createMock(DatabaseConnector.class);
		partialAnchors = EasyMock.createMock(DatabaseConnector.class);
		
		LinkedList<String> romeoList = new LinkedList<String>();
		romeoList.add("Romeo");
		romeoList.add("Romeo Must Die");
		LinkedList<String> juliaList = new LinkedList<String>();
		juliaList.add("Julia");
		juliaList.add("Julia Roberts");
		
		EasyMock.expect(anchors.getFragmentTargets("Romeo")).andReturn(romeoList);
		EasyMock.expect(anchors.getFragmentTargets("and")).andReturn(new LinkedList<String>());
		EasyMock.expect(anchors.getFragmentTargets("Julia")).andReturn(juliaList);
		EasyMock.expect(anchors.getFragmentTargets("are")).andReturn(new LinkedList<String>());
		EasyMock.expect(anchors.getFragmentTargets("happy")).andReturn(new LinkedList<String>());
		EasyMock.replay(anchors);
		
		EntityLinker linker = new EntityLinker(evaluator, anchors);
		LinkedList<EntityOccurance> entityList = linker.link("Romeo and Julia are happy.");
		assertEquals("Two candidates found.", 2, entityList.size());
		
		
		for(EntityOccurance eo: entityList) System.out.println(eo);
		
		//fail("Not yet implemented");
	}

}
