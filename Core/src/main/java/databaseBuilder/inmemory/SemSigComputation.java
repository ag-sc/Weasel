package main.java.databaseBuilder.inmemory;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.TreeSet;

import main.java.utility.Stopwatch;
import main.java.databaseBuilder.fileparser.WikiParser;
import main.java.datatypes.TinyEdge;
import main.java.datatypes.Tuple;

public class SemSigComputation {

	static String pageLinksFilePath = "page_links_en.nt";
	//final static String pageLinksFilePath = "../../data/Wikipedia/Pagelinks/test/merkel_wiki.txt";
	static String semSigFile = "semsig.txt";
	
	static HashMap<String, TreeSet<TinyEdge>> graphMap;
	
	final static int numberOfSteps = 100000;
	final static int frequencyThreshold = 10;
	final static double restartProbability = 0.85;
	
	private static void calcWeight(String source, TinyEdge edge){
		float weight = 1;

		TreeSet<TinyEdge> tmpSet = graphMap.get(edge.target);
		if (tmpSet != null) {
			for (TinyEdge v2 : tmpSet) {
				if (v2 == null)
					continue;
				tmpSet = graphMap.get(v2.target);
				if (tmpSet != null && tmpSet.contains(new TinyEdge(source))) {
					weight++;
				}
			}
		}
		
		edge.weight = weight;
	}
	
	private static ArrayList<Tuple<String, Integer>> randomWalk(String startNode){
		
		String currentNode = startNode;
		HashMap<String, Integer> tmpSignature = new HashMap<String, Integer>();
		ArrayList<Tuple<String, Integer>> signature = new ArrayList<Tuple<String, Integer>>();
		
		for(int i = 0; i < numberOfSteps; i++){
			if(Math.random() > restartProbability){
				float totalWeight = 0.0f;
				
				TreeSet<TinyEdge> edges = graphMap.get(currentNode);
				if(edges == null){
					currentNode = startNode;
					continue;
				}
				
				for(TinyEdge edge: edges) totalWeight += edge.weight;

				double random = Math.random();
				double accumulatedWeight = 0.0;
				
				for(TinyEdge edge: edges){
					accumulatedWeight += edge.weight / totalWeight;
					if(random < accumulatedWeight){
						if(tmpSignature.containsKey(edge.target)){
							tmpSignature.put(edge.target, tmpSignature.get(edge.target) + 1);
						}else{
							tmpSignature.put(edge.target, 1);
						}
						currentNode = edge.target;
						break;
					}
				}
				
			}else{ // restart walk
				currentNode = startNode;
			}
		}

		for (Entry<String, Integer> e : tmpSignature.entrySet()) {
			if (e.getValue() > frequencyThreshold)
				signature.add(new Tuple<String, Integer>(e.getKey(), e.getValue()));
		}

		Collections.sort(signature, new Comparator() {
			public int compare(Object o1, Object o2) {
				Tuple<String, Integer> t1 = (Tuple) o1;
				Tuple<String, Integer> t2 = (Tuple) o2;
				return t1.y.compareTo(t2.y);
			}
		});

		Collections.reverse(signature);
		return signature;
	}

	public static void run(String pageLinksPath, String semSigPath) throws IOException {
		System.out.println("Calculate semsig...");
		
		pageLinksFilePath = pageLinksPath;
		semSigFile = semSigPath;
		
		// TODO Auto-generated method stub
		graphMap = new HashMap<String, TreeSet<TinyEdge>>(30000000);
		WikiParser parser = new WikiParser(pageLinksFilePath);
		
		Stopwatch sw = new Stopwatch(Stopwatch.UNIT.SECONDS);
		String tuple[];
		int lineCounter = 0;
		parser.parseTuple(); //remove first line
		while ((tuple = parser.parseTuple()) != null) {
			lineCounter++;
			if(tuple.length != 2) continue;
			String source = tuple[0];
			String sink   = tuple[1];
			
			if(source.isEmpty() || sink.isEmpty()) continue;
			
			TreeSet<TinyEdge> tmpSet = graphMap.get(source);
			if(tmpSet == null){
				tmpSet = new TreeSet<TinyEdge>();
				tmpSet.add(new TinyEdge(sink));
				graphMap.put(source, tmpSet);
			}else{
				tmpSet.add(new TinyEdge(sink));
				graphMap.put(source, tmpSet);
			}
			
			if (lineCounter % 1000000 == 0) {
				sw.stop();
				System.out.println("Processed lines:\t" + lineCounter + "\tTime since last message: " + sw);
				sw.start();
			}
		}
		int graphSize = graphMap.size();
		System.out.println("Processed all pagelinks: #" + graphSize);
		
		parser.close();
		
		System.out.println("Calculate weights");
		lineCounter = 0;
		sw.start();
		for(Entry<String, TreeSet<TinyEdge>> entry: graphMap.entrySet()){
			
			for(TinyEdge v1: entry.getValue()){
				calcWeight(entry.getKey(), v1);
			}
			
			if (lineCounter % 100000 == 0) {
				sw.stop();
				System.out.println("Processed nodes:\t" + lineCounter + "\tTime since last message: " + sw);
				sw.start();
			}
			lineCounter++;
		}
		
		System.out.println("Processed all edge weights: #" + graphSize);
		
		System.out.println("Calculate SemSig");
		lineCounter = 0;
		BufferedWriter fw = new BufferedWriter(new FileWriter(semSigFile));
		for(Entry<String, TreeSet<TinyEdge>> entry: graphMap.entrySet()){
			ArrayList<Tuple<String, Integer>> rw = randomWalk(entry.getKey());
			fw.write(entry.getKey() + "\n");
			for(Tuple<String, Integer> t: rw) fw.write(t.x + "\t" + t.y + "\n");
			fw.write("\n");
			
			if (lineCounter % 100000 == 0) {
				sw.stop();
				System.out.println("Processed random walks:\t" + lineCounter + "\tTime since last message: " + sw);
				sw.start();
			}
			lineCounter++;
		}
		fw.close();
		
		System.out.println("All done");
	}

}
