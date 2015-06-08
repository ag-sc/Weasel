import iniloader.IniLoader;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;

import weka.classifiers.Classifier;
import weka.classifiers.functions.LibSVM;
import weka.classifiers.functions.SMO;
import weka.classifiers.trees.J48;
import weka.core.Instances;
import configuration.Config;
import datasetEvaluator.DatasetEvaluatorSandbox;

public class EntityLinking {

	static String pathToARFF = "defaultName.arff";

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
		double result = 0;

		Config config = Config.getInstance();
		String wekaModelStatus = config.getParameter("wekaModelStatus");
		pathToARFF = config.getParameter("arffModelName") + ".arff";
		
		// load model
		if (wekaModelStatus.equals("test")) {
			try {
				System.out.println("Generate Model...");
				Instances inst = new Instances(new BufferedReader(new FileReader(pathToARFF)));
				inst.setClassIndex(inst.numAttributes() - 1);
				SMO smo = new SMO();
				smo.setOptions(weka.core.Utils.splitOptions("-M"));
				Classifier cls = smo;
				// Classifier cls = new J48();
				cls.buildClassifier(inst);
				// serialize model
				// ObjectOutputStream oos = new ObjectOutputStream(
				// new FileOutputStream(pathToARFF.replace(".arff", ".model")));
				// oos.writeObject(cls);
				// oos.flush();
				// oos.close();

				config.cls = cls;
				System.out.println("Done.");

				// config.cls = (Classifier)
				// weka.core.SerializationHelper.read(pathToARFF.replace(".arff",
				// ".model"));
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				System.err.println("Problem loading weka classifier model. Switch to train mode.");
				config.setParameter("wekaModelStatus", "train");
			}
		}

		BufferedWriter fw = null;
		if (wekaModelStatus.equals("train")) {
			config.instanciateArffWriter(pathToARFF);
			fw = config.getArffWriter();
			try {
				fw.write("@RELATION entityLinking\n\n");
				fw.write("@ATTRIBUTE candidateVectorScore\tNUMERIC\n");
				fw.write("@ATTRIBUTE tfidfScore\tNUMERIC\n");
				fw.write("@ATTRIBUTE pageRank\tNUMERIC\n");
				fw.write("@ATTRIBUTE candidateReferenceFrequency\tNUMERIC\n");
				fw.write("@ATTRIBUTE class\t{0, 1}\n\n");
				fw.write("@DATA\n");
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

		result = DatasetEvaluatorSandbox.evaluate();

		if (wekaModelStatus.equals("train")) {
			try {
				fw.flush();
				fw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// try {
		// BufferedWriter fw = new BufferedWriter(new
		// FileWriter("experiment_results.txt"));
		// Config config = Config.getInstance();
		//
		// double bestScore = 0;
		// String bestScoreString = "";
		//
		// for (int pageRankFactor = 0; pageRankFactor <= 20; pageRankFactor++)
		// {
		// double pageRank = Math.min(0.05 * pageRankFactor, 1.0);
		// config.setParameter("vector_evaluation_pageRankWeight",
		// Double.toString(pageRank));
		//
		//
		// for (int lamdaFactor = 0; lamdaFactor <= 20; lamdaFactor++) {
		// double lambda = Math.min(0.05 * lamdaFactor, 1.0);
		// config.setParameter("vector_evaluation_lamda",
		// Double.toString(lambda));
		//
		//
		// // for(int boolScoringCounter = 0; boolScoringCounter <= 1;
		// boolScoringCounter++){
		// boolean boolScoring = false;
		// // if(boolScoringCounter == 0) boolScoring = false;
		// // else boolScoring = true;
		// config.setParameter("candidate_vector_boolean_scoring",
		// Boolean.toString(boolScoring));
		//
		//
		// // for(int redirectsAsCorrectCounter = 0; redirectsAsCorrectCounter
		// <= 1; redirectsAsCorrectCounter++){
		// boolean redirectsAsCorrect = true;
		// // if(redirectsAsCorrectCounter == 0) redirectsAsCorrect = false;
		// // else redirectsAsCorrect = true;
		// config.setParameter("countRedirectsAsCorrect",
		// Boolean.toString(redirectsAsCorrect));
		//
		//
		// result = DatasetEvaluatorSandbox.evaluate();
		// fw.write(" Correct entities: " + result + "%");
		// fw.write(" pageRankFactor: " + pageRank);
		// fw.write(" lamdaFactor: " + lambda);
		// fw.write(" boolScoring: " + boolScoring);
		// fw.write(" redirectsAsCorrect: " + redirectsAsCorrect + "\n");
		// fw.flush();
		//
		// if(result > bestScore){
		// bestScore = result;
		// bestScoreString = "PageRankWeight: " + pageRank + " Lamda: " +
		// lamdaFactor + " boolScoring" + boolScoring +
		// " countRedirectsAsCorrect: " + redirectsAsCorrect;
		// }
		// // }
		// // }
		// }
		// }
		//
		//
		// fw.write("Best result: " + Double.toString(bestScore) + "\n");
		// fw.write(bestScoreString);
		// fw.close();
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }

	}

}
