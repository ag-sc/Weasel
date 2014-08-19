package evaluation;

import java.util.LinkedList;

import datatypes.EntityOccurance;
import datatypes.FragmentPlusCandidates;

public class RandomEvaluator extends EvaluationEngine{

	@Override
	public LinkedList<EntityOccurance> evaluate(
			LinkedList<FragmentPlusCandidates> fragments) {
		LinkedList<EntityOccurance> entities = new LinkedList<EntityOccurance>();
		
		for(FragmentPlusCandidates fpc: fragments){
			int index = (int)Math.floor(Math.random() * (double)fpc.candidates.size());
			String entityName = fpc.candidates.get(index);
			EntityOccurance tmp = new EntityOccurance(fpc.fragment);
			tmp.setName(entityName);
			entities.add(tmp);
		}
		
		return entities;
	}

}
