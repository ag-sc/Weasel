package evaluation;

import annotatedSentence.AnnotatedSentence;
import annotatedSentence.Candidate;
import annotatedSentence.Fragment;

public class RandomEvaluator extends EvaluationEngine{

	@Override
	public void evaluate(AnnotatedSentence annotatedSentence) {
		for(Fragment f: annotatedSentence.buildFragmentList()){
			int index = (int) Math.floor(f.getCandidatesSize() * Math.random());
			int counter = 0;
			for(Candidate c: f.getCandidates()){
				if(counter == index){
					f.setID(c.word);
					f.probability = Math.random();
					break;
				}
				counter++;
			}
		}
	}

}
