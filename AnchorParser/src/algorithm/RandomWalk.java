package algorithm;

import graphAccess.GraphAccess;

import java.util.HashMap;
import java.util.TreeSet;
import java.util.Map.Entry;

import neo4j.Neo4jCore.RelTypes;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

import datatypes.TinyEdge;



public class RandomWalk {
	private static int frequencyThreshold;
	private static int numberOfSteps;
	private static double restartProbability;
	private static int nodeCounter = 0;
	
	private static void explore(Node n, double currentProbability, HashMap<Node, Double> signature, double continueProb) {
		nodeCounter++;
		double totalWeight = 0.0;
		for (Relationship r : n.getRelationships(Direction.OUTGOING, RelTypes.LINK_TO)) {
			totalWeight += ((Integer) r.getProperty("weight", 0)).doubleValue();
		}

		for (Relationship r : n.getRelationships(Direction.OUTGOING, RelTypes.LINK_TO)) {
			double tmp = (((Integer) r.getProperty("weight", 0)).doubleValue() / totalWeight) * continueProb * currentProbability;
			if (tmp > (double) frequencyThreshold / (double) numberOfSteps) {
				Double foundValue = signature.get(r.getEndNode());
				if (foundValue == null || foundValue < tmp)
					signature.put(r.getEndNode(), tmp);
				explore(r.getEndNode(), tmp, signature, (1.0 - restartProbability));
			}
		}
	}
	
	public static HashMap<Node, Double> calcProbSignature(Node source, int numberOfSteps_in, int frequencyThreshold_in, double restartProbability_in){
		frequencyThreshold = frequencyThreshold_in;
		numberOfSteps = numberOfSteps_in;
		restartProbability = restartProbability_in;
		
		HashMap<Node, Double> tmpSignature = new HashMap<Node, Double>();
		nodeCounter = 0;
		explore(source, 1, tmpSignature, 1);
		System.out.println(nodeCounter);
		return tmpSignature;
	}
	
	public static HashMap<Node, Double> randomWalk(Node startNode, int numberOfSteps_in, int frequencyThreshold_in, double restartProbability_in){
		frequencyThreshold = frequencyThreshold_in;
		numberOfSteps = numberOfSteps_in;
		restartProbability = restartProbability_in;		
		
		Node currentNode = startNode;
		HashMap<Node, Double> tmpSignature = new HashMap<Node, Double>();
		HashMap<Node, Double> signature = new HashMap<Node, Double>();
		
		for(int i = 0; i < numberOfSteps; i++){
			if(Math.random() > restartProbability){
				double totalWeight = 0.0;
				for (Relationship r : currentNode.getRelationships(Direction.OUTGOING, RelTypes.LINK_TO)) {
					totalWeight += ((Integer) r.getProperty("weight", 0)).doubleValue();
				}
				
				double random = Math.random();
				double accumulatedWeight = 0.0;
				
				for(Relationship r : currentNode.getRelationships(Direction.OUTGOING, RelTypes.LINK_TO)){
					accumulatedWeight += ((Integer) r.getProperty("weight", 0)).doubleValue() / totalWeight;
					if(random < accumulatedWeight){
						if(tmpSignature.containsKey(r.getEndNode())){
							tmpSignature.put(r.getEndNode(), tmpSignature.get(r.getEndNode()) + 1);
						}else{
							tmpSignature.put(r.getEndNode(), 1.0);
						}
						currentNode = r.getEndNode();
						break;
					}
				}
				
			}else{ // restart walk
				currentNode = startNode;
			}
		}
		
		for(Entry<Node, Double> e: tmpSignature.entrySet()){
			if(e.getValue() > frequencyThreshold) signature.put(e.getKey(), e.getValue());
		}
		
		return signature;
	}
}
