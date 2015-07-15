package main.java.executable.tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.Set;
import java.util.TreeSet;

public class SentencePicker {

	static int folds = 2;
	static int nrOfDataEntries = 1393;
	static String inputPath = "E:/Master Project/data/aida-yago2-dataset/AIDA-YAGO2-dataset.tsv";
	static String outputPath = "E:/Master Project/data/aida-yago2-dataset/aida_picked_setences.tsv";

	// static int folds = 2;
	// static int nrOfDataEntries = 50;
	// static String inputPath = "E:/Master Project/data/kore50/kore50.tsv";
	// static String outputPath = "E:/Master Project/data/kore50/kore50_fold";

	public static void main(String[] args) throws IOException {
		String line = "";
		Set<Integer> numberSet = new TreeSet<Integer>();
		BufferedReader sentenceNumberReader = new BufferedReader(new InputStreamReader(new FileInputStream("E:/Master Project/data/workingSentences_spotlight.txt")));
		while ((line = sentenceNumberReader.readLine()) != null) {
			numberSet.add(Integer.parseInt(line));
		}
		sentenceNumberReader.close();
		
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(inputPath)));
		LinkedList<String> dataList = new LinkedList<String>();

		line = "";
		int counter = 0;
		StringBuilder sb = new StringBuilder();
		while ((line = br.readLine()) != null) {
			if (line.contains("-DOCSTART-") && (!line.equals("-DOCSTART- (1 EU)") || !line.equals("-DOCSTART- (1 CEL01)"))) {
				if (!sb.toString().isEmpty() && !numberSet.contains(counter))
					dataList.add(sb.toString());
				sb = new StringBuilder();
				counter++;
			}

			if (!line.isEmpty())
				sb.append(line + "\n");
		}
		br.close();
		dataList.add(sb.toString().trim());

		System.out.println("size: " + dataList.size());

		BufferedWriter fw = new BufferedWriter(new FileWriter(outputPath));
		for (String s : dataList) {
			fw.write(s + "\n");
		}
		fw.close();
	}

}
