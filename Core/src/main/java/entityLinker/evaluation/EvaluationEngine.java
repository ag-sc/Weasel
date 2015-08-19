package main.java.entityLinker.evaluation;

import main.java.datatypes.VectorMap;
import main.java.datatypes.annotatedSentence.AnnotatedSentence;
import main.java.datatypes.configuration.Config;
import main.java.datatypes.databaseConnectors.DatabaseConnector;

public abstract class EvaluationEngine {
	
	static VectorMap vectorMap = null;

	public abstract void evaluate(AnnotatedSentence annotatedSentence);

	public static EvaluationEngine getInstance(DatabaseConnector dbConnector, WekaLink wekaLink) {
		Config config = Config.getInstance();
		String engine = config.getParameter("evaluator");
		try {
			switch (engine) {
			case "babelfy":
				double minimumScore = Double.parseDouble(config.getParameter("minimumScoreBabelfy"));
				int maxAmbiguity = Integer.parseInt(config.getParameter("maxAmbiguityBabelfy"));
				return new BabelfyEvaluation(dbConnector, minimumScore, maxAmbiguity);
			case "random":
				return new RandomEvaluation(dbConnector);
			case "vector":
				if(vectorMap == null){
					vectorMap = new VectorMap();
				}
				return new VectorEvaluation(dbConnector, vectorMap, config.getParameter("dfPath"), wekaLink);
			case "spotlight":
				return new DBpediaSpotlightEvaluation();
			default:
				System.err.println("Evaluation engine '" + engine + "' not recognized. Terminating.");
				System.exit(-2);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
		throw new IllegalArgumentException();

	}
}
