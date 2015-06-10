package entityLinker;

import org.easymock.EasyMock;
import org.junit.Before;

import datatypes.databaseConnectors.DatabaseConnector;
import entityLinker.evaluation.EvaluationEngine;
import entityLinker.evaluation.RandomEvaluation;

public class EntityLinkerBaseTest {
	EvaluationEngine evaluator;
	DatabaseConnector anchors, partialAnchors;
	EntityLinker linker;

	@Before
	public void universalSetup(){
		evaluator = new RandomEvaluation();
		anchors = EasyMock.createMock(DatabaseConnector.class);
		partialAnchors = EasyMock.createMock(DatabaseConnector.class);
		linker = new EntityLinker(evaluator, anchors, partialAnchors);
	}

}
