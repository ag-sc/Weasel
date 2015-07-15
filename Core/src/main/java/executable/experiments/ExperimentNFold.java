package main.java.executable.experiments;

import main.java.iniloader.IniLoader;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Date;

import main.java.nif.ModelAdapter;
import main.java.nif.NIFAdapter;

import com.hp.hpl.jena.rdf.model.Model;

import main.java.utility.Stopwatch;
import main.java.datasetEvaluator.DatasetEvaluator;
import main.java.datasetEvaluator.DatasetEvaluatorSandbox;
import main.java.datasetEvaluator.datasetParser.DatasetParser;
import main.java.datatypes.configuration.Config;
import main.java.entityLinker.InputStringHandler;

public class ExperimentNFold {

	static int experimentNumber = 9;
	static String dataset = "aida";
	static int numberOfFolds = 10;

	public static void main(String[] args) {
		String filepath = "../config.ini";
		if (args.length == 1)
			filepath = args[0];
		System.out.println("Using config file: " + filepath);
		IniLoader iniLoader = new IniLoader();
		iniLoader.parse(filepath);
		double result = 0;

		double resultSum = 0.0;
		double[] results = new double[3];
		Config config = Config.getInstance();
		config.setParameter("wekaModelStatus", "train");

		try {
			BufferedWriter fw = new BufferedWriter(new FileWriter("experiment_" + experimentNumber + ".txt"));
			Date date = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			String formattedDate = sdf.format(date);
			fw.write(formattedDate + "\n");
			fw.write("Experiment number " + experimentNumber + "(" + config.getParameter("datasetPath") + ")\n\n");
			fw.write("id\tcorrect\t\tTime\n");
			
			Stopwatch swTotal = new Stopwatch(Stopwatch.UNIT.MINUTES);
			for (int trainFold = 0; trainFold < numberOfFolds; trainFold++) {
				Stopwatch sw = new Stopwatch(Stopwatch.UNIT.SECONDS);

				
				sw.start();
				
				// Train
				System.out.println("Start training...");
				config.setParameter("wekaModelStatus", "train");
				config.setParameter("datasetPath", "/home/felix/data/kore10fold/kore50_fold_" + trainFold + "_testset.tsv");
//				config.setParameter("datasetPath", "/home/felix/data/aida10fold/aida_fold_" + trainFold + "_testset.tsv");
//				config.setParameter("datasetPath", "/home/felix/data/aidaTestSentence.tsv");
				//				config.setParameter("datasetPath", "E:/Master Project/data/aida-yago2-dataset/aida_fold_" + trainFold + "_testset.tsv");
				//				result = sandbox.evaluate();
				InputStringHandler handler = new InputStringHandler();
				DatasetParser parser = DatasetParser.getInstance();
				parser.parseIntoStringHandler(handler);
				Model model = handler.getModel();
				DatasetEvaluatorSandbox sandbox = new DatasetEvaluatorSandbox();
				ModelAdapter adapter = new NIFAdapter(sandbox);
				adapter.linkModel(model);
//				model.write(System.out, "Turtle");	
				DatasetEvaluator.evaluateModel(model);
//				model.write(System.out, "Turtle");	
				
				// Test
				System.out.println("Start testing...");
				config.setParameter("wekaModelStatus", "test");
				config.setParameter("datasetPath", "/home/felix/data/kore10fold/kore50_fold_" + trainFold + ".tsv");
//				config.setParameter("datasetPath", "/home/felix/data/aida10fold/aida_fold_" + trainFold + ".tsv");
//				result = sandbox.evaluate();
//				resultSum += result;
				handler = new InputStringHandler();
				parser = DatasetParser.getInstance();
				parser.parseIntoStringHandler(handler);
				model = handler.getModel();
				sandbox = new DatasetEvaluatorSandbox();
				adapter = new NIFAdapter(sandbox);
				adapter.linkModel(model);
				System.out.println("Model for fold " + trainFold);
				model.write(System.out, "Turtle");	
				double[] tmp = DatasetEvaluator.evaluateModel(model);
				results[0] += tmp[0];
				results[1] += tmp[1];
				results[2] += tmp[2];
				
				sw.stop();
				result = round(result, 4);
				
				fw.write("Precision: " + (tmp[0] * 100) + " %\n");
				fw.write("Recall: " + (tmp[1] * 100) + " %\n");
				fw.write("F-Measure: " + (tmp[2]) + "\n");				
				fw.write(round(sw.doubleTime, 4) + " s\n");
				fw.flush();
			}

			fw.write("\n");
			swTotal.stop();
			fw.write("Total time: " + round(swTotal.doubleTime, 4) + " minutes.\n");
			//resultSum /= (double)numberOfFolds;
			//fw.write("Average of the "+ numberOfFolds +" results: " + resultSum + "%");
			System.out.println("Average Precision: " + (results[0] * 10) + " %");
			System.out.println("Average Recall: " + (results[1] * 10) + " %");
			System.out.println("Average F-Measure: " + (results[2] / 10));
			
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static double round(double value, int places) {
		if (places < 0)
			throw new IllegalArgumentException();

		BigDecimal bd = new BigDecimal(value);
		bd = bd.setScale(places, RoundingMode.HALF_UP);
		return bd.doubleValue();
	}

}
