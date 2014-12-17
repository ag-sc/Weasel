package evaluation;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeSet;
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
		objectReader.close();
		System.out.println("Reading in vectormap from file - took " + sw.stop() + " minutes.");
		
		sw.start();
		fileInputStream = new FileInputStream(dfFilePath);
		objectReader = new ObjectInputStream(fileInputStream);
		df = (DocumentFrequency) objectReader.readObject(); 
		objectReader.close();
		System.out.println("Reading in documentfrequency from file - took " + sw.stop() + " minutes.");
	}
	
	private double magnitude(int[] array){
		double result = 0.0;
		for(int i = 0; i < array.length; i++){
			result += array[i] * array[i];
		}
		result = Math.sqrt(result);
		return result;
	}
	
	private double magnitude(float[] array){
		double result = 0.0;
		for(int i = 0; i < array.length; i++){
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
		for(TFIDFResult r: resultList){
			if(stopWords.contains(r.token)) continue;
			Integer i = df.getWordID(r.token);
			if(i == null){
				System.out.println(r.token + " not found in df");
				continue;
			}
			tfidfMap.put(i , r.tfidf);
		}
		
		double tfidfMagnitude = 0.0;
		for(double d: tfidfMap.values()){
			tfidfMagnitude += d * d;
		}
		tfidfMagnitude = Math.sqrt(tfidfMagnitude);
		
		// prepare candidate vector
		LinkedList<Fragment> fragmentList = annotatedSentence.buildFragmentList();
		Map<String, Integer> candidateCountMap = new HashMap<String, Integer>();
		for(Fragment fragment: fragmentList){
			for(String candidate: fragment.candidates){
				if(candidateCountMap.containsKey(candidate)){
					candidateCountMap.put(candidate, candidateCountMap.get(candidate) + 1);
				}else{
					candidateCountMap.put(candidate, 1);
				}
			}
		}
		double candidateMagnitude = 0.0;
		for(Integer i: candidateCountMap.values()){
			candidateMagnitude += i * i;
		}
		candidateMagnitude = Math.sqrt(candidateMagnitude);
		
		// evaluate
		for(Fragment fragment: fragmentList){
			String bestCandidate = "";
			double bestScore = 0;
			
			for(String candidate: fragment.candidates);
			
			for(String candidate: fragment.candidates){
				// Find candidate
				int candidateID =  Integer.parseInt(candidate);
				VectorEntry candidateEntry = vectorMap.get(candidateID);
				if(candidateEntry == null){
					//System.out.println(h2.resolveID(candidate) + " not found in bigMap!");
					continue;
				}else{
					System.out.println(h2.resolveID(candidate) + " found in bigMap!");
				}
				
				// Candidate vector overlap
				double candidateVectorScore = 0;
				for(int i = 0; i < candidateEntry.semSigVector.length; i++){
					if(candidateEntry.semSigVector[i] < 0) break;
					else if(candidateCountMap.containsKey(Integer.toString(candidateEntry.semSigVector[i]))){
						candidateVectorScore = candidateEntry.semSigCount[i] * candidateCountMap.get(Integer.toString(candidateEntry.semSigVector[i]));
					}
				}
				candidateVectorScore /= candidateMagnitude * magnitude(candidateEntry.semSigCount);
				
				// TFIDF vector overlap
				double tfidfVectorScore = 0;
				for(int i = 0; i < candidateEntry.tfVector.length; i++){
					if(candidateEntry.tfVector[i] < 0) break;
					else if(tfidfMap.containsKey(candidateEntry.tfVector[i])){
						tfidfVectorScore += candidateEntry.tfScore[i] * tfidfMap.get(candidateEntry.tfVector[i]);
					}
				}
				tfidfVectorScore /= tfidfMagnitude * magnitude(candidateEntry.tfScore);
				
				
				//candidateScore = candidateScore / entry.x.size();
				double candidateScore = lambda * candidateVectorScore + (1 - lambda) * tfidfVectorScore;
				System.out.println("Score for " + h2.resolveID(candidate) + ": " + candidateScore + " - vector: " + candidateVectorScore + " - tfidf: " +tfidfVectorScore );
				if(candidateScore > bestScore){
					bestScore = candidateScore;
					bestCandidate = candidate;
				}
			}
			
			if(bestScore > 0){
				fragment.setEntity(h2.resolveID(bestCandidate));
				fragment.probability = bestScore;
			}
		}
		
		annotatedSentence.assign(0);
		
		
//		System.out.println("Entities:");
//		for(Integer i: vectorMap.get(371).x){
//			System.out.println(h2.resolveID(i.toString()));
//		}
//		System.out.println("\nWord frequencies:");
//		for(Entry<Integer, Float> e: vectorMap.get(371).y.entrySet()){
//			System.out.println(df.getWordFromID(e.getKey()) + " - tf/idf: " + e.getValue());
//		}

	}

}
