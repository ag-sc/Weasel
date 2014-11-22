package tfidf;

import java.util.Collections;
import java.util.LinkedList;
import java.util.TreeSet;

import datatypes.TFIDFResult;

public final class TFIDF {

	public static LinkedList<TFIDFResult> compute(String sentence, DocumentFrequency df){
		String wordArray[] = sentence.split(" ");
		return compute(wordArray, df);
	}
	
	public static LinkedList<TFIDFResult> compute(String[] sentence, DocumentFrequency df){
		TreeSet<String> wordSet = new TreeSet<String>();
		for(String w: sentence) wordSet.add(w.toLowerCase());
		TermFrequency tf = new TermFrequency();
		for(String word: sentence){
			tf.addTerm(word.toLowerCase());
		}
		
		LinkedList<TFIDFResult> resultList = new LinkedList<TFIDFResult>();
		for(String word: wordSet){
			int wordDf = df.getFrequency(word);
			if(wordDf == 0) continue;
			resultList.add(new TFIDFResult(word, df.numberOfDocuments, tf.getFrequency(word), wordDf));
		}
		Collections.sort(resultList);
		Collections.reverse(resultList);
		
		return resultList;
	}

}
