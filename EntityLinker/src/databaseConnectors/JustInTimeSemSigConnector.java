package databaseConnectors;

import java.util.HashMap;
import java.util.LinkedList;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;

import algorithm.RandomWalk;

public class JustInTimeSemSigConnector extends Neo4jConnector {

	double restartProbability;
	int numberOfSteps, frequencyThreshold;

	public JustInTimeSemSigConnector(GraphDatabaseService graphDB, Label label, RelationshipType relType, double restartProbability, int numberOfSteps,
			int frequencyThreshold) {
		super(graphDB, label, relType);
		this.restartProbability = restartProbability;
		this.numberOfSteps = numberOfSteps;
		this.frequencyThreshold = frequencyThreshold;
	}

	@Override
	public LinkedList<String> getFragmentTargets(String fragment) {
		LinkedList<String> list = new LinkedList<String>();

		try (Transaction tx = graphDB.beginTx()) {
			Node entity = null;
			for (Node node : graphDB.findNodesByLabelAndProperty(label, "name", fragment)) {
				entity = node;
			}
			if (entity != null) {
				//HashMap<Node, Double> semSig = RandomWalk.randomWalk(entity, numberOfSteps, frequencyThreshold, restartProbability);
				HashMap<Node, Double> semSig = RandomWalk.calcProbSignature(entity, numberOfSteps, frequencyThreshold, restartProbability);
				for (Node n : semSig.keySet()) {
					list.add((String) n.getProperty("name"));
				}
			}

			tx.success();
		}

		return list;
	}
}
