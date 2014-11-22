package evaluation;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeSet;
import java.util.Map.Entry;

import configuration.Config;
import tfidf.DocumentFrequency;
import tfidf.TFIDF;
import databaseConnectors.DatabaseConnector;
import databaseConnectors.H2Connector;
import datatypes.TFIDFResult;
import datatypes.Tuple;
import fileparser.StopWordParser;
import annotatedSentence.AnnotatedSentence;
import annotatedSentence.Fragment;

public class VectorEvaluation extends EvaluationEngine {

	private TreeSet<String> stopWords;
	private DatabaseConnector semanticSignatureDB;
	HashMap<Integer, Tuple<ArrayList<Integer>, HashMap<Integer, Float>>> vectorMap;
	DocumentFrequency df;
	double lambda = 0.5;
	
	public VectorEvaluation(DatabaseConnector semanticSignatureDB, String vectorMapFilePath, String dfFilePath) throws IOException, ClassNotFoundException {
		this.semanticSignatureDB = semanticSignatureDB;
		String stopwordsPath = Config.getInstance().getParameter("stopwordsPath");
		stopWords = StopWordParser.parseStopwords(stopwordsPath);
		
		FileInputStream fileInputStream = new FileInputStream(vectorMapFilePath);
		ObjectInputStream objectReader = new ObjectInputStream(fileInputStream);
		vectorMap = (HashMap<Integer, Tuple<ArrayList<Integer>, HashMap<Integer, Float>>>) objectReader.readObject(); 
		objectReader.close();
		
		fileInputStream = new FileInputStream(dfFilePath);
		objectReader = new ObjectInputStream(fileInputStream);
		df = (DocumentFrequency) objectReader.readObject(); 
		objectReader.close();
	}

	@Override
	public void evaluate(AnnotatedSentence annotatedSentence) {
		H2Connector h2 = (H2Connector) semanticSignatureDB;
		
		// TFIDF
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
		
		LinkedList<Fragment> fragmentList = annotatedSentence.buildFragmentList();
		for(Fragment fragment: fragmentList){
			String bestCandidate = "";
			double bestScore = 0;
			
			for(String candidate: fragment.candidates){
				// Candidate vector overlap
				int candidateID =  Integer.parseInt(candidate);
				Tuple<ArrayList<Integer>, HashMap<Integer, Float>> candidateEntry = vectorMap.get(candidateID);
				if(candidateEntry == null){
					//System.out.println(h2.resolveID(candidate) + " not found in bigMap!");
					continue;
				}else{
					System.out.println(h2.resolveID(candidate) + " found in bigMap!");
				}
				double candidateVectorScore = 0;
				for(Integer i: candidateEntry.x){
					if(fragment.candidates.contains(i.toString())){
						//System.out.println("\t" + h2.resolveID(i.toString()) + " found in vector");
						candidateVectorScore += 1.0;
					}
				}
				
				//candidateVectorScore /= fragment.candidates.size();
				
				// TFIDF vector overlap
				double tfidfVectorScore = 0;
				for(Entry<Integer, Float> e: candidateEntry.y.entrySet()){
					Integer i = e.getKey();
					if(i != null && tfidfMap.containsKey(i)){
						tfidfVectorScore += tfidfMap.get(i);
						System.out.println("\t" + df.getWordFromID(i) + " found in tfidf vector");
					}
				}
				
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
