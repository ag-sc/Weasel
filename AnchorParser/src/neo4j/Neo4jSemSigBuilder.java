package neo4j;

import graphAccess.GraphAccess;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.TreeSet;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.schema.IndexDefinition;
import org.neo4j.graphdb.schema.Schema;
import org.neo4j.tooling.GlobalGraphOperations;

import datatypes.TinyEdge;

public class Neo4jSemSigBuilder extends Neo4jPrototype {
	public Neo4jSemSigBuilder(String dbPath) {
		super(dbPath);
		// TODO Auto-generated constructor stub
	}

	long start, end;

	private final double restartProbability = 0.85; // 0.85
	private final int frequencyThreshold = 10; // 100
	private final int numberOfSteps = 100000; // 1000000

	private void explore(Node n, double currentProbability, HashMap<Node, Double> signature, double continueProb) {
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

	private void randomWalkProbability(Node source) {
		HashMap<Node, Double> tmpSignature = new HashMap<Node, Double>();

		explore(source, 1, tmpSignature, 1);

		for (Entry<Node, Double> e : tmpSignature.entrySet()) {
			addRelation(source, e.getKey(), e.getValue());
		}
	}

	private void randomWalk(Node source) {
		Node currentNode = source;
		HashMap<Node, Integer> tmpSignature = new HashMap<Node, Integer>();

		for (int i = 0; i < numberOfSteps; i++) {
			if (Math.random() > restartProbability) {

				if (currentNode.getDegree(Direction.OUTGOING) == 0) {
					currentNode = source;
					continue;
				}

				double totalWeight = 0.0;
				for (Relationship r : currentNode.getRelationships(Direction.OUTGOING, RelTypes.LINK_TO)) {
					totalWeight += ((Integer) r.getProperty("weight", 0)).doubleValue();
				}

				double random = Math.random();
				double accumulatedWeight = 0.0;

				for (Relationship r : currentNode.getRelationships(Direction.OUTGOING, RelTypes.LINK_TO)) {
					accumulatedWeight += ((Integer) r.getProperty("weight", 0)).doubleValue() / totalWeight;
					if (random < accumulatedWeight) {
						if (tmpSignature.containsKey(r.getEndNode())) {
							tmpSignature.put(r.getEndNode(), tmpSignature.get(r.getEndNode()) + 1);
						} else {
							tmpSignature.put(r.getEndNode(), 1);
						}
						currentNode = r.getEndNode();
						break;
					}
				}

			} else { // restart walk
				currentNode = source;
			}
		}

		for (Entry<Node, Integer> e : tmpSignature.entrySet()) {
			if (e.getValue() > frequencyThreshold) {
				addRelation(source, e.getKey(), e.getValue());
			}
		}
	}

	private void addRelation(Node sourceNode, Node sinkNode, double count) {
		if (sourceNode.equals(sinkNode))
			return;

		Relationship r = sourceNode.createRelationshipTo(sinkNode, RelTypes.SEMANTIC_SIGNATURE_OF);
		r.setProperty("probability", count);

		changeCounter++;
		checkTransactions();
	}

	@Override
	void _run(String[] args) {
		start = System.nanoTime();

		GlobalGraphOperations global = GlobalGraphOperations.at(graphDB);
		int counter = 0;
		long intervalStart = System.nanoTime(), intervalEnd;
		for (Node n : global.getAllNodes()) {
			randomWalk(n);
			counter++;
			//randomWalkProbability(n);
			if(counter % 1000000 == 0){
				intervalEnd = System.nanoTime();
				double passedTime = (intervalEnd - intervalStart) / 60000000000.0;
				System.out.println("processed nodes: " + counter + "\ttime since last: " + passedTime + "\tmins");
				intervalStart = System.nanoTime();
			}
		}
		System.out.println("Nr of nodes: " + counter);

		// int tmp = 0;
		// for (Node n : global.getAllNodes()) {
		// System.out.println("node: " + n.getProperty("name", "FAILURE"));
		// for(Relationship r: n.getRelationships(Direction.OUTGOING,
		// RelTypes.CONNECTION)){
		// System.out.println("Connec	" +
		// (String)r.getEndNode().getProperty("name"));
		// }
		// for(Relationship r: n.getRelationships(Direction.OUTGOING,
		// RelTypes.SEMANTIC_SIGNATURE)){
		// System.out.println("SemSig	" +
		// (String)r.getEndNode().getProperty("name") + "(" +
		// r.getProperty("probability") + ")");
		// }
		// if(tmp++ > 20) break;
		// }

	}

	public static void main(String[] args) {

		Neo4jSemSigBuilder builder = new Neo4jSemSigBuilder("../../data/DBs/BatchPageLinksTest");
		builder.run(new String[1]);
	}
}
