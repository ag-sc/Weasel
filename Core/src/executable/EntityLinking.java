package executable;

import iniloader.IniLoader;

import datasetEvaluator.DatasetEvaluatorSandbox;

public class EntityLinking {

	public EntityLinking() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		String filepath = "../config.ini";
		if (args.length == 1)
			filepath = args[0];
		System.out.println("Using config file: " + filepath);
		IniLoader iniLoader = new IniLoader();
		iniLoader.parse(filepath);

		DatasetEvaluatorSandbox.evaluate();

	}

}
