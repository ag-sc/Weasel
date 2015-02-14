import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import configuration.Config;
import datasetEvaluator.DatasetEvaluatorSandbox;


public class EntityLinking {

	public EntityLinking() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		String filepath = "../config.ini";
		if(args.length == 1) filepath = args[0];
		System.out.println("Using config file: " + filepath);
		IniLoader iniLoader = new IniLoader();
		iniLoader.parse(filepath);
		double result = 0;
		
//		result = DatasetEvaluatorSandbox.evaluate();
		
		try {
			BufferedWriter fw = new BufferedWriter(new FileWriter("experiment_results.txt"));
			Config config = Config.getInstance();

			double bestScore = 0;
			String bestScoreString = "";
			
			for (int pageRankFactor = 0; pageRankFactor <= 20; pageRankFactor++) {
				double pageRank = Math.min(0.05 * pageRankFactor, 1.0);
				config.setParameter("vector_evaluation_pageRankWeight", Double.toString(pageRank));
				
				
				for (int lamdaFactor = 0; lamdaFactor <= 20; lamdaFactor++) {
					double lambda = Math.min(0.05 * lamdaFactor, 1.0);
					config.setParameter("vector_evaluation_lamda", Double.toString(lambda));
					
					
//					for(int boolScoringCounter = 0; boolScoringCounter <= 1; boolScoringCounter++){
						boolean boolScoring = false;
//						if(boolScoringCounter == 0) boolScoring = false;
//						else boolScoring = true;
						config.setParameter("candidate_vector_boolean_scoring", Boolean.toString(boolScoring));

						
//						for(int redirectsAsCorrectCounter = 0; redirectsAsCorrectCounter <= 1; redirectsAsCorrectCounter++){
							boolean redirectsAsCorrect = true;
//							if(redirectsAsCorrectCounter == 0) redirectsAsCorrect = false;
//							else redirectsAsCorrect = true;
							config.setParameter("countRedirectsAsCorrect", Boolean.toString(redirectsAsCorrect));
							

							result = DatasetEvaluatorSandbox.evaluate();
							fw.write(" Correct entities: " + result + "%");
							fw.write(" pageRankFactor: " + pageRank);
							fw.write(" lamdaFactor: " + lambda);
							fw.write(" boolScoring: " + boolScoring);
							fw.write(" redirectsAsCorrect: " + redirectsAsCorrect + "\n");
							fw.flush();
							
							if(result > bestScore){
								bestScore = result;
								bestScoreString = "PageRankWeight: " + pageRank + " Lamda: " + lamdaFactor + " boolScoring" + boolScoring + " countRedirectsAsCorrect: " + redirectsAsCorrect;
							}
//						}
//					}
				}
			}

			
			fw.write("Best result: " + Double.toString(bestScore) + "\n");
			fw.write(bestScoreString);
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
