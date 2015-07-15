package entityLinker.evaluation;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.nustaq.serialization.FSTObjectInput;

import utility.Stopwatch;
import databaseBuilder.fileparser.StopWordParser;
import datatypes.TFIDFResult;
import datatypes.VectorEntry;
import datatypes.annotatedSentence.AnnotatedSentence;
import datatypes.annotatedSentence.Candidate;
import datatypes.annotatedSentence.Fragment;
import datatypes.configuration.Config;
import datatypes.databaseConnectors.DatabaseConnector;
import datatypes.tfidf.DocumentFrequency;
import datatypes.tfidf.TFIDF;

public class VectorEvaluation extends EvaluationEngine {

	private TreeSet<String> stopWords;
	private DatabaseConnector dbConnector;
	static HashMap<Integer, VectorEntry> vectorMap = null;
	static DocumentFrequency df = null;
	static double[] pageRankArray = null;
	double lambda;
	double pageRankWeight;
	boolean boolScoring = true;
	WekaLink wekaLink;

	public VectorEvaluation(DatabaseConnector semanticSignatureDB, String vectorMapFilePath, String dfFilePath, WekaLink wekaLink) throws IOException,
			ClassNotFoundException {
		this.dbConnector = semanticSignatureDB;
		String stopwordsPath = Config.getInstance().getParameter("stopwordsPath");
		stopWords = StopWordParser.parseStopwords(stopwordsPath);
		this.wekaLink = wekaLink;

		Stopwatch sw = new Stopwatch(Stopwatch.UNIT.MINUTES);
		// read vectorMap object
		if (vectorMap == null) {
			sw.start();
			FileInputStream fileInputStream = new FileInputStream(vectorMapFilePath);
			ObjectInputStream objectReader = new ObjectInputStream(fileInputStream);
			System.out.println("Vector map not loaded yet. Loading now...");
			vectorMap = (HashMap<Integer, VectorEntry>) objectReader.readObject();
			objectReader.close();
			fileInputStream.close();
			System.out.println("Done. Took " + sw.stop() + " minutes.");
		}

		// read pageRank object
		if (pageRankArray == null) {
			sw.start();
			System.out.println("PageRank not loaded yet. Loading now...");
			String pageRankArrayPath = Config.getInstance().getParameter("pageRankArrayPath");
			FileInputStream fileInputStream = new FileInputStream(pageRankArrayPath);
			ObjectInputStream objectReader = new ObjectInputStream(fileInputStream);
			pageRankArray = (double[]) objectReader.readObject();
			objectReader.close();
			fileInputStream.close();
			System.out.println("Done. Took " + sw.stop() + " minutes.");
		}

		sw.start();
		// read document frequency object
		if (df == null) {
			sw.start();
			System.out.println("DocumentFrequencyObject not loaded yet. Loading now...");

			// FileInputStream fileInputStream = new
			// FileInputStream(dfFilePath);
			// ObjectInputStream objectReader = new
			// ObjectInputStream(fileInputStream);
			// df = (DocumentFrequency) objectReader.readObject();
			// objectReader.close();

//			df = new DocumentFrequency();
			
			FileInputStream fileInputStream = new FileInputStream(dfFilePath);
			FSTObjectInput in = new FSTObjectInput(fileInputStream);
			df = (DocumentFrequency) in.readObject();
			in.close();
			fileInputStream.close();
			
			System.out.println("Done. Took " + sw.stop() + " minutes.");
		}

		Config config = Config.getInstance();
		lambda = Double.parseDouble(config.getParameter("vector_evaluation_lamda"));
		pageRankWeight = Double.parseDouble(config.getParameter("vector_evaluation_pageRankWeight"));
		boolScoring = Boolean.parseBoolean(config.getParameter("candidate_vector_boolean_scoring"));
	}

	private double magnitude(int[] array) {
		double result = 0.0;
		for (int i = 0; i < array.length; i++) {
			result += array[i] * array[i];
		}
		result = Math.sqrt(result);
		return result;
	}

	private double magnitude(float[] array) {
		double result = 0.0;
		for (int i = 0; i < array.length; i++) {
			result += array[i] * array[i];
		}
		result = Math.sqrt(result);
		return result;
	}

