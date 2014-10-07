package entityLinker;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.TreeSet;

import stopwatch.Stopwatch;
import annotatedSentence.AnnotatedSentence;
import annotatedSentence.Fragment;
import databaseConnectors.DatabaseConnector;
import datatypes.AnnotatedSentenceDeprecated;
import datatypes.EntityOccurance;
import datatypes.FragmentPlusCandidates;
import evaluation.EvaluationEngine;
import fileparser.StopWordParser;


public class EntityLinker {

	private EvaluationEngine evaluator;
	private DatabaseConnector anchors;
	private DatabaseConnector partialAnchors;
	private TreeSet<String> stopWords;
	
	public EntityLinker(EvaluationEngine evaluator, DatabaseConnector connector, DatabaseConnector partialAnchors, String stopWordsTextFile) {
		this.evaluator = evaluator;
		this.anchors = connector;
		this.partialAnchors = partialAnchors;
		if(stopWordsTextFile != null) {
			try {
				stopWords = StopWordParser.parseStopwords("../../data/stopwords.txt");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public EntityLinker(EvaluationEngine evaluator, DatabaseConnector connector, DatabaseConnector partialAnchors) {
		this(evaluator, connector, partialAnchors, null);
	}
	
//	private LinkedList<EntityOccurance> createFragments(String sentence){
//		LinkedList<EntityOccurance> fragments = new LinkedList<EntityOccurance>();
//		
//		//TODO: implement sophisticated version
//		//TODO: fix string index
//		for(String s: sentence.replace(",", "").replace(".", "").split(" ")){
//			int start = sentence.indexOf(s);
//			fragments.add(new EntityOccurance(s, start, start + s.length()));
//		}
//		
//		return fragments;
//	}
	
//	private HashMap<String, LinkedList<String>> findAllCandidats(LinkedList<EntityOccurance> fragments){
//		HashMap<String, LinkedList<String>> allCandidats = new HashMap<String, LinkedList<String>>();
//		
//		for(EntityOccurance eo: fragments){
//			LinkedList<String> candidats = anchors.getFragmentTargets(eo.getFragment());
//			if(candidats.size() > 0) allCandidats.put(eo.getFragment(), candidats);
//		}
//		
//		return allCandidats;
//	}

//	public HashMap<String, LinkedList<String>> getFragmentPlusCandidates(String sentence) {
//		//LinkedList<EntityOccurance> fragments = createFragments(sentence);
//		String splitSentence[] = sentence.replace(",", "").replace(".", "").split(" ");
//		for(int i = 0; i < splitSentence.length; i++){
//			LinkedList<String> candidats = partialAnchors.getFragmentTargets(splitSentence[i]);
//		}
//		
//		
//		HashMap<String, LinkedList<String>> allCandidats = findAllCandidats(fragments);
//		//System.out.println(allCandidats);
//		return allCandidats;
//	}
	
	public AnnotatedSentence getFragmentedSentence(String sentence) {
		String splitSentence[] = sentence.replace(",", "").replace(".", "").split(" ");
		AnnotatedSentence as = new AnnotatedSentence(splitSentence);
		Stopwatch sw = new Stopwatch(Stopwatch.UNIT.MILLISECONDS);
		
		sw.start();
		for (int i = 0; i < splitSentence.length; i++) {
			if (stopWords == null || !stopWords.contains(splitSentence[i])) {
				Fragment f = new Fragment(i, i);
				f.candidates.addAll(anchors.getFragmentTargets(splitSentence[i]));
				if(f.candidates.isEmpty()) continue;
				as.addFragment(f);
			}
		}
		
		System.out.println("Added all anchor candidates - Time: "+sw.stop() + " s");
		sw.start();

		for (int i = 0; i < splitSentence.length; i++) {
			if (stopWords == null || !stopWords.contains(splitSentence[i])) {
				String fragment = splitSentence[i];
				LinkedList<String> candidats = partialAnchors.getFragmentTargets(fragment);
				for (String candidat : candidats) {
					String splitCandidat[] = candidat.split(" ");
					int candidatIndex = -1;
					for (int j = 0; j < splitCandidat.length; j++) {
						if (splitCandidat[j].equals(fragment)) {
							candidatIndex = j;
							break;
						}
					}
					assert (candidatIndex >= 0);
					boolean validCandidate = true;
					for (int j = 0; j < splitCandidat.length; j++) {
						int tmpIndex = i - candidatIndex + j;
						if (tmpIndex < 0 || tmpIndex >= splitSentence.length || !splitSentence[tmpIndex].equals(splitCandidat[j])) {
							validCandidate = false;
							break;
						}
					}
					if (validCandidate) {
						Fragment f = new Fragment(i - candidatIndex, i - candidatIndex + splitCandidat.length - 1);
						f.candidates.addAll(anchors.getFragmentTargets(candidat));
						as.addFragment(f);
					}				
				}
			}
		}
		
		System.out.println("Added all partialAnchor candidates - Time: "+sw.stop() + " s");
		return as;
	}
	
	public AnnotatedSentence link(String sentence) {
		AnnotatedSentence as = getFragmentedSentence(sentence);
		evaluator.evaluate(as);
		return as;
	}
	
	public void closeConnectors(){
		anchors.close();
		partialAnchors.close();
	}

}
