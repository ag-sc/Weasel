package datasetEvaluator;

import java.io.IOException;
import java.sql.SQLException;

import configuration.Config;
import stopwatch.Stopwatch;
import databaseConnectors.H2Connector;
import databaseConnectors.H2PAConnector;
import datasetParser.DatasetParser;
import entityLinker.EntityLinker;
import evaluation.BabelfyEvaluation;
import evaluation.EvaluationEngine;
import evaluation.VectorEvaluation;

public class DatasetEvaluatorSandbox {

	public static void evaluate() {
		try {
			Config config = Config.getInstance();
			
			// TestSet
			DatasetParser parser = DatasetParser.getInstance();
			
			String dbPathH2 = config.getParameter("H2Path");
			String partialAnchorSQL = "SELECT Anchor FROM PartialAnchorToAnchor where partialAnchorID is (select id from partialAnchorID where partialAnchor is (?))";
			H2Connector partialAnchors = new H2PAConnector(dbPathH2, "sa", "", partialAnchorSQL);
			String anchorSQL = "SELECT EntityIdList FROM AnchorToEntity where id is (select id from AnchorID where anchor is (?))";
			H2Connector anchors = new H2Connector(dbPathH2, "sa", "", anchorSQL);
			
			String entityToEntitySQL = "select entitySinkIDList from EntityToEntity where EntitySourceID is (?)";
			H2Connector semSigConnector = new H2Connector(dbPathH2, "sa", "", entityToEntitySQL);
//			EvaluationEngine evaluator = EvaluationEngine.getInstance(semSigConnector);
			EvaluationEngine evaluator = new VectorEvaluation(semSigConnector, 
					"../../data/smallVector.map",
					"../../data/Wikipedia Abstracts/documentFrequency");
			
			// Linker & Evaluation
			//System.out.println("About to start evaluation.");
			String stopwordsPath = config.getParameter("stopwordsPath");
			EntityLinker linker = new EntityLinker(evaluator, anchors, partialAnchors, stopwordsPath);
			DatasetEvaluator dataEvaluator = new DatasetEvaluator(parser, linker, anchors); // checkupConnector);
			Stopwatch sw = new Stopwatch(Stopwatch.UNIT.MINUTES);
			dataEvaluator.evaluate();
			System.out.println("Evaluation time: " + sw.stop() + " minutes");
//			System.out.println("Lookup Time: " + ((BabelfyEvaluation)evaluator).lookUpTime + " ms");
//			System.out.println("search Time: " + ((BabelfyEvaluation)evaluator).searchSetTime + " ms");
				
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
