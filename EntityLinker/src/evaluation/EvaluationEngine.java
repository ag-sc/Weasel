package evaluation;

import java.util.HashMap;
import java.util.LinkedList;

import datatypes.AnnotatedSentence;
import datatypes.EntityOccurance;
import datatypes.FragmentPlusCandidates;

public abstract class EvaluationEngine {

	public abstract void evaluate(HashMap<String, LinkedList<String>> fragments, AnnotatedSentence annotatedSentence);
}
