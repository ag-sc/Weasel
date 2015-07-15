package main.java.executable.testPrograms;

import java.io.IOException;

import main.java.utility.Stopwatch;

import com.hp.hpl.jena.rdf.model.Model;

import main.java.nif.FOXAdapter;
import main.java.nif.ModelAdapter;
import main.java.nif.NIFAdapter;
import main.java.nif.SpotlightAdapter;
import main.java.iniloader.IniLoader;
import main.java.datasetEvaluator.DatasetEvaluator;
import main.java.datasetEvaluator.DatasetEvaluatorSandbox;
import main.java.datasetEvaluator.datasetParser.DatasetParser;
import main.java.entityLinker.InputStringHandler;

public class StringHandlerTest {

	public static void main(String[] args) throws ClassCastException, ClassNotFoundException, IOException {
		String filepath = "../config.ini";
		if (args.length == 1)
			filepath = args[0];
		System.out.println("Using config file: " + filepath);
		IniLoader iniLoader = new IniLoader();
		iniLoader.parse(filepath);
		
		DatasetEvaluatorSandbox sandbox = new DatasetEvaluatorSandbox();

		Stopwatch sw = new Stopwatch(Stopwatch.UNIT.SECONDS);
		InputStringHandler handler = new InputStringHandler();
		DatasetParser parser = DatasetParser.getInstance();
		parser.parseIntoStringHandler(handler);
		//handler.handleString("Merkel is the chancellor of Germany. She leads the Bundesrepublik.");
		Model model = handler.getModel();
		
		ModelAdapter adapter;
	
//		adapter = new FOXAdapter();
		
		
		adapter = new NIFAdapter(sandbox);
		
//		adapter = new SpotlightAdapter();
		
		adapter.linkModel(model);
		sw.stop();
		
		model.write(System.out, "Turtle");
		
		DatasetEvaluator.evaluateModel(model);
		System.out.println("Data input and evaluation time: " + sw + " seconds");
	}

}
