package datasetEvaluator;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.TreeSet;

import databaseConnectors.JDBMConnector;
import datasetParser.KORE50Parser;
import entityLinker.EntityLinker;
import evaluation.EvaluationEngine;
import evaluation.RandomEvaluator;

public class DatasetEvaluatorSandbox {

	public static void main(String[] args) {
		try {		
			FileInputStream fileInputStream = new FileInputStream("../../data/Babelfy/semantic signature.binary");
			ObjectInputStream objectReader = new ObjectInputStream(fileInputStream);
			HashMap<String, TreeSet<String>> semanticSignature = (HashMap<String, TreeSet<String>>) objectReader.readObject(); 
			objectReader.close();
			fileInputStream.close();
			
			TreeSet<String> tmp = semanticSignature.get("Angelina_Jolie");
			for(String s: tmp){
				System.out.println(s);
			}
			
			
//			KORE50Parser parser = new KORE50Parser(new File("../../data/DatasetParser/kore50.tsv"));
//			EvaluationEngine evaluator = new RandomEvaluator();
//			JDBMConnector linkerConnector = new JDBMConnector("../../data/Wikipedia Anchor/db/anchorKeyMap", "anchorKeyMap");
//			JDBMConnector checkupConnector = new JDBMConnector("../../data/Wikipedia Anchor/db/uriKeyMap", "uriKeyMap");
//			EntityLinker linker = new EntityLinker(evaluator, linkerConnector);
//			
//			DatasetEvaluator dataEvaluator = new DatasetEvaluator(parser, linker, checkupConnector);
//			dataEvaluator.evaluate();
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
