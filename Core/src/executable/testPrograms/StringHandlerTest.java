package executable.testPrograms;

import java.io.IOException;

import com.hp.hpl.jena.rdf.model.Model;

import nif.FOXAdapter;
import nif.NIFAdapter;
import iniloader.IniLoader;
import datasetEvaluator.DatasetEvaluator;
import datasetEvaluator.DatasetEvaluatorSandbox;
import datasetEvaluator.datasetParser.DatasetParser;
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
		DatasetParser parser = DatasetParser.getInstance();
		parser.parseIntoStringHandler(handler);
		//handler.handleString("Merkel is the chancellor of Germany. She leads the Bundesrepublik.");
		Model model = handler.getModel();
		
//		FOXAdapter foxAdapter = new FOXAdapter();
//		foxAdapter.linkModel(model);
		
		DatasetEvaluatorSandbox sandbox = new DatasetEvaluatorSandbox();
		NIFAdapter adapter = new NIFAdapter(sandbox);
		adapter.linkModel(model);
//		
//		System.out.println();
		model.write(System.out, "Turtle");
		
		DatasetEvaluator.evaluateModel(model);
	}

}
