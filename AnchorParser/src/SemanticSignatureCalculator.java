import graphAccess.GraphAccess;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.TreeSet;

import datatypes.TinyEdge;

public class SemanticSignatureCalculator {

	static double restartProbability = 0.85; // 0.85
	static int frequencyThreshold = 5; // 100
	static int numberOfSteps = 50000; // 1000000
	
	public static HashMap<String, Integer> randomWalk(String startNode, GraphAccess graphAccess){
		String currentNode = startNode;
		HashMap<String, Integer> tmpSignature = new HashMap<String, Integer>();
		HashMap<String, Integer> signature = new HashMap<String, Integer>();
		
		for(int i = 0; i < numberOfSteps; i++){
			if(Math.random() > restartProbability){
				TreeSet<TinyEdge> treeSet = graphAccess.query(currentNode);
				double totalWeight = 0.0;
				
				if(treeSet != null && treeSet.size() > 0){
					for(TinyEdge edge: treeSet){
						totalWeight += edge.weight;
					}
				}else{
					currentNode = startNode;
					continue;
				}
				
				double random = Math.random();
				double accumulatedWeight = 0.0;
				
				for(TinyEdge edge: treeSet){
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
		
		for(Entry<String, Integer> e: tmpSignature.entrySet()){
			if(e.getValue() > frequencyThreshold) signature.put(e.getKey(), e.getValue());
		}
		
		return signature;
	}
	
	public static HashMap<String, HashMap<String, Integer>> calcSignature(GraphAccess graphAccess){
		HashMap<String, HashMap<String, Integer>> map = new HashMap<String, HashMap<String, Integer>>();
		Iterator<Entry<String, TreeSet<TinyEdge>>> iterator = graphAccess.getGraphIterator();
		long start, end;
		start = System.nanoTime();
		
		int counter = 0;
		while(iterator.hasNext()){
			Entry<String, TreeSet<TinyEdge>> entry = iterator.next();
			map.put(entry.getKey(), randomWalk(entry.getKey(), graphAccess));
			counter++;
			if(counter % 1000 == 0) {
				end = System.nanoTime();
				double passedTime = (end - start) / 1000000000.0;
				System.out.println("random walks completed: " + counter + " - " + passedTime + " s since last call");
				start = System.nanoTime();
			}
		}
		
		return map;
	}
}
