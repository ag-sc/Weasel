package entityLinker;

import org.easymock.EasyMock;
import org.junit.Before;
import databaseConnectors.DatabaseConnector;
import evaluation.EvaluationEngine;
import evaluation.RandomEvaluation;

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
