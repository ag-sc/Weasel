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
			KORE50Parser parser = new KORE50Parser(new File("../../data/DatasetParser/parserTests/kore50.tsv"));
			EvaluationEngine evaluator = new RandomEvaluator();
			JDBMConnector connector = new JDBMConnector("../../data/Wikipedia Anchor/db/anchorKeyMap", "anchorKeyMap");
			EntityLinker linker = new EntityLinker(evaluator, connector);
			
			DatasetEvaluator dataEvaluator = new DatasetEvaluator(parser, linker, connector);
			dataEvaluator.evaluate();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
