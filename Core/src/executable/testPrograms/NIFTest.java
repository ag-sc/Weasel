package executable.testPrograms;

import nif.NIFAdapter;
import iniloader.IniLoader;
import datasetEvaluator.DatasetEvaluatorSandbox;

public class NIFTest {

	public static void main(String[] args) {
		String filepath = "../config.ini";
		if (args.length == 1)
			filepath = args[0];
		System.out.println("Using config file: " + filepath);
		IniLoader iniLoader = new IniLoader();
		iniLoader.parse(filepath);

		DatasetEvaluatorSandbox sandbox = new DatasetEvaluatorSandbox();
//		NIFAdapter adapter = new NIFAdapter("E:/Master Project/data/NIF/nifTest.ttl", sandbox);
		NIFAdapter adapter = new NIFAdapter("E:/Master Project/data/kore50/kore50.ttl", sandbox);
		adapter.linkModel();
	}

}
