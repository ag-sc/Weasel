package evaluation;

import java.util.HashMap;
import java.util.LinkedList;

import datatypes.AnnotatedSentenceDeprecated;
import datatypes.EntityOccurance;
import datatypes.FragmentPlusCandidates;

public class RandomEvaluator extends EvaluationEngine{

	@Override
	public void evaluate(HashMap<String, LinkedList<String>> fragments, AnnotatedSentenceDeprecated annotatedSentence) {
		for(int i = 0; i < annotatedSentence.length(); i++){
			LinkedList<String> candidates = fragments.get(annotatedSentence.getToken(i));
			if(candidates != null){
				int index = (int)Math.floor(Math.random() * (double)candidates.size());
				annotatedSentence.setEntity(i, candidates.get(index));
			}
		}
	}

}
