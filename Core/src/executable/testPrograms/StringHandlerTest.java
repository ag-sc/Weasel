package executable.testPrograms;

import java.io.IOException;

import nif.NIFAdapter;
import iniloader.IniLoader;
import datasetEvaluator.DatasetEvaluatorSandbox;
import entityLinker.InputStringHandler;

public class StringHandlerTest {

	public static void main(String[] args) throws ClassCastException, ClassNotFoundException, IOException {
		String filepath = "../config.ini";
		if (args.length == 1)
			filepath = args[0];
		System.out.println("Using config file: " + filepath);
		IniLoader iniLoader = new IniLoader();
		iniLoader.parse(filepath);

		InputStringHandler handler = new InputStringHandler();
		handler.handleString("Barack Obama is president of the United States.");
		
		DatasetEvaluatorSandbox sandbox = new DatasetEvaluatorSandbox();
		NIFAdapter adapter = new NIFAdapter(handler.getModel(), sandbox);
		adapter.linkModel();
	}

}
