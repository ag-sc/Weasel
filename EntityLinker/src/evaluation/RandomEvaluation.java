package evaluation;

import databaseConnectors.DatabaseConnector;
import annotatedSentence.AnnotatedSentence;
import annotatedSentence.Candidate;
import annotatedSentence.Fragment;

public class RandomEvaluation extends EvaluationEngine{
	
	DatabaseConnector dbConnector;

	public RandomEvaluation(DatabaseConnector dbConnector) {
		this.dbConnector = dbConnector;
	}

	@Override
	public void evaluate(AnnotatedSentence annotatedSentence) {
		for(Fragment f: annotatedSentence.getFragmentList()){
			int index = (int) Math.floor(f.getCandidatesSize() * Math.random());
			int counter = 0;
			for(Candidate c: f.getCandidates()){
				
				if(counter == index){
					String entity = dbConnector.resolveID(c.getEntity().split("_")[0]);
					f.setEntity(entity);
					f.probability = Math.random();
					break;
				}
				counter++;
			}
		}
	}

}
