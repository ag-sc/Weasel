package entityLinker.evaluation;

import datatypes.annotatedSentence.AnnotatedSentence;
import datatypes.configuration.Config;
import datatypes.databaseConnectors.DatabaseConnector;

public abstract class EvaluationEngine {

	public abstract void evaluate(AnnotatedSentence annotatedSentence);

	public static EvaluationEngine getInstance(DatabaseConnector dbConnector) {
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
				return new VectorEvaluation(dbConnector, config.getParameter("vectorMapPath"), config.getParameter("dfPath"));
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