	@Override
	public void evaluate(AnnotatedSentence annotatedSentence) {
		Config config = Config.getInstance();

		// TFIDF computation for input sentence
		LinkedList<TFIDFResult> resultList = TFIDF.compute(annotatedSentence.getSentence(), df);
		Map<Integer, Float> tfidfMap = new HashMap<Integer, Float>();
		for (TFIDFResult r : resultList) {
			if (stopWords.contains(r.token))
				continue;
			Integer i = df.getWordID(r.token);
			if (i == null) {
				System.out.println(r.token + " not found in df");
				continue;
			}
			tfidfMap.put(i, r.tfidf);
		}

		double tfidfMagnitude = 0.0;
		for (double d : tfidfMap.values()) {
			tfidfMagnitude += d * d;
		}
		tfidfMagnitude = Math.sqrt(tfidfMagnitude);

		// prepare candidate vector
		// Map<String, Integer> candidateCountMap = new HashMap<String,
		// Integer>();
		// for (Fragment fragment : fragmentList) {
		// for (Candidate candidate : fragment.getCandidates()) {
		// if (candidateCountMap.containsKey(candidate.getEntity())) {
		// candidateCountMap.put(candidate.getEntity(),
		// candidateCountMap.get(candidate.getEntity()) + 1);
		// } else {
		// candidateCountMap.put(candidate.getEntity(), 1);
		// }
		// }
		Map<Integer, Integer> foundEntitiesMap = annotatedSentence.getFoundEntities();
		double candidateMagnitude = 0.0;
		for (Integer i : foundEntitiesMap.values()) {
			candidateMagnitude += i * i;
		}
		candidateMagnitude = Math.sqrt(candidateMagnitude);

		// evaluate
		double candidateVectorAverage = 0;
		double tfidfVectorAverage = 0;
		double count = 0;

		// normalizing
		HashMap<String, Double[]> scoreMap = new HashMap<String, Double[]>();
		double maxCandidateScore = 0.0;
		double maxTFIDFScore = 0.0;
		// double maxCandidateReferences = 0.0;
		// String sentence = " ";
		// for (String substring : annotatedSentence.wordArray)
		// sentence += substring + " ";
		// sentence = sentence.toLowerCase();

		List<Fragment> fragmentList = annotatedSentence.getFragmentList();
		for (Fragment fragment : fragmentList) {
			for (Candidate candidate : fragment.getCandidates()) {
				// if (candidateCount % 100 == 0)
				// System.out.println("working on candidate " + candidateCount);
				// Find candidate
				int candidateID = Integer.parseInt(candidate.getEntity());
				VectorEntry candidateEntry = vectorMap.get(candidateID);

				if (candidateEntry == null) {
					// System.out.println(h2.resolveID(candidate) +
					// " not found in bigMap!");
					continue;
				} else {
					// System.out.println(h2.resolveID(candidate) +
					// " found in bigMap!");
				}

				// Candidate vector overlap
				double candidateVectorScore = 0;
				for (int i = 0; i < candidateEntry.semSigVector.length; i++) {
					if (candidateEntry.semSigVector[i] < 0)
						break;
					else if (foundEntitiesMap.containsKey(candidateEntry.semSigVector[i])) {
						if (boolScoring) {
							candidateVectorScore += 1;
						}

						else
							candidateVectorScore += candidateEntry.semSigCount[i] * foundEntitiesMap.get(candidateEntry.semSigVector[i]);
					}
				}

				// Collection<Emit> emits = trie.parseText(sentence);
				// candidateVectorScore += emits.size();

				double mag = magnitude(candidateEntry.semSigCount);
				if (mag > 0)
					candidateVectorScore /= candidateMagnitude * mag;
				else
					candidateVectorScore = 0;

				// TFIDF vector overlap
				double tfidfVectorScore = 0;
				for (int i = 0; i < candidateEntry.tfVector.length; i++) {
					if (candidateEntry.tfVector[i] < 0)
						break;
					else if (tfidfMap.containsKey(candidateEntry.tfVector[i])) {
						tfidfVectorScore += candidateEntry.tfScore[i] * tfidfMap.get(candidateEntry.tfVector[i]);
					}
				}

				double tmp = tfidfMagnitude * magnitude(candidateEntry.tfScore);
				if (tmp > 0)
					tfidfVectorScore /= tmp;
				else
					tfidfVectorScore = 0;

				// normalizing
				Double tmpArray[] = { candidateVectorScore, tfidfVectorScore };
				scoreMap.put(candidate.getEntity(), tmpArray);
				// if (candidate.count > maxCandidateReferences) {
				// maxCandidateReferences = candidate.count;
				// }
				if (candidateVectorScore > maxCandidateScore)
					maxCandidateScore = candidateVectorScore;
				if (tfidfVectorScore > maxTFIDFScore)
					maxTFIDFScore = tfidfVectorScore;

				count += 1;
			}
		}

		for (Fragment fragment : fragmentList) {
			boolean eligibleForARFF = false;
			Integer tmpID = dbConnector.resolveName(fragment.getOriginEntity());
			int numberOfIncorrectExamples = Integer.parseInt(config.getParameter("incorrecExamplesPerCorrect"));
			TreeSet<String> incorrectSet = new TreeSet<String>();
			Candidate correct = null;
			if (tmpID != null) {
				TreeSet<Candidate> candidates = fragment.getCandidatesSortByNameOnly();
				correct = new Candidate(tmpID.toString(), 0, 0);
				if (candidates.contains(correct) && candidates.size() > (numberOfIncorrectExamples + 1)) {
					eligibleForARFF = true;
					LinkedList<Candidate> candidateList = new LinkedList<Candidate>(candidates);

					Collections.shuffle(candidateList);
					for (int i = 0; i < numberOfIncorrectExamples; i++) {
						Candidate tmp = candidateList.pop();
						if (tmp.getEntity().equals(correct.getEntity())) {
							i--;
						} else {
							incorrectSet.add(tmp.getEntity());
						}
					}

					Double[] tmpMap1 = scoreMap.get(correct.getEntity());
					if (tmpMap1 == null)
						eligibleForARFF = false;
					for (String c : incorrectSet) {
						Double[] tmpMap2 = scoreMap.get(c);
						if (tmpMap2 == null)
							eligibleForARFF = false;
					}

				}
			}

			double bestScore = 0;
			double smoScore = 99999;
			String bestCandidate = "";
			
//			if(!fragment.getOriginEntity().isEmpty() && fragment.getCandidates().isEmpty()){
//				System.out.println("No candidates for fragment with entity: " + fragment.getOriginEntity());
//			}
			
			for (Candidate candidate : fragment.getCandidates()) {
				Double[] tmp = scoreMap.get(candidate.getEntity());
				if (tmp == null)
					continue;
				candidateVectorAverage += (tmp[0] / maxCandidateScore);
				tfidfVectorAverage += (tmp[1] / maxTFIDFScore);
				if (Double.isNaN(tfidfVectorAverage))
					System.err.println(candidate + " tfidfVector is NaN - " + tmp[1] + " - " + maxTFIDFScore);

				double candidateReferenceFrequency = candidate.getReferenceProbability();//((double) candidate.count / (double) dbConnector.getTotalNumberOfReferences());
				// TODO: change calculation back?
				double candidateVectorScore = (tmp[0]); // / maxCandidateScore);
				double tfidfScore = (tmp[1]); // / maxTFIDFScore);

				// double candidateScore = candidateReferenceFrequency * (lambda
				// * candidateVectorScore + (1 - lambda) * tfidfScore);

				// double candidateScore = candidateReferenceFrequency * (lambda
				// * candidateVectorScore + tmp1 * tfidfScore) * tmp2
				// + pageRankWeight *
				// pageRankArray[Integer.parseInt(candidate.getEntity())] ;

				// 3
				// double candidateScore = (1 - pageRankWeight) *
				// candidateReferenceFrequency + pageRankWeight *
				// pageRankArray[Integer.parseInt(candidate.getEntity())];

				// 4
				// double candidateScore = lambda * candidateVectorScore + (1 -
				// lambda) * tfidfScore;
				// double candidateScore = candidateReferenceFrequency * (lambda
				// * candidateVectorScore + (1 - lambda) * tfidfScore); // 5
				// double candidateScore =
				// pageRankArray[Integer.parseInt(candidate.getEntity())] *
				// (lambda * candidateVectorScore + (1 - lambda) * tfidfScore);
				// // 6
				// double candidateScore =
				// pageRankArray[Integer.parseInt(candidate.getEntity())] +
				// (lambda * candidateVectorScore + (1 - lambda) * tfidfScore);
				// // 7

				// 8
				// double candidateScore = candidateReferenceFrequency * (lambda
				// * candidateVectorScore + tmp1 * tfidfScore) * tmp2
				// + pageRankWeight *
				// pageRankArray[Integer.parseInt(candidate.getEntity())] ;

				// 9
				// double candidateScore = (lambda * candidateVectorScore + (1-
				// lambda) * tfidfScore) *
				// (pageRankWeight *
				// pageRankArray[Integer.parseInt(candidate.getEntity())] +
				// candidateReferenceFrequency * (1 - pageRankWeight));

				// 10
				// double candidateScore =
				// Math.sqrt(candidateReferenceFrequency) * (lambda *
				// candidateVectorScore + (1 - lambda) * tfidfScore);

				// 11
				// double candidateScore = (1 - pageRankWeight) *
				// Math.sqrt(candidateReferenceFrequency) + pageRankWeight *
				// Math.sqrt(pageRankArray[Integer.parseInt(candidate.getEntity())]);

				// 13
				// double candidateScore = lambda *
				// Math.sqrt(candidateVectorScore) + (1 - lambda) *
				// Math.sqrt(tfidfScore);

				// 14
				double pageRank = pageRankArray[Integer.parseInt(candidate.getEntity())];

				double candidateScore = (lambda * Math.sqrt(candidateVectorScore) + (1 - lambda) * Math.sqrt(tfidfScore))
						* (pageRankWeight * Math.sqrt(pageRank) + Math.sqrt(candidateReferenceFrequency) * (1 - pageRankWeight));

				if (eligibleForARFF && config.getParameter("wekaModelStatus").equals("train")) {
					try {
						if (candidate.getEntity().equals(correct.getEntity())) {
							wekaLink.writeToARFF(candidateVectorScore, tfidfScore, pageRank, candidateReferenceFrequency, "1");
						} else if (incorrectSet.contains(candidate.getEntity())) {
							wekaLink.writeToARFF(candidateVectorScore, tfidfScore, pageRank, candidateReferenceFrequency, "0");
						}
					} catch (Exception e) {
						System.err.println("Exception during:");
						System.err.println(dbConnector.resolveID(candidate.getEntity()));
						System.err.println(candidateVectorScore);
						System.err.println(tfidfScore);
						System.err.println(pageRankArray[Integer.parseInt(candidate.getEntity())]);
						System.err.println(candidateReferenceFrequency);
					}
				}

				try {
					if (config.getParameter("wekaModelStatus").equals("test")) {
						
						
						
						double[] values = wekaLink.testInstance(candidateVectorScore, tfidfScore, pageRank, candidateReferenceFrequency);
						if (values[0] < smoScore) {
							smoScore = values[0];
							bestScore = values[1];
							bestCandidate = candidate.getEntity();
							fragment.setEntity(dbConnector.resolveID(bestCandidate));
							fragment.probability = 1.0 - smoScore;
						}
						
						//System.out.println(candidate.getEntity() + ": candidateVectorScore: " + candidateVectorScore + " tfidf: " + tfidfScore + " pagerank: " + pageRank + " candidateref: " + candidateReferenceFrequency + " values0: " + values[0]  + " values1: " +  values[1]);

						
//						if(dbConnector.resolveID(candidate.getEntity()).equals("bovine_spongiform_encephalopathy")){
//							System.out.println("bovine: " + candidateVectorScore + " tfidf: " + tfidfScore + " pagerank: " + pageRank + " candidateref: " + candidateReferenceFrequency + " values0: " + values[0]  + " values1: " +  values[1]);
//						}else if(dbConnector.resolveID(candidate.getEntity()).equals("bachelor_of_science")){
//							System.out.println("science: " + candidateVectorScore + " tfidf: " + tfidfScore + " pagerank: " + pageRank + " candidateref: " + candidateReferenceFrequency + " values0: " + values[0]  + " values1: " +  values[1]);
//						}
					}

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				if (config.getParameter("wekaModelStatus").equals("off") && candidateScore > bestScore) {
					bestScore = candidateScore;
					bestCandidate = candidate.getEntity();
				}
			}
			if (config.getParameter("wekaModelStatus").equals("off") && bestScore > 0) {
				fragment.setEntity(dbConnector.resolveID(bestCandidate));
				fragment.probability = bestScore;
			}
		}

		annotatedSentence.assign(0);

	}
}
