import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.TreeSet;

import custom.DocumentFrequency;
import custom.TermFrequency;
import datatypes.TFIDFResult;
import datatypes.Tuple;


public class SignaturemapSandbox {

	public SignaturemapSandbox() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) throws IOException, ClassNotFoundException {
		// get df object
		FileInputStream fileInputStream = new FileInputStream("../../data/Wikipedia Abstracts/documentFrequency");
		ObjectInputStream objectReader = new ObjectInputStream(fileInputStream);
		DocumentFrequency df = (DocumentFrequency) objectReader.readObject(); 
		objectReader.close();
		
		// get abstract reader
		BufferedReader br = new BufferedReader(new FileReader("../../data/Wikipedia Abstracts/test/abstracts_cleaned.txt"));
		String line;
		int counter = 0;
		
		// create fingerprint map
		HashMap<Integer, Tuple<ArrayList<Integer>, HashMap<Integer, Float>>> map = new HashMap<Integer, Tuple<ArrayList<Integer>, HashMap<Integer, Float>>>();
		
		while((line = br.readLine()) != null){
			counter++;
			String title = br.readLine().replace(" ", "_");
			
			
			// calculate TF/IDF
			line = br.readLine().toLowerCase();
			String wordArray[] = line.split(" ");
			TreeSet<String> wordSet = new TreeSet<String>();
			for(String w: wordArray) wordSet.add(w);
			TermFrequency tf = new TermFrequency();
			for(String word: wordArray){
				tf.addTerm(word);
			}
			
			LinkedList<TFIDFResult> resultList = new LinkedList<TFIDFResult>();
			for(String word: wordSet){
				resultList.add(new TFIDFResult(word, df.numberOfDocuments, tf.getFrequency(word), df.getFrequency(word)));
			}
			Collections.sort(resultList);
			Collections.reverse(resultList);
			HashMap<Integer, Float> top100TFIDF = new HashMap<Integer, Float>();
			
			for(int i = 0; i < 100 && i < resultList.size(); i++){
				top100TFIDF.put(df.getWordID(resultList.get(i).token), resultList.get(i).tfidf);
			}

			if(counter % 100000 == 0){
				System.out.println(counter);
			}
		}
		br.close();

		
		System.out.println("All done. #Abstracts: " + counter);

	}

}
