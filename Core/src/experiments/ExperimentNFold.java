package experiments;

import iniloader.IniLoader;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Date;

import stopwatch.Stopwatch;
import weka.classifiers.Classifier;
import weka.classifiers.functions.SMO;
import weka.core.Instances;
import configuration.Config;
import datasetEvaluator.DatasetEvaluatorSandbox;

public class ExperimentNFold {

	static int experimentNumber = 2;
	static String dataset = "aida";
	static int numberOfFolds = 10;

	public static void main(String[] args) {
		String filepath = "config.ini";
		if (args.length == 1)
			filepath = args[0];
		System.out.println("Using config file: " + filepath);
		IniLoader iniLoader = new IniLoader();
		iniLoader.parse(filepath);
		double result = 0;

		double resultSum = 0.0;
		Config config = Config.getInstance();
		String pathToARFF = config.getParameter("arffModelName") + ".arff";

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
//				config.setParameter("datasetPath", "/home/felix/data/aida10fold/aida_fold_" + trainFold + ".tsv");
//				config.setParameter("wekaModelStatus", "train");
//				try {
//					config.instanciateArffWriter(pathToARFF);
//					BufferedWriter fw2 = config.getArffWriter();
//					fw2.write("@RELATION entityLinking\n\n");
//					fw2.write("@ATTRIBUTE candidateVectorScore\tNUMERIC\n");
//					fw2.write("@ATTRIBUTE tfidfScore\tNUMERIC\n");
//					fw2.write("@ATTRIBUTE pageRank\tNUMERIC\n");
//					fw2.write("@ATTRIBUTE candidateReferenceFrequency\tNUMERIC\n");
//					fw2.write("@ATTRIBUTE class\t{0, 1}\n\n");
//					fw2.write("@DATA\n");
//					DatasetEvaluatorSandbox.evaluate();
//					fw2.close();
//				} catch (IOException e1) {
//					// TODO Auto-generated catch block
//					e1.printStackTrace();
//				}
//				config.setParameter("wekaModelStatus", "test");
				
				// Build Model
//				Instances inst = new Instances(new BufferedReader(new FileReader(pathToARFF)));
//				inst.setClassIndex(inst.numAttributes() - 1);
//				SMO smo = new SMO();
//				smo.setOptions(weka.core.Utils.splitOptions("-M"));
//				Classifier cls = smo;
//				cls.buildClassifier(inst);
//				config.cls = cls;
				
				// Test
				config.setParameter("datasetPath", "/home/felix/data/aida10fold/aida_fold_" + trainFold + "_testset.tsv");
				result = DatasetEvaluatorSandbox.evaluate();
				resultSum += result;
				
				sw.stop();
				result = round(result, 4);
				fw.write(trainFold + ":\t" + result + "%\t" + round(sw.doubleTime, 4) + " s\n");
				fw.flush();
			}

			fw.write("\n");
			swTotal.stop();
			fw.write("Total time: " + round(swTotal.doubleTime, 4) + " minutes.\n");
			resultSum /= (double)numberOfFolds;
			fw.write("Average of the "+ numberOfFolds +" results: " + resultSum + "%");
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
