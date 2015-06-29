package executable.testPrograms;

import java.io.IOException;

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

		DatasetEvaluatorSandbox sandbox = new DatasetEvaluatorSandbox();
		InputStringHandler handler = new InputStringHandler(sandbox);
		handler.handleString("Barack Obama is president of the United States.");
	}

}
