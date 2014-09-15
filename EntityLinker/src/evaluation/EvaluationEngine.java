package evaluation;

import java.util.HashMap;
import java.util.LinkedList;

import datatypes.AnnotatedSentenceDeprecated;
import datatypes.EntityOccurance;
import datatypes.FragmentPlusCandidates;

public abstract class EvaluationEngine {

	public abstract void evaluate(HashMap<String, LinkedList<String>> fragments, AnnotatedSentenceDeprecated annotatedSentence);
}
