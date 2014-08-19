import java.util.LinkedList;

import databaseConnectors.DatabaseConnector;
import datatypes.EntityOccurance;
import datatypes.FragmentPlusCandidates;
import evaluation.EvaluationEngine;


public class EntityLinker {

	private EvaluationEngine evaluator;
	private DatabaseConnector connector;
	
	public EntityLinker(EvaluationEngine evaluator, DatabaseConnector connector) {
		this.evaluator = evaluator;
		this.connector = connector;
	}
	
	private LinkedList<EntityOccurance> createFragments(String sentence){
		LinkedList<EntityOccurance> fragments = new LinkedList<EntityOccurance>();
		
		//TODO: implement sophisticated version
		for(String s: sentence.replace(",", "").replace(".", "").split(" ")){
			int start = sentence.indexOf(s);
			fragments.add(new EntityOccurance(s, start, start + s.length()));
		}
		
		return fragments;
	}
	
	private LinkedList<FragmentPlusCandidates> findAllCandidats(LinkedList<EntityOccurance> fragments){
		LinkedList<FragmentPlusCandidates> allCandidats = new LinkedList<FragmentPlusCandidates>();
		
		for(EntityOccurance eo: fragments){
			LinkedList<String> candidats = connector.lookUpFragment(eo.getFragment());
			if(candidats.size() > 0) allCandidats.addLast(new FragmentPlusCandidates(eo, candidats));
		}
		
		return allCandidats;
	}

	public LinkedList<EntityOccurance> link(String sentence) {
		LinkedList<EntityOccurance> fragments = createFragments(sentence);
		LinkedList<FragmentPlusCandidates> allCandidats = findAllCandidats(fragments);
		
		LinkedList<EntityOccurance> resultingEntities = evaluator.evaluate(allCandidats);
		
		// TODO Auto-generated method stub
		return resultingEntities;
	}

}
