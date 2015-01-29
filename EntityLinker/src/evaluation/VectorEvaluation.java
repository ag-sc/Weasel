package evaluation;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeSet;

import org.nustaq.serialization.FSTObjectInput;

import configuration.Config;
import stopwatch.Stopwatch;
import tfidf.DocumentFrequency;
import tfidf.TFIDF;
import databaseConnectors.DatabaseConnector;
import databaseConnectors.H2Connector;
import datatypes.PageRankNode;
import datatypes.TFIDFResult;
import datatypes.VectorEntry;
import fileparser.StopWordParser;
import annotatedSentence.AnnotatedSentence;
import annotatedSentence.Candidate;
import annotatedSentence.Fragment;

public class VectorEvaluation extends EvaluationEngine {

	private TreeSet<String> stopWords;
	private DatabaseConnector dbConnector;
	HashMap<Integer, VectorEntry> vectorMap;
	DocumentFrequency df;
	double[] pageRankArray;
	double lambda = 0.5;
	boolean boolScoring = true;

	public VectorEvaluation(DatabaseConnector semanticSignatureDB, String vectorMapFilePath, String dfFilePath) throws IOException, ClassNotFoundException {
		this.dbConnector = semanticSignatureDB;
		String stopwordsPath = Config.getInstance().getParameter("stopwordsPath");
		stopWords = StopWordParser.parseStopwords(stopwordsPath);

		Stopwatch sw = new Stopwatch(Stopwatch.UNIT.MINUTES);
		FileInputStream fileInputStream = new FileInputStream(vectorMapFilePath);
		ObjectInputStream objectReader = new ObjectInputStream(fileInputStream);
		vectorMap = (HashMap<Integer, VectorEntry>) objectReader.readObject();
		// vectorMap = new HashMap<Integer, VectorEntry>();
		objectReader.close();
		System.out.println("Reading in vectormap from file - took " + sw.stop() + " minutes.");

		// read pageRank object
		String pageRankArrayPath = Config.getInstance().getParameter("pageRankArrayPath");
		fileInputStream = new FileInputStream(pageRankArrayPath);
		objectReader = new ObjectInputStream(fileInputStream);
		pageRankArray = (double[]) objectReader.readObject();
		objectReader.close();
		fileInputStream.close();

		sw.start();
		// read document frequency object
		fileInputStream = new FileInputStream(dfFilePath);
		FSTObjectInput in = new FSTObjectInput(fileInputStream);
		df = (DocumentFrequency) in.readObject();
		in.close();

		// fileInputStream = new FileInputStream(dfFilePath);
		// objectReader = new ObjectInputStream(fileInputStream);
		// df = (DocumentFrequency) objectReader.readObject();
		// df = new DocumentFrequency();
		objectReader.close();
		System.out.println("Reading in documentfrequency from file - took " + sw.stop() + " minutes.");
		Config config = Config.getInstance();
		lambda = Double.parseDouble(config.getParameter("vector_evaluation_lamda"));
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

		// TFIDF computation for input sentence
		LinkedList<TFIDFResult> resultList = TFIDF.compute(annotatedSentence.wordArray, df);
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
		LinkedList<Fragment> fragmentList = annotatedSentence.buildFragmentList();
		Map<String, Integer> candidateCountMap = new HashMap<String, Integer>();
		for (Fragment fragment : fragmentList) {
			for (Candidate candidate : fragment.getCandidates()) {
				if (candidateCountMap.containsKey(candidate.getWord())) {
					candidateCountMap.put(candidate.getWord(), candidateCountMap.get(candidate.getWord()) + 1);
				} else {
					candidateCountMap.put(candidate.getWord(), 1);
				}
			}
		}
		double candidateMagnitude = 0.0;
		for (Integer i : candidateCountMap.values()) {
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
		double maxCandidateReferences = 0.0;
		String sentence = " ";
		for (String substring : annotatedSentence.wordArray)
			sentence += substring + " ";
		//sentence = sentence.toLowerCase();

		for (Fragment fragment : fragmentList) {
			for (Candidate candidate : fragment.getCandidates()) {
				// if (candidateCount % 100 == 0)
				// System.out.println("working on candidate " + candidateCount);
				// Find candidate
				int candidateID = Integer.parseInt(candidate.getWord());
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
				Map<Integer, Integer> foundEntitiesMap = annotatedSentence.getFoundEntities();
				double candidateVectorScore = 0;
				for (int i = 0; i < candidateEntry.semSigVector.length; i++) {
					if (candidateEntry.semSigVector[i] < 0)
						break;
					else if (foundEntitiesMap.containsKey(candidateEntry.semSigVector[i])) {
						if (boolScoring) {
							candidateVectorScore += 1;
							// if(fragment.originWord.equals("British") &&
							// candidate.word.equals("12863")){
							// System.out.println("United_States overlap: " +
							// h2.resolveID(Integer.toString(candidateEntry.semSigVector[i])));
							// }else if(fragment.originWord.equals("British") &&
							// candidate.word.equals("122931")){
							// System.out.println("Great_Britain overlap: " +
							// h2.resolveID(Integer.toString(candidateEntry.semSigVector[i])));
							// }
						}

						else
							candidateVectorScore += foundEntitiesMap.get(candidateEntry.semSigVector[i]);
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
				scoreMap.put(candidate.getWord(), tmpArray);
				if (candidate.count > maxCandidateReferences) {
					maxCandidateReferences = candidate.count;
				}
				if (candidateVectorScore > maxCandidateScore)
					maxCandidateScore = candidateVectorScore;
				if (tfidfVectorScore > maxTFIDFScore)
					maxTFIDFScore = tfidfVectorScore;

				count += 1;
			}
		}

		// Pick best after normalization
		try {
			BufferedWriter fw = new BufferedWriter(new FileWriter("vector_evaluation_evaluation.txt"));

			for (Fragment fragment : fragmentList) {
				fw.write(fragment.originWord + "\n");
				double bestScore = 0;
				String bestCandidate = "";
				for (Candidate candidate : fragment.getCandidates()) {
					Double[] tmp = scoreMap.get(candidate.getWord());
					if (tmp == null)
						continue;
					candidateVectorAverage += (tmp[0] / maxCandidateScore);
					tfidfVectorAverage += (tmp[1] / maxTFIDFScore);
					if (Double.isNaN(tfidfVectorAverage))
						System.err.println(candidate + " tfidfVector is NaN - " + tmp[1] + " - " + maxTFIDFScore);

					double candidateScore = (candidate.count / maxCandidateReferences)
							* (lambda * (tmp[0] / maxCandidateScore) + (1 - lambda) * (tmp[1] / maxTFIDFScore));
//					double candidateScore = (candidate.count / maxCandidateReferences)
//							* ((lambda * (tmp[0] / maxCandidateScore) + (1 - lambda) * (tmp[1] / maxTFIDFScore)) * 0.8 + 0.2 * pageRankArray[Integer
//									.parseInt(candidate.getWord())]);

					fw.write("reference factor: " + (candidate.count / maxCandidateReferences) + "\tcandidateScore: " + (tmp[0] / maxCandidateScore)
							+ "\ttfidfScore:" + (tmp[1] / maxTFIDFScore) + "\n");
					fw.write("\t" + dbConnector.resolveID(candidate.getWord()) + "\t" + "pagerank: " + pageRankArray[Integer.parseInt(candidate.getWord())] + "\n");
					if (candidateScore > bestScore) {
						bestScore = candidateScore;
						bestCandidate = candidate.getWord();
					}
				}
				if (bestScore > 0) {
					fragment.setEntity(dbConnector.resolveID(bestCandidate));
					fragment.probability = bestScore;
				}
				fw.write("\n");
			}
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		annotatedSentence.assign(0);

		System.out.println("candidate vector average: " + (candidateVectorAverage / count));
		System.out.println("tfidf vector average:     " + (tfidfVectorAverage / count));

	}

}
