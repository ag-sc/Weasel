package entityLinker.evaluation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import weka.classifiers.Classifier;
import weka.classifiers.functions.SMO;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import datatypes.configuration.Config;

public class WekaLink {
	BufferedWriter arffWriter;
	String arffFilePath;
	String modelFilePath;
	Classifier classifier;
	FastVector<Attribute> fvWekaAttributes;
	Config config;

	public WekaLink() {
		config = Config.getInstance();
		arffFilePath = config.getParameter("arffFilePath");
		modelFilePath = config.getParameter("modelFilePath");

		// detect if model file exists already
		if (config.getParameter("wekaModelStatus").equals("auto")) {
			File f = new File(modelFilePath);
			if (!config.getParameter("evaluator").equals("vector")) {
				System.out.println("The evaluation system is not set to 'vector'. WekaLink is automatically set to 'off'.");
				config.setParameter("wekaModelStatus", "off");
			} else if (f.exists() && !f.isDirectory()) {
				System.out.println("Weka model file was found. System is automatically set to 'test'.");
				config.setParameter("wekaModelStatus", "test");
			} else {
				System.out.println("No Weka model file was found. System is automatically set to 'train'.");
				config.setParameter("wekaModelStatus", "train");
			}
		}

		if (config.getParameter("wekaModelStatus").equals("off")) {
			return;
		}

		// either create .arff file or load model.
		try {
			switch (config.getParameter("wekaModelStatus")) {
			case "train":
				arffWriter = new BufferedWriter(new FileWriter(arffFilePath));
				arffWriter.write("@RELATION entityLinking\n\n");
				arffWriter.write("@ATTRIBUTE candidateVectorScore\tNUMERIC\n");
				arffWriter.write("@ATTRIBUTE tfidfScore\tNUMERIC\n");
				arffWriter.write("@ATTRIBUTE pageRank\tNUMERIC\n");
				arffWriter.write("@ATTRIBUTE candidateReferenceFrequency\tNUMERIC\n");
				arffWriter.write("@ATTRIBUTE class\t{0, 1}\n\n");
				arffWriter.write("@DATA\n");
				break;
			case "test":
				ObjectInputStream ois = new ObjectInputStream(new FileInputStream(modelFilePath));
				classifier = (SMO) ois.readObject();
				ois.close();
				break;
			default:
				System.err.println("'wekaModelStatus' variable not set correctly!");
				System.err.println("Current value: " + config.getParameter("wekaModelStatus"));
				System.err.println("Allowed values: test, train, auto");
				break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// prepare input vector
		Attribute Attribute1 = new Attribute("candidateVectorScore");
		Attribute Attribute2 = new Attribute("tfidfScore");
		Attribute Attribute3 = new Attribute("pageRank");
		Attribute Attribute4 = new Attribute("candidateReferenceFrequency");
		FastVector<String> fvClassVal = new FastVector<String>(2);
		fvClassVal.addElement("0");
		fvClassVal.addElement("1");
		Attribute ClassAttribute = new Attribute("class", fvClassVal);
		fvWekaAttributes = new FastVector<Attribute>(5);
		fvWekaAttributes.addElement(Attribute1);
		fvWekaAttributes.addElement(Attribute2);
		fvWekaAttributes.addElement(Attribute3);
		fvWekaAttributes.addElement(Attribute4);
		fvWekaAttributes.addElement(ClassAttribute);

		// prepare input vector
//		Attribute Attribute1 = new Attribute("candidateVectorScore");
//		Attribute Attribute2 = new Attribute("tfidfScore");
//		Attribute Attribute3 = new Attribute("pageRank");
//		Attribute Attribute4 = new Attribute("candidateReferenceFrequency");
//		FastVector<String> fvClassVal = new FastVector<String>(2);
//		fvClassVal.addElement("0");
//		fvClassVal.addElement("1");
//		Attribute ClassAttribute = new Attribute("class", fvClassVal);
//		fvWekaAttributes = new FastVector<Attribute>(4);
//		fvWekaAttributes.addElement(Attribute1);
//		fvWekaAttributes.addElement(Attribute2);
//		fvWekaAttributes.addElement(Attribute3);
//		fvWekaAttributes.addElement(Attribute4);
//		fvWekaAttributes.addElement(ClassAttribute);
	}

	public void writeToARFF(double candidateVectorScore, double tfidfScore, double pageRank, double candidateReferenceFrequency, String clazz) {
		try {
			 arffWriter.write(candidateVectorScore + "," + tfidfScore + "," + pageRank + "," + candidateReferenceFrequency + "," + clazz + "\n");
//			arffWriter.write(candidateVectorScore + ","  + tfidfScore + "," + pageRank + "," + clazz + "\n");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public double[] testInstance(double candidateVectorScore, double tfidfScore, double pageRank, double candidateReferenceFrequency) {
		double[] values = new double[2];
		if (!config.getParameter("wekaModelStatus").equals("test")) {
			System.err.println("'wekaModelStatus' not 'test'. Cannot test instances. Return [0, 0].");
			return values;
		}

		 Instance ins = new DenseInstance(5);
		 ins.setValue(0, candidateVectorScore);
		 ins.setValue(1, tfidfScore);
		 ins.setValue(2, pageRank);
		 ins.setValue(3, candidateReferenceFrequency);

//		Instance ins = new DenseInstance(4);
//		ins.setValue(0, candidateVectorScore);
//		ins.setValue(1, tfidfScore);
//		ins.setValue(2, pageRank);

		Instances dataUnlabeled = new Instances("TestInstances", fvWekaAttributes, 0);
		dataUnlabeled.add(ins);
		dataUnlabeled.setClassIndex(dataUnlabeled.numAttributes() - 1);

		try {
			Instance instance = dataUnlabeled.firstInstance();
			if (instance != null) {
				values = classifier.distributionForInstance(instance);
			} else {
				System.err.println("No instance for instance: " + ins.toString());
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return values;
	}

	public void finalizeTraining() {
		try {
			arffWriter.close();

			System.out.println("Generate Model...");
			Instances inst = new Instances(new BufferedReader(new FileReader(arffFilePath)));
			inst.setClassIndex(inst.numAttributes() - 1);
			SMO smo = new SMO();
			smo.setOptions(weka.core.Utils.splitOptions("-M"));
			Classifier cls = smo;
			cls.buildClassifier(inst);
			// serialize model
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(modelFilePath));
			oos.writeObject(cls);
			oos.flush();
			oos.close();
			System.out.println("Done.");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(-1);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(-1);
		}
	}
}
