package entityLinker;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;

import databaseBuilder.fileparser.StopWordParser;
import datatypes.annotatedSentence.AnnotatedSentence;
import datatypes.annotatedSentence.Fragment;
import datatypes.configuration.Config;
import datatypes.databaseConnectors.DatabaseConnector;
import entityLinker.evaluation.EvaluationEngine;

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

	private AnnotatedSentence assignCandidates(AnnotatedSentence as) {
		HashMap<Integer, Integer> foundEntities = new HashMap<Integer, Integer>();

		List<Fragment> fragmentList = as.getFragmentList();
		boolean disallowRedirects = Boolean.parseBoolean(Config.getInstance().getParameter("disallowRedirectsAndDisambiguationAsCandidates"));

		for (Fragment f : fragmentList) {
			if (stopWords != null && stopWords.contains(f.originWord))
				continue;

			String originWord = f.originWord;

			LinkedList<String> foundEntitiesList = anchors.getFragmentTargets(originWord);

			// System.out.println("Entities for originWord " + originWord);
			// for(String s: foundEntitiesList){
			// System.out.println("\t" + anchors.resolveID(s.split("_")[0]));
			// }
			// if there are no candidates, check whether the entity
			// appears directly (happens for obscure names for example).
			if (foundEntitiesList.isEmpty()) {
				String tmp = originWord.replace(" ", "_");
				Integer id = anchors.resolveName(tmp);
				if (id != null)
					foundEntitiesList.add(id + "_1");
			}
			if (foundEntitiesList.isEmpty()) {
				if (originWord.length() > 1 && originWord.equals(originWord.toUpperCase())) {
					originWord = originWord.toUpperCase().replace(originWord.substring(1), originWord.substring(1).toLowerCase());
					foundEntitiesList = anchors.getFragmentTargets(originWord);
				}
			}

//			 if(f.originWord.equals("reuters_television")){
//				  System.out.println("reuters id: " + anchors.resolveName(f.originWord));
//				  for(String s: foundEntitiesList) System.out.println(" " + s);
//			 }
			
			
			LinkedList<String> cleanedList = new LinkedList<String>();
//			System.out.println("Origin word: " + f.originWord);
			for (String s : foundEntitiesList) {
//				String[] split = s.split("_");
//				Integer id = Integer.parseInt(split[0]);
////				if(f.originWord.equalsIgnoreCase("Germany")) System.out.println("Germany is redirect: " + anchors.isDisambiguation(id));
////				if (id != null && !anchors.isDisambiguation(id)) {
////					System.out.println("	candidate: " + anchors.resolveID(id.toString()));
//					if (anchors.getRedirect(id) >= 0) {
//						if(disallowRedirects){
//							cleanedList.add(anchors.getRedirect(id) + "_" + split[1]);
//						}else{
							cleanedList.add(s);
//							cleanedList.add(anchors.getRedirect(id) + "_" + split[1]);
//						}
//					} else {
//						cleanedList.add(s);
//					}

//				}
//				else if(id != null && anchors.isDisambiguation(id)){
//					System.out.println("disambiguation: " + anchors.resolveID(id.toString()));
//				}
			}
//			if(f.originWord.equals("reuters_television")){
//				  System.out.println("reuters id: " + anchors.resolveName(f.originWord));
//				  for(String s: cleanedList) System.out.println(" " + s);
//			}
			f.addCandidateStrings(cleanedList);

			// find entities for candidate vector score computation in vector
			// evaluation step
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
		// System.out.println("Added all anchor candidates - Time: " + sw.stop()
		// + " ms");
		return as;

	}

	public AnnotatedSentence link(AnnotatedSentence as) {
		// sentence = sentence.toLowerCase();
		assignCandidates(as);
		evaluator.evaluate(as);
		return as;
	}

	public void closeConnectors() {
		anchors.close();
	}

}
