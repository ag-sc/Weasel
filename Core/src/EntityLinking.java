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
		IniLoader iniLoader = new IniLoader();
		iniLoader.parse();

//		try {
//			BufferedWriter fw = new BufferedWriter(new FileWriter("experiment_results.txt"));
//			Config config = Config.getInstance();
//			
//			config.setParameter("candidate_vector_boolean_scoring", "true");
//			fw.write("Boolean scoring: true");
//			for (double l = 0.0; l <= 1; l += 0.05) {
//				config.setParameter("vector_evaluation_lamda", Double.toString(l));
				String result = DatasetEvaluatorSandbox.evaluate();
//				fw.write("lambda: " + l + "\t Correct entities: " + result + "%\n");
//				fw.flush();
//			}
//			
//			config.setParameter("candidate_vector_boolean_scoring", "false");
//			fw.write("Boolean scoring: false");
//			for (double l = 0.0; l <= 1; l += 0.05) {
//				config.setParameter("vector_evaluation_lamda", Double.toString(l));
//				String result = DatasetEvaluatorSandbox.evaluate();
//				fw.write("lambda: " + l + "\t Correct entities: " + result + "%\n");
//				fw.flush();
//			}
//			
//			fw.close();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

	}

}
