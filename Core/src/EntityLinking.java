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

//		try {
//			BufferedWriter fw = new BufferedWriter(new FileWriter("experiment_results.txt"));
//			Config config = Config.getInstance();
//			config.setParameter("vector_evaluation_lamda", "0.65");
//			
//			for (int i = 0; i <= 5; i++) {
//				double lambda = Math.min(0.2 * i, 1.0);
//				config.setParameter("vector_evaluation_pageRankWeight", Double.toString(lambda));
				String result = DatasetEvaluatorSandbox.evaluate();
//				fw.write("pagerankweight: " + lambda + "\t Correct entities: " + result + "%\n");
//				fw.flush();
//			}
//			fw.close();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

	}

}
