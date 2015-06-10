import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.LinkedList;

import datasetEvaluator.datasetParser.KORE50Parser;
import datatypes.TermFrequency;


public class TFIDFSandbox {

	public TFIDFSandbox() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) throws IOException, ClassNotFoundException {
		System.out.println("Hello");
		FileInputStream fileInputStream = new FileInputStream("../../data/Wikipedia Abstracts/documentFrequency");
		ObjectInputStream objectReader = new ObjectInputStream(fileInputStream);

		DF_Computation dfComp = (DF_Computation) objectReader.readObject(); 
		objectReader.close();
		fileInputStream.close();
		System.out.println("dfComp loaded.");
		
		FileWriter writer = new FileWriter("../../data/temp/tfidf.txt");
		TFIDF_Computation tfidfComp = new TFIDF_Computation(writer);
		tfidfComp.SetDF_Computation(dfComp);
		
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("../../data/DatasetParser/test/kore50.tsv"), "UTF8"));
		KORE50Parser parser = new KORE50Parser(br, true);

		String parsedSentence;
		Integer counter = 0;
		while((parsedSentence = parser.parseString()).length() > 0){
			tfidfComp.addDocument(counter.toString(), parsedSentence.toLowerCase());
			counter++;
		}
	}

}
