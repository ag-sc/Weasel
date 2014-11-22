package entityLinker;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.TreeSet;

import stopwatch.Stopwatch;
import annotatedSentence.AnnotatedSentence;
import annotatedSentence.Fragment;
import databaseConnectors.DatabaseConnector;
import databaseConnectors.H2Connector;
import datatypes.AnnotatedSentenceDeprecated;
import datatypes.EntityOccurance;
import datatypes.FragmentPlusCandidates;
import datatypes.H2List;
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
				stopWords = StopWordParser.parseStopwords(stopWordsTextFile);
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
	
	public AnnotatedSentence getFragmentedSentence2(String sentence) {
		String splitSentence[] = sentence.replace(",", "").replace(".", "").split(" ");
		AnnotatedSentence as = new AnnotatedSentence(splitSentence);
		Stopwatch sw = new Stopwatch(Stopwatch.UNIT.MILLISECONDS);
		
		for(int i = 0; i < splitSentence.length; i++){
			if (stopWords != null && stopWords.contains(splitSentence[i])) continue;
			
			LinkedList<String> candidates = new LinkedList<String>();;
			LinkedList<String> tmpList = new LinkedList<String>();
			String word = "";
			String tmpWord = "";
			int j = i;
			while(j < splitSentence.length){
				tmpWord = (tmpWord + " " + splitSentence[j]).trim();
				tmpList = anchors.getFragmentTargets(tmpWord);
				
				if(tmpList.size() > 0){
					candidates = tmpList;
					word = tmpWord;
				}else break;
				
				j++;
			}
			
			j -= 1;
			Fragment f = new Fragment(i, j);
			f.candidates.addAll(candidates);
			if(f.candidates.isEmpty()) continue;
			f.originWord = word;
			as.addFragment(f);
			i = j;
		}
		
		System.out.println("Added all anchor candidates - Time: " + sw.stop() + " ms");
		return as;
	}
	
	public AnnotatedSentence getFragmentedSentence(String sentence) {
		String splitSentence[] = sentence.replace(",", "").replace(".", "").split(" ");
		AnnotatedSentence as = new AnnotatedSentence(splitSentence);
		Stopwatch sw = new Stopwatch(Stopwatch.UNIT.MILLISECONDS);
				
		Integer array[] = {1,2,4,5,6,7,8,11};
		TreeSet<Integer> coveredWords = new TreeSet<Integer>();
		//for(Integer i: array) coveredWords.add(i);
		
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
						if (tmpIndex < 0 || tmpIndex >= splitSentence.length || !splitSentence[tmpIndex].equals(splitCandidat[j])  || coveredWords.contains(tmpIndex)) { // TODO: remove second condition, exists for debug purposes
							validCandidate = false;
							break;
						}
					}
					if (validCandidate) {
						Fragment f = new Fragment(i - candidatIndex, i - candidatIndex + splitCandidat.length - 1);
						f.candidates.addAll(anchors.getFragmentTargets(candidat));
						f.originWord = splitSentence[i];
						as.addFragment(f);
						for(int j = f.start; j <= f.stop; j++){
							coveredWords.add(j);
						}
					}				
				}
			}
		}
		System.out.println("Added all partialAnchor candidates - Time: "+sw.stop() + " ms");
		
		sw.start();
		for (int i = 0; i < splitSentence.length; i++) {
			if (stopWords == null || !stopWords.contains(splitSentence[i]) && !coveredWords.contains(i)) {
				Fragment f = new Fragment(i, i);
				f.candidates.addAll(anchors.getFragmentTargets(splitSentence[i]));
				if(f.candidates.isEmpty()) continue;
				f.originWord = splitSentence[i];
				as.addFragment(f);
			}
		}
		
		System.out.println("Added all anchor candidates - Time: "+sw.stop() + " ms");
		
		
		return as;
	}
	
	public AnnotatedSentence link(String sentence) {
		AnnotatedSentence as = getFragmentedSentence2(sentence);
		evaluator.evaluate(as);
		return as;
	}
	
	public void closeConnectors(){
		anchors.close();
		partialAnchors.close();
	}

}
