package datasetEvaluator;

import java.io.IOException;
import java.sql.SQLException;

import configuration.Config;
import stopwatch.Stopwatch;
import databaseConnectors.DatabaseConnector;
import databaseConnectors.H2Connector;
import databaseConnectors.H2PAConnector;
import databaseConnectors.InMemoryConnector;
import datasetParser.DatasetParser;
import entityLinker.EntityLinker;
import evaluation.BabelfyEvaluation;
import evaluation.EvaluationEngine;
import evaluation.VectorEvaluation;

public class DatasetEvaluatorSandbox {

	public static String evaluate() {
		try {
			Config config = Config.getInstance();
			
			// TestSet
			DatasetParser parser = DatasetParser.getInstance();
			
			String dbPathH2 = config.getParameter("H2Path");
			String anchorSQL = "SELECT EntityIdList FROM AnchorToEntity where id is (select id from AnchorID where anchor is (?))";
//			DatabaseConnector anchors = new H2Connector(dbPathH2, "sa", "", anchorSQL);
			DatabaseConnector anchors = new InMemoryConnector(config.getParameter("inMemoryDataContainerPath"));
			
//			String entityToEntitySQL = "select entitySinkIDList from EntityToEntity where EntitySourceID is (?)";
//			DatabaseConnector semSigConnector = new H2Connector(dbPathH2, "sa", "", entityToEntitySQL);
//			EvaluationEngine evaluator = EvaluationEngine.getInstance(semSigConnector);
			EvaluationEngine evaluator = new VectorEvaluation(anchors, 
					config.getParameter("vectorMapPath"),
					config.getParameter("dfPath"));
			
			// Linker & Evaluation
			//System.out.println("About to start evaluation.");
			String stopwordsPath = config.getParameter("stopwordsPath");
			EntityLinker linker = new EntityLinker(evaluator, anchors, stopwordsPath);
			DatasetEvaluator dataEvaluator = new DatasetEvaluator(parser, linker, anchors); // checkupConnector);
			Stopwatch sw = new Stopwatch(Stopwatch.UNIT.MINUTES);
			String result = dataEvaluator.evaluate();
			System.out.println("Evaluation time (including data load): " + sw.stop() + " minutes");
//			System.out.println("Lookup Time: " + ((BabelfyEvaluation)evaluator).lookUpTime + " ms");
//			System.out.println("search Time: " + ((BabelfyEvaluation)evaluator).searchSetTime + " ms");
			return result;
				
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
//		catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		return "No result - try catch fail?";
	}

}
