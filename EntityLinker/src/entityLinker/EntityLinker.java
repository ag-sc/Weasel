package entityLinker;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.TreeSet;

import configuration.Config;
import stopwatch.Stopwatch;
import annotatedSentence.AnnotatedSentence;
import annotatedSentence.Fragment;
import databaseConnectors.DatabaseConnector;
import databaseConnectors.H2Connector;
import evaluation.EvaluationEngine;
import fileparser.StopWordParser;

public class EntityLinker {

	private EvaluationEngine evaluator;
	private DatabaseConnector anchors;
	private TreeSet<String> stopWords;

	public EntityLinker(EvaluationEngine evaluator, DatabaseConnector connector, String stopWordsTextFile) {
		this.evaluator = evaluator;
		this.anchors = connector;
		if (stopWordsTextFile != null) {
			try {
				stopWords = StopWordParser.parseStopwords(stopWordsTextFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public EntityLinker(EvaluationEngine evaluator, DatabaseConnector connector) {
		this(evaluator, connector, null);
	}

	public AnnotatedSentence getFragmentedSentence(String sentence, HashSet<Integer> allEntities) throws ClassNotFoundException, SQLException {
		HashMap<Integer, Integer> foundEntities = new HashMap<Integer, Integer>();

		String splitSentence[] = sentence.replace(",", "").replace(".", "").split(" ");
		AnnotatedSentence as = new AnnotatedSentence(splitSentence);
		Stopwatch sw = new Stopwatch(Stopwatch.UNIT.MILLISECONDS);

		for (int i = 0; i < splitSentence.length; i++) {
			if (stopWords != null && stopWords.contains(splitSentence[i]))
				continue;

			LinkedList<String> candidates = new LinkedList<String>();
			LinkedList<String> tmpList = new LinkedList<String>();
			String originWord = "";
			String tmpWord = "";
			String testWord = "";
			int j = i;
			int maxJ= i;
			while (j < splitSentence.length) {
				// find entities for candidate vector score computation in vector evaluation step
				String word = splitSentence[j];
				if (word.length() > 0) {
					String wikiWord = Character.toUpperCase(word.charAt(0)) + word.substring(1); // build wikipedia-like string
					if (wikiWord.length() > 0) {
						if (testWord.length() == 0)
							testWord = wikiWord;
						else
							testWord = (testWord + "_" + wikiWord).trim();
						LinkedList<String> foundEntitiesList = anchors.getFragmentTargets(testWord);
						while (foundEntitiesList.size() > 0) {
							String idPlusCount = foundEntitiesList.pop();
							int foundEntityID = Integer.parseInt(idPlusCount.split("_")[0]);
							if (foundEntities.containsKey(foundEntityID)) {
								foundEntities.put(foundEntityID, foundEntities.get(foundEntityID) + 1);
							} else {
								foundEntities.put(foundEntityID, 1);
							}
						}
					}
				}
				
				// find anchors
				tmpWord = (tmpWord + " " + splitSentence[j]).trim();
				tmpList = anchors.getFragmentTargets(tmpWord);
				LinkedList<String> validWords = new LinkedList<String>();
				if(allEntities != null){
					for (String s : tmpList) {
						String[] splitString = s.split("_");
						if (allEntities.contains(Integer.parseInt(splitString[0])))
							validWords.add(s);
					}
					if (validWords.size() > 0) {
						candidates = validWords;
						originWord = tmpWord;
						maxJ = j;
					}
				} else {
					if (tmpList.size() > 0) {
						candidates = tmpList;
						originWord = tmpWord;
						maxJ = j;
					} else if (j > 5)
						break;
				}
				j++;
			}

			//maxJ -= 1;
			Fragment f = new Fragment(i, maxJ);
			f.addCandidateStrings(candidates);
			if (f.getCandidatesSize() == 0)
				continue;
			f.originWord = originWord;
			as.addFragment(f);
			i = maxJ;
		}
		
		as.setFoundEntities(foundEntities);
		//System.out.println("Added all anchor candidates - Time: " + sw.stop() + " ms");
		return as;

	}

	public AnnotatedSentence link(String sentence, HashSet<Integer> allEntities) {
		//sentence = sentence.toLowerCase();
		AnnotatedSentence as;
		try {
			as = getFragmentedSentence(sentence, allEntities);
			evaluator.evaluate(as);
			return as;
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String tmp[] = {};
		return new AnnotatedSentence(tmp);
	}

	public void closeConnectors() {
		anchors.close();
	}

}
