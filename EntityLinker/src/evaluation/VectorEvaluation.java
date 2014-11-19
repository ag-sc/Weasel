package evaluation;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import tfidf.DocumentFrequency;
import databaseConnectors.DatabaseConnector;
import databaseConnectors.H2Connector;
import datatypes.Tuple;
import annotatedSentence.AnnotatedSentence;

public class VectorEvaluation extends EvaluationEngine {

	private DatabaseConnector semanticSignatureDB;
	HashMap<Integer, Tuple<ArrayList<Integer>, HashMap<Integer, Float>>> vectorMap;
	DocumentFrequency df;
	
	public VectorEvaluation(DatabaseConnector semanticSignatureDB, String vectorMapFilePath, String dfFilePath) throws IOException, ClassNotFoundException {
		this.semanticSignatureDB = semanticSignatureDB;
		
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
		System.out.println("Entities:");
		for(Integer i: vectorMap.get(371).x){
			System.out.println(h2.resolveID(i.toString()));
		}
		System.out.println("\nWord frequencies:");
		for(Entry<Integer, Float> e: vectorMap.get(371).y.entrySet()){
			System.out.println(df.getWordFromID(e.getKey()) + " - tf/idf: " + e.getValue());
		}

	}

}
