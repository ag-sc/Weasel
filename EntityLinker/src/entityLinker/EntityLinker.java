package entityLinker;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;

import configuration.Config;
import annotatedSentence.AnnotatedSentence;
import annotatedSentence.Fragment;
import databaseConnectors.DatabaseConnector;
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

	private AnnotatedSentence assignCandidates(AnnotatedSentence as, HashSet<Integer> allEntities) {
		HashMap<Integer, Integer> foundEntities = new HashMap<Integer, Integer>();

		List<Fragment> fragmentList = as.getFragmentList();
		boolean disallowRedirects = Boolean.parseBoolean(Config.getInstance().getParameter("disallowRedirectsAndDisambiguationAsCandidates"));
		
		for (Fragment f: fragmentList) {
			if (stopWords != null && stopWords.contains(f.originWord))
				continue;

			String originWord = f.originWord;
			
			LinkedList<String> foundEntitiesList = anchors.getFragmentTargets(originWord);
			
			if(foundEntitiesList.isEmpty()){	// if there are no candidates, check whether the entity appears directly (happens for obscure names for example)
				String tmp = originWord.replace(" ", "_");
				Integer id = anchors.resolveName(tmp);
				if(id != null) foundEntitiesList.add(id + "_1");
//				if(f.originWord.equals("Reuters Television")) System.out.println("reuters id: " + id + " word: " + tmp + " size: " + foundEntitiesList.size());
			}
			if(foundEntitiesList.isEmpty()){
				if(originWord.length() > 1 && originWord.equals(originWord.toUpperCase())){
					originWord = originWord.toUpperCase().replace(originWord.substring(1), originWord.substring(1).toLowerCase());
					foundEntitiesList = anchors.getFragmentTargets(originWord);
				}
			}
			
			if (disallowRedirects) {
				LinkedList<String> cleanedList = new LinkedList<String>();
				for (String s : foundEntitiesList) {
					String[] split = s.split("_");
					Integer id = Integer.parseInt(split[0]);
					if (anchors.getRedirect(id) >= 0) {
						cleanedList.add(anchors.getRedirect(id) + "_" + split[1]);
					} else if (id != null && !anchors.isDisambiguation(id)) {
						cleanedList.add(s);
					}
					f.addCandidateStrings(cleanedList);
				}
			} else {
				f.addCandidateStrings(foundEntitiesList);
			}
			
			// find entities for candidate vector score computation in vector evaluation step
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
		
		as.setFoundEntities(foundEntities);
		//System.out.println("Added all anchor candidates - Time: " + sw.stop() + " ms");
		return as;

	}

	public AnnotatedSentence link(AnnotatedSentence as, HashSet<Integer> allEntities) {
		// sentence = sentence.toLowerCase();
		assignCandidates(as, allEntities);
		evaluator.evaluate(as);
		return as;
	}

	public void closeConnectors() {
		anchors.close();
	}

}
