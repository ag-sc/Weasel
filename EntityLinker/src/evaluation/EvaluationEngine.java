package evaluation;

import configuration.Config;
import databaseConnectors.DatabaseConnector;
import annotatedSentence.AnnotatedSentence;

public abstract class EvaluationEngine {

	public abstract void evaluate(AnnotatedSentence annotatedSentence);
	
	public static EvaluationEngine getInstance(DatabaseConnector dbConnector){
		Config config = Config.getInstance();
		String engine = config.getParameter("evaluator");
		switch(engine){
		case "babelfy":
			double minimumScore = Double.parseDouble(config.getParameter("minimumScoreBabelfy"));
			int maxAmbiguity = Integer.parseInt(config.getParameter("maxAmbiguityBabelfy"));
			return new BabelfyEvaluation(dbConnector, minimumScore, maxAmbiguity);
		case "random":
			return new RandomEvaluation(dbConnector);
		case "vector":
			break;
		default:
			break;
		}
		throw new IllegalArgumentException();
	}
}
