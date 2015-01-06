package evaluation;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeSet;

import org.ahocorasick.trie.Emit;
import org.ahocorasick.trie.Trie;
import org.nustaq.serialization.FSTObjectInput;
import org.nustaq.serialization.FSTObjectOutput;

import configuration.Config;
import stopwatch.Stopwatch;
import tfidf.DocumentFrequency;
import tfidf.TFIDF;
import databaseConnectors.DatabaseConnector;
import databaseConnectors.H2Connector;
import datatypes.TFIDFResult;
import datatypes.VectorEntry;
import fileparser.StopWordParser;
import annotatedSentence.AnnotatedSentence;
import annotatedSentence.Fragment;

public class VectorEvaluation extends EvaluationEngine {

	private TreeSet<String> stopWords;
	private DatabaseConnector semanticSignatureDB;
	HashMap<Integer, VectorEntry> vectorMap;
	DocumentFrequency df;
	double lambda = 0.5;

	public VectorEvaluation(DatabaseConnector semanticSignatureDB, String vectorMapFilePath, String dfFilePath) throws IOException, ClassNotFoundException {
		this.semanticSignatureDB = semanticSignatureDB;
		String stopwordsPath = Config.getInstance().getParameter("stopwordsPath");
		stopWords = StopWordParser.parseStopwords(stopwordsPath);

		Stopwatch sw = new Stopwatch(Stopwatch.UNIT.MINUTES);
		FileInputStream fileInputStream = new FileInputStream(vectorMapFilePath);
		ObjectInputStream objectReader = new ObjectInputStream(fileInputStream);
		vectorMap = (HashMap<Integer, VectorEntry>) objectReader.readObject();
		// vectorMap = new HashMap<Integer, VectorEntry>();
		objectReader.close();
		System.out.println("Reading in vectormap from file - took " + sw.stop() + " minutes.");

		sw.start();
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
		H2Connector h2 = (H2Connector) semanticSignatureDB;

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
			for (String candidate : fragment.candidates) {
				if (candidateCountMap.containsKey(candidate)) {
					candidateCountMap.put(candidate, candidateCountMap.get(candidate) + 1);
				} else {
					candidateCountMap.put(candidate, 1);
				}
			}
		}
		double candidateMagnitude = 0.0;
		for (Integer i : candidateCountMap.values()) {
			candidateMagnitude += i * i;
		}
		candidateMagnitude = Math.sqrt(candidateMagnitude);

		// tmp start

		// try {
		// ObjectOutputStream out = new ObjectOutputStream(new
		// FileOutputStream("../../data/candidateCount.map"));
		// out.writeObject(candidateCountMap);
		// out.close();
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		//
		// tmp end

		// evaluate
		double candidateVectorAverage = 0;
		double tfidfVectorAverage = 0;
		double count = 0;

		// normalizing
		HashMap<String, Double[]> scoreMap = new HashMap<String, Double[]>();
		double maxCandidateScore = 0.0;
		double maxTFIDFScore = 0.0;
		String sentence = " ";
		for (String substring : annotatedSentence.wordArray)
			sentence += substring + " ";
		sentence = sentence.toLowerCase();

		TreeSet<String> treeset = new TreeSet<String>();
		TreeSet<String> nottreeset = new TreeSet<String>();

		int candidateCount = 0;

