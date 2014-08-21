package datasetEvaluator;

import java.io.File;
import java.io.IOException;

import databaseConnectors.JDBMConnector;
import datasetParser.KORE50Parser;
import entityLinker.EntityLinker;
import evaluation.EvaluationEngine;
import evaluation.RandomEvaluator;

public class DatasetEvaluatorSandbox {

	public static void main(String[] args) {
		try {
			KORE50Parser parser = new KORE50Parser(new File("../../data/DatasetParser/kore50.tsv"));
			EvaluationEngine evaluator = new RandomEvaluator();
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
