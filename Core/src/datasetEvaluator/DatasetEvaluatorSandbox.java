package datasetEvaluator;

import java.io.IOException;
import java.sql.SQLException;

import utility.Stopwatch;
import datatypes.annotatedSentence.AnnotatedSentence;
import datatypes.configuration.Config;
import datatypes.databaseConnectors.ConnectorFactory;
import datatypes.databaseConnectors.DatabaseConnector;
import entityLinker.EntityLinker;
import entityLinker.evaluation.EvaluationEngine;
import entityLinker.evaluation.WekaLink;

public class DatasetEvaluatorSandbox {

	private DatabaseConnector anchorConnector;
	private EvaluationEngine evaluator;
	private EntityLinker linker;
	private WekaLink wekaLink;

	public DatasetEvaluatorSandbox() {
		Config config = Config.getInstance();

		// Get correct DB connector
		String connectorType = config.getParameter("dbConnector");
		try {
			switch (connectorType) {
			case "H2":
				String sql = "SELECT EntityIdList FROM AnchorToEntity where id is (select id from AnchorID where anchor is (?))";
				anchorConnector = ConnectorFactory.getH2Connector(config.getParameter("H2Path"), sql);
				break;
			case "inMemory":
				anchorConnector = ConnectorFactory.getInMemoryConnector(config.getParameter("inMemoryDataContainerPath"));
				break;
			default:
				System.err.println("Connector type '" + connectorType + "' not recognized. Terminating.");
				System.exit(-2);
			}

			// Chose evaluation engine
			evaluator = null;
			if (!config.getParameter("wekaModelStatus").equals("off"))
				wekaLink = new WekaLink();
			if (connectorType.equals("H2")) {
				String sql = "select entitySinkIDList from EntityToEntity where EntitySourceID is (?)";
				DatabaseConnector connector = ConnectorFactory.getH2Connector(config.getParameter("H2Path"), sql);
				evaluator = EvaluationEngine.getInstance(connector, wekaLink);
			} else {
				evaluator = EvaluationEngine.getInstance(anchorConnector, wekaLink);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
		// Linker
		String stopwordsPath = config.getParameter("stopwordsPath");
		linker = new EntityLinker(evaluator, anchorConnector, stopwordsPath);
	}
	
	public AnnotatedSentence evaluateSentence(AnnotatedSentence input){
		return linker.link(input);
	}

	public double evaluate() {
		try {
			Config config = Config.getInstance();

			// Evaluation
			DatasetEvaluator dataEvaluator = new DatasetEvaluator(linker, anchorConnector); // checkupConnector);
			Stopwatch sw = new Stopwatch(Stopwatch.UNIT.MINUTES);
			double result = dataEvaluator.evaluate();
			System.out.println("Evaluation time (including data load): " + sw.stop() + " minutes");

			// build weka model if applicable
			if (wekaLink != null && config.getParameter("wekaModelStatus").equals("train")) {
				wekaLink.finalizeTraining();
			}

			return result;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return -1;
	}

}
