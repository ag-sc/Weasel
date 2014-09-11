package algorithm;

import java.util.HashMap;

import neo4j.Neo4jCore.RelTypes;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;



public class RandomWalk {
	private static int frequencyThreshold;
	private static int numberOfSteps;
	private static double restartProbability;
	
	private static void explore(Node n, double currentProbability, HashMap<Node, Double> signature, double continueProb) {
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
		explore(source, 1, tmpSignature, 1);
		return tmpSignature;
	}
}
