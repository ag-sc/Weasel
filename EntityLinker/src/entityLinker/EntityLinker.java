package entityLinker;
import java.util.HashMap;
import java.util.LinkedList;

import databaseConnectors.DatabaseConnector;
import datatypes.AnnotatedSentence;
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
		//TODO: fix string index
		for(String s: sentence.replace(",", "").replace(".", "").split(" ")){
			int start = sentence.indexOf(s);
			fragments.add(new EntityOccurance(s, start, start + s.length()));
		}
		
		return fragments;
	}
	
	private HashMap<String, LinkedList<String>> findAllCandidats(LinkedList<EntityOccurance> fragments){
		HashMap<String, LinkedList<String>> allCandidats = new HashMap<String, LinkedList<String>>();
		
		for(EntityOccurance eo: fragments){
			LinkedList<String> candidats = connector.getFragmentTargets(eo.getFragment());
			if(candidats.size() > 0) allCandidats.put(eo.getFragment(), candidats);
		}
		
		return allCandidats;
	}

	public HashMap<String, LinkedList<String>> getFragmentPlusCandidates(String sentence) {
		LinkedList<EntityOccurance> fragments = createFragments(sentence);
		HashMap<String, LinkedList<String>> allCandidats = findAllCandidats(fragments);
		//System.out.println(allCandidats);
		return allCandidats;
	}
	
	public void link(HashMap<String, LinkedList<String>> fragmentPlusCandidates, AnnotatedSentence annotatedSentence) {
		evaluator.evaluate(fragmentPlusCandidates, annotatedSentence);
	}

}