		for (Fragment fragment : fragmentList) {
			String bestCandidate = "";
			double bestScore = 0;

			for (String candidate : fragment.candidates) {
				candidateCount++;
				if (candidateCount % 100 == 0)
					System.out.println("working on candidate " + candidateCount);
				// Find candidate
				int candidateID = Integer.parseInt(candidate);
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
				HashMap<Integer, Integer> hackMap = Config.getInstance().hackMap;
				// Trie trie = new
				// Trie().removeOverlaps().onlyWholeWords().caseInsensitive();
				double candidateVectorScore = 0;
				for (int i = 0; i < candidateEntry.semSigVector.length; i++) {
					if (candidateEntry.semSigVector[i] < 0)
						break;
					else if (hackMap.containsKey(candidateEntry.semSigVector[i])) {
						candidateVectorScore += 1;// hackMap.get(candidateEntry.semSigVector[i]);
					}

					// else
					// if(candidateCountMap.containsKey(Integer.toString(candidateEntry.semSigVector[i]))){
					// candidateVectorScore = candidateEntry.semSigCount[i] *
					// candidateCountMap.get(Integer.toString(candidateEntry.semSigVector[i]));
					// if(fragment.originWord.equals("BSE"))
					// System.out.println(h2.resolveID(Integer.toString(candidateID))
					// +
					// " - " +
					// h2.resolveID(Integer.toString(candidateEntry.semSigVector[i]))
					// +
					// " - " + candidateEntry.semSigCount[i] + " - " +
					// candidateCountMap.get(Integer.toString(candidateEntry.semSigVector[i])));
					// }

					// String word =
					// h2.resolveID(Integer.toString(candidateEntry.semSigVector[i])).toLowerCase().replace("_",
					// " ");
					// String word =
					// Integer.toString(candidateEntry.semSigVector[i]);
					// trie.addKeyword(word);

					// word = " " + word + " ";
					// if(treeset.contains(word)){
					// candidateVectorScore += 1;
					// } else if (!nottreeset.contains(word)) {
					// if (sentence.contains(word)) {
					// System.out.println(h2.resolveID(candidate) + " - found "
					// + word);
					// treeset.add(word);
					// candidateVectorScore += 1;
					// } else {
					// nottreeset.add(word);
					// }
					// }
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
				tfidfVectorScore /= tfidfMagnitude * magnitude(candidateEntry.tfScore);

				// normalizing
				Double tmpArray[] = { candidateVectorScore, tfidfVectorScore };
				scoreMap.put(candidate, tmpArray);
				if (candidateVectorScore > maxCandidateScore)
					maxCandidateScore = candidateVectorScore;
				if (tfidfVectorScore > maxTFIDFScore)
					maxTFIDFScore = tfidfVectorScore;

				// candidateScore = candidateScore / entry.x.size();
				double candidateScore = 100 * lambda * candidateVectorScore + (1 - lambda) * tfidfVectorScore;
				count += 1;
				// candidateVectorAverage += candidateVectorScore;
				// tfidfVectorAverage += tfidfVectorScore;
				// System.out.println("Score for " + h2.resolveID(candidate) +
				// ": " + candidateScore + " - vector: " + candidateVectorScore
				// + " - tfidf: " +tfidfVectorScore );
				// if(candidateScore > bestScore){
				// bestScore = candidateScore;
				// bestCandidate = candidate;
				// }
			}

			// if(bestScore > 0){
			// fragment.setEntity(h2.resolveID(bestCandidate));
			// fragment.probability = bestScore;
			// }
		}

		// Pick best after normalization
		try {
			Config config = Config.getInstance();
			BufferedWriter fw = new BufferedWriter(new FileWriter("vector_evaluation_evaluation.txt"));

			for (Fragment fragment : fragmentList) {
				fw.write(fragment.originWord + "\n");
				double bestScore = 0;
				String bestCandidate = "";
				for (String candidate : fragment.candidates) {
					Double[] tmp = scoreMap.get(candidate);
					if (tmp == null)
						continue;
					candidateVectorAverage += (tmp[0] / maxCandidateScore);
					tfidfVectorAverage += (tmp[1] / maxTFIDFScore);

					double candidateScore = lambda * (tmp[0] / maxCandidateScore) + (1 - lambda) * (tmp[1] / maxTFIDFScore);
					fw.write((tmp[0] / maxCandidateScore) + "\t" + (tmp[1] / maxTFIDFScore) + "\t" + h2.resolveID(candidate) + "\n");
					if (candidateScore > bestScore) {
						bestScore = candidateScore;
						bestCandidate = candidate;
					}
				}
				if (bestScore > 0) {
					fragment.setEntity(h2.resolveID(bestCandidate));
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

		// System.out.println("Entities:");
		// for(Integer i: vectorMap.get(371).x){
		// System.out.println(h2.resolveID(i.toString()));
		// }
		// System.out.println("\nWord frequencies:");
		// for(Entry<Integer, Float> e: vectorMap.get(371).y.entrySet()){
		// System.out.println(df.getWordFromID(e.getKey()) + " - tf/idf: " +
		// e.getValue());
		// }

	}

}
