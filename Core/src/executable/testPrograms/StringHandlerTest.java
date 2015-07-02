package executable.testPrograms;

import java.io.IOException;

import com.hp.hpl.jena.rdf.model.Model;

import nif.FOXAdapter;
import nif.ModelAdapter;
import nif.SpotlightAdapter;
import iniloader.IniLoader;
import datasetEvaluator.DatasetEvaluator;
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
		
		ModelAdapter adapter;
	
//		adapter = new FOXAdapter();
		
//		DatasetEvaluatorSandbox sandbox = new DatasetEvaluatorSandbox();
//		adapter = new NIFAdapter(sandbox);
		
		adapter = new SpotlightAdapter();
		
		adapter.linkModel(model);
		
		model.write(System.out, "Turtle");
		
		DatasetEvaluator.evaluateModel(model);
	}

}
