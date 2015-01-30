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

		try {
			BufferedWriter fw = new BufferedWriter(new FileWriter("experiment_results.txt"));
			Config config = Config.getInstance();
			
			for (int i = 0; i <= 20; i++) {
				double lambda = Math.min(0.05 * i, 1.0);
				config.setParameter("vector_evaluation_lamda", Double.toString(lambda));
				String result = DatasetEvaluatorSandbox.evaluate();
				fw.write("lambda: " + lambda + "\t Correct entities: " + result + "%\n");
				fw.flush();
			}
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
