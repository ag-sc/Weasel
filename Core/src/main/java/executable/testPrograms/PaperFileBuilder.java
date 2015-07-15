package main.java.executable.testPrograms;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.UnsupportedEncodingException;
import java.util.Map.Entry;

import org.nustaq.serialization.FSTObjectInput;

import main.java.datatypes.InMemoryDataContainer;
import main.java.datatypes.configuration.Config;
import main.java.datatypes.tfidf.DocumentFrequency;
import main.java.iniloader.IniLoader;

public class PaperFileBuilder {

	static Config config;

	public static void main(String[] args) throws IOException, ClassNotFoundException {
		String filepath = "config.ini";
		if (args.length == 1)
			filepath = args[0];
		System.out.println("Using config file: " + filepath);
		IniLoader iniLoader = new IniLoader();
		iniLoader.parse(filepath);
		config = Config.getInstance();

		System.out.println("load inMemoryContainer");
		FileInputStream fileInputStream = new FileInputStream(config.getParameter("inMemoryDataContainerPath"));
		ObjectInputStream objectReader = new ObjectInputStream(fileInputStream);
		InMemoryDataContainer container = (InMemoryDataContainer) objectReader.readObject();
		objectReader.close();

		// buildAnchorFile(container);
		vectorMap(container);
		
		System.out.println("All done! Have a good day.");
	}

	private static void buildAnchorFile(InMemoryDataContainer container) throws ClassNotFoundException, IOException {

		BufferedWriter fw = new BufferedWriter(new FileWriter("anchors.tsv"));
		int entrySetSize = container.anchorID.entrySet().size();
		int fivePercent = entrySetSize / 20;
		int counter = 0;
		for (Entry<String, Integer> e : container.anchorID.entrySet()) {
			String anchorName = e.getKey();
			Integer anchorID = e.getValue();

			if (anchorName.isEmpty())
				continue;

			for (int i = 0; i < container.anchorToCandidates[anchorID].length; i++) {
				fw.write(anchorName + "\t" + container.idToEntity[container.anchorToCandidates[anchorID][i]] + "\t"
						+ container.anchorToCandidatesCount[anchorID][i] + "\t" + container.anchorToCandidatesProb[anchorID][i] + "\n");
			}

			counter++;
			if (counter % fivePercent == 0) {
				System.out.println(counter + " / " + entrySetSize);
				fw.flush();
			}
		}
		fw.close();

	}

	private static void vectorMap(InMemoryDataContainer container) throws IOException, ClassNotFoundException {
		String vectorMapOutputPath = config.getParameter("vectorMapPath");
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(vectorMapOutputPath + ".txt"), "UTF8"));
		String line = "";
		BufferedWriter fw = new BufferedWriter(new FileWriter("vectorMap.txt"));
		
		System.out.println("PageRank not loaded yet. Loading now...");
		String pageRankArrayPath = Config.getInstance().getParameter("pageRankArrayPath");
		FileInputStream fileInputStream = new FileInputStream(pageRankArrayPath);
		ObjectInputStream objectReader = new ObjectInputStream(fileInputStream);
		double[] pageRankArray = (double[]) objectReader.readObject();
		objectReader.close();
		fileInputStream.close();
		System.out.println("Done.");
		
		System.out.println("ReadDFObject");
		fileInputStream = new FileInputStream(config.getParameter("dfPath"));
		FSTObjectInput in = new FSTObjectInput(fileInputStream);
		DocumentFrequency df = (DocumentFrequency) in.readObject();
		in.close();
		System.out.println("Done.");

		int counter = 0;
		while ((line = br.readLine()) != null) {
			counter++;

			String[] splitLine = line.split("\t");
			if(splitLine[0].isEmpty()) continue;
			Integer entityID = Integer.parseInt(splitLine[0]);
			String entity = container.idToEntity[entityID];
			fw.write(entity + "\t" + pageRankArray[entityID] + "\n");

			// semsig
			StringBuilder sb = new StringBuilder();
			splitLine = br.readLine().split("\t");
			for (String entry : splitLine) {
				if (entry.equals("-1"))
					break;
				String targetEntity = container.idToEntity[Integer.parseInt(entry)];
				sb.append(targetEntity + "\t");
			}
			fw.write(sb.toString().trim() + "\n");

			// score
			sb = new StringBuilder();
			splitLine = br.readLine().split("\t");
			for (String entry : splitLine) {
				if (entry.equals("0"))
					break;
				sb.append(entry + "\t");
			}
			fw.write(sb.toString().trim() + "\n");

			// terms
			sb = new StringBuilder();
			splitLine = br.readLine().split("\t");
			for (String term : splitLine) {
				if (term.equals("-1"))
					break;
				String targetEntity = df.getWordFromID(Integer.parseInt(term));
				sb.append(targetEntity + "\t");
			}
			fw.write(sb.toString().trim() + "\n");

			// tf-idf
			sb = new StringBuilder();
			splitLine = br.readLine().split("\t");
			for (String entry : splitLine) {
				if (entry.equals("0.0"))
					break;
				sb.append(entry + "\t");
			}
			fw.write(sb.toString().trim() + "\n");
			fw.write("\n");
			
			br.readLine(); // read empty line
			
			if(counter % 1000000 == 0) System.out.println("Entries parsed: " + counter);
		}

		fw.close();
		br.close();
		
	}
	
}


























