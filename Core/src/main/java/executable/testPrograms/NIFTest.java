package main.java.executable.testPrograms;

import main.java.nif.NIFAdapter;
import main.java.iniloader.IniLoader;
import main.java.datasetEvaluator.DatasetEvaluatorSandbox;

public class NIFTest {

	public static void main(String[] args) {
		String filepath = "../config.ini";
		if (args.length == 1)
			filepath = args[0];
		System.out.println("Using config file: " + filepath);
		IniLoader iniLoader = new IniLoader();
		iniLoader.parse(filepath);

		DatasetEvaluatorSandbox sandbox = new DatasetEvaluatorSandbox();
		NIFAdapter adapter = new NIFAdapter(sandbox);
//		NIFAdapter adapter = new NIFAdapter("E:/Master Project/data/kore50/kore50.ttl", sandbox);
		//adapter.linkModel();
	}

}
