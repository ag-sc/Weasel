package datasetEvaluator;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.TreeSet;

import databaseConnectors.JDBMConnector;
import databaseConnectors.Neo4jConnector;
import datasetParser.KORE50Parser;
import entityLinker.EntityLinker;
import evaluation.BabelfyEvaluation;
import evaluation.EvaluationEngine;
import evaluation.RandomEvaluator;

public class DatasetEvaluatorSandbox {

	public static void main(String[] args) {
		try {					
			KORE50Parser parser = new KORE50Parser(new File("../../data/DatasetParser/test/kore50.tsv"));
			Neo4jConnector connector = new Neo4jConnector("../../data/DBs/InfoboxPlusCategories");
			EvaluationEngine evaluator = new BabelfyEvaluation(connector, 0.8, 10);
			JDBMConnector linkerConnector = new JDBMConnector("../../data/Wikipedia Anchor/db/anchorKeyMap", "anchorKeyMap");
			JDBMConnector checkupConnector = new JDBMConnector("../../data/Wikipedia Anchor/db/uriKeyMap", "uriKeyMap");
			EntityLinker linker = new EntityLinker(evaluator, linkerConnector);
			
			DatasetEvaluator dataEvaluator = new DatasetEvaluator(parser, linker, checkupConnector);
			dataEvaluator.evaluate();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
