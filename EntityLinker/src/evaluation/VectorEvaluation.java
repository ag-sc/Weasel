package evaluation;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.nustaq.serialization.FSTObjectInput;

import configuration.Config;
import stopwatch.Stopwatch;
import tfidf.DocumentFrequency;
import tfidf.TFIDF;
import databaseConnectors.DatabaseConnector;
import datatypes.TFIDFResult;
import datatypes.VectorEntry;
import fileparser.StopWordParser;
import annotatedSentence.AnnotatedSentence;
import annotatedSentence.Candidate;
import annotatedSentence.Fragment;

public class VectorEvaluation extends EvaluationEngine {

	private TreeSet<String> stopWords;
	private DatabaseConnector dbConnector;
	static HashMap<Integer, VectorEntry> vectorMap = null;
	static DocumentFrequency df = null;
	static double[] pageRankArray = null;
	double lambda;
	double pageRankWeight;
	boolean boolScoring = true;

	public VectorEvaluation(DatabaseConnector semanticSignatureDB, String vectorMapFilePath, String dfFilePath) throws IOException, ClassNotFoundException {
		this.dbConnector = semanticSignatureDB;
		String stopwordsPath = Config.getInstance().getParameter("stopwordsPath");
		stopWords = StopWordParser.parseStopwords(stopwordsPath);

		Stopwatch sw = new Stopwatch(Stopwatch.UNIT.MINUTES);
		// read vectorMap object
		if(vectorMap == null){
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
		if(pageRankArray == null){
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
		if(df == null){
			sw.start();
			System.out.println("DocumentFrequencyObject not loaded yet. Loading now...");
			FileInputStream fileInputStream = new FileInputStream(dfFilePath);
			FSTObjectInput in = new FSTObjectInput(fileInputStream);
			df = (DocumentFrequency) in.readObject();
			in.close();
			fileInputStream.close();
			System.out.println("Done. Took " + sw.stop() + " minutes.");
		}

		// fileInputStream = new FileInputStream(dfFilePath);
		// objectReader = new ObjectInputStream(fileInputStream);
		// df = (DocumentFrequency) objectReader.readObject();
		// df = new DocumentFrequency();
		
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

		// TFIDF vector computation for input sentence
		LinkedList<TFIDFResult> resultList = TFIDF.compute(annotatedSentence.getSentence(), df);
		Map<Integer, Float> tfidfMap = new HashMap<Integer, Float>();
		for (TFIDFResult r : resultList) {
			if (stopWords.contains(r.token))
				continue;
			Integer id = df.getWordID(r.token);
			if (id == null) {
				System.out.println(r.token + " not found in df");
				continue;
			}
			tfidfMap.put(id, r.tfidf);
		}

		double sentenceTfidfMagnitude = 0.0;
		for (double d : tfidfMap.values()) {
			sentenceTfidfMagnitude += d * d;
		}
		sentenceTfidfMagnitude = Math.sqrt(sentenceTfidfMagnitude);

		// candidate vector computation for input sentence
		Map<Integer, Integer> foundEntitiesMap = annotatedSentence.getFoundEntities();
		double sentenceCVMagnitude = 0.0;
		for(Integer value: foundEntitiesMap.values()){
			sentenceCVMagnitude += value;
		}
		sentenceCVMagnitude /= foundEntitiesMap.values().size();
		
//		Map<String, Integer> candidateCountMap = new HashMap<String, Integer>();
//		for (Fragment fragment : fragmentList) {
//			for (Candidate candidate : fragment.getCandidates()) {
//				if (candidateCountMap.containsKey(candidate.getEntity())) {
//					candidateCountMap.put(candidate.getEntity(), candidateCountMap.get(candidate.getEntity()) + 1);
//				} else {
//					candidateCountMap.put(candidate.getEntity(), 1);
//				}
//			}
//		}
//		double candidateMagnitude = 0.0;
//		for (Integer i : candidateCountMap.values()) {
//			candidateMagnitude += i * i;
//		}
//		candidateMagnitude = Math.sqrt(candidateMagnitude);

		// evaluate
//		double tfidfVectorAverage = 0;

		// variables for normalization
		HashMap<String, Double[]> scoreMap = new HashMap<String, Double[]>();
		double maxCandidateScore = 0.0;
		double maxTFIDFScore = 0.0;
		double maxCandidateReferences = 0.0;

		List<Fragment> fragmentList = annotatedSentence.getFragmentList();
		for (Fragment fragment : fragmentList) {
			if(fragment.originWord.equals("reuters_television")){
				System.out.println("Reuters outer loop");
			}
			for (Candidate candidate : fragment.getCandidates()) {
				// Find candidate
				int candidateID = Integer.parseInt(candidate.getEntity());
				VectorEntry candidateEntry = vectorMap.get(candidateID);

				if (candidateEntry == null) {
//					System.err.println("#" + dbConnector.resolveID(candidate.getEntity()) +" not found in vectormap!");
					continue;
				}else{
//					System.err.println(dbConnector.resolveID(candidate.getEntity()) +" found in vectormap!");
				}
				
				// TFIDF vector overlap
				double tfidfVectorScore = 0;
				for (int i = 0; i < candidateEntry.tfVector.length; i++) {
					if (candidateEntry.tfVector[i] < 0)
						break;
					else if (tfidfMap.containsKey(candidateEntry.tfVector[i])) {
						tfidfVectorScore += candidateEntry.tfScore[i] * tfidfMap.get(candidateEntry.tfVector[i]);
					}
				}

				double tfidfMagnitude = magnitude(candidateEntry.tfScore) * sentenceTfidfMagnitude;
				if (tfidfMagnitude > 0)
					tfidfVectorScore /= tfidfMagnitude;
				else
					tfidfVectorScore = 0;

				// Candidate vector overlap
				double candidateVectorScore = 0;
				for (int i = 0; i < candidateEntry.semSigVector.length; i++) {
					if (candidateEntry.semSigVector[i] < 0)
						break;
					else if (foundEntitiesMap.containsKey(candidateEntry.semSigVector[i])) {
						if (boolScoring) {
							candidateVectorScore += 1;
						}else{
							candidateVectorScore += candidateEntry.semSigCount[i] * foundEntitiesMap.get(candidateEntry.semSigVector[i]);
						}
					}
				}

				double semsigMagnitude = magnitude(candidateEntry.semSigCount) * sentenceCVMagnitude;
				if (semsigMagnitude > 0)
					candidateVectorScore /= semsigMagnitude;
				else
					candidateVectorScore = 0;

				// normalizing preparation
				Double tmpArray[] = { candidateVectorScore, tfidfVectorScore };
				scoreMap.put(candidate.getEntity(), tmpArray);
				if (candidate.count > maxCandidateReferences)
					maxCandidateReferences = candidate.count;
				if (candidateVectorScore > maxCandidateScore)
					maxCandidateScore = candidateVectorScore;
				if (tfidfVectorScore > maxTFIDFScore)
					maxTFIDFScore = tfidfVectorScore;

				if(fragment.originWord.equals("reuters_television")){
					System.out.println("candidate: " + dbConnector.resolveID(candidate.getEntity()));
					System.out.println(candidate.count);
					System.out.println(candidateVectorScore);
					System.out.println(tfidfVectorScore);
				}
			}
		}

		// Pick best after normalization
		try {
			BufferedWriter fw = new BufferedWriter(new FileWriter("vector_evaluation_evaluation.txt"));
			double tmp1 = (1 - lambda);
			double tmp2 = (1 - pageRankWeight);
			
//			System.err.println(lambda + " - " + tmp1 + " --- " + pageRankWeight + " -" + tmp2);

			for (Fragment fragment : fragmentList) {
				fw.write(fragment.originWord + "\n");
				double bestScore = 0;
				String bestCandidate = "";
				for (Candidate candidate : fragment.getCandidates()) {
					Double[] tmp = scoreMap.get(candidate.getEntity());
					if (tmp == null)
						continue;
//					tfidfVectorAverage += (tmp[1] / maxTFIDFScore);
//					if (Double.isNaN(tfidfVectorAverage))
//						System.err.println(candidate + " tfidfVector is NaN - " + tmp[1] + " - " + maxTFIDFScore);

					double candidateReferenceFrequency = (candidate.count / maxCandidateReferences);
					double candidateVectorScore = (tmp[0] / maxCandidateScore);
					double tfidfScore = (tmp[1] / maxTFIDFScore);
					
//					double candidateScore = (lambda * candidateVectorScore + (1 - lambda) * tfidfScore);
					
					//System.out.println("Candidate: " + dbConnector.resolveID(candidate.getEntity()) + " " + candidateReferenceFrequency+ " " + candidateVectorScore+ " " + tfidfScore);
					
					double candidateScore = candidateReferenceFrequency * (  (lambda * candidateVectorScore + tmp1 * tfidfScore) * tmp2
							+ pageRankWeight * pageRankArray[Integer.parseInt(candidate.getEntity())] );

//					double candidateScore = pageRankArray[Integer.parseInt(candidate.getEntity())];
					
					fw.write("reference factor: " + (candidate.count / maxCandidateReferences) + "\tcandidateScore: " + (tmp[0] / maxCandidateScore)
							+ "\ttfidfScore:" + (tmp[1] / maxTFIDFScore) + "\n");
					fw.write("\t" + dbConnector.resolveID(candidate.getEntity()) + "\t" + "pagerank: " + pageRankArray[Integer.parseInt(candidate.getEntity())] + "\n");
					if (candidateScore > bestScore) {
						bestScore = candidateScore;
						bestCandidate = candidate.getEntity();
					}
//					if(fragment.originWord.equals("Reuters Television")){
//						System.out.println("score " + candidate.getWord() + " "+ candidateScore);
//					}
				}
				if (bestScore > 0) {
					fragment.setEntity(dbConnector.resolveID(bestCandidate));
//					System.out.println("best candidate: " + bestCandidate + " -> " + fragment.getEntity());
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

//		System.out.println("candidate vector average: " + (candidateVectorAverage / count));
//		System.out.println("tfidf vector average:     " + (tfidfVectorAverage / count));

	}

}
