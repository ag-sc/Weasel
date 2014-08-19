package evaluation;

import java.util.LinkedList;

import datatypes.EntityOccurance;
import datatypes.FragmentPlusCandidates;

public abstract class EvaluationEngine {

	public abstract LinkedList<EntityOccurance> evaluate(LinkedList<FragmentPlusCandidates> fragments);
}
