package evaluation;

import annotatedSentence.AnnotatedSentence;
import annotatedSentence.Fragment;

public class RandomEvaluator extends EvaluationEngine{

	@Override
	public void evaluate(AnnotatedSentence annotatedSentence) {
		for(Fragment f: annotatedSentence.buildFragmentList()){
			int index = (int) Math.floor(f.candidates.size() * Math.random());
			int counter = 0;
			for(String s: f.candidates){
				if(counter == index){
					f.setValue(s);
					f.probability = Math.random();
					break;
				}
				counter++;
			}
		}
	}

}
