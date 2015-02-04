package databaseConnectors;

import java.util.LinkedList;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;

public class Neo4jConnector extends DatabaseConnector{

	protected GraphDatabaseService graphDB;
	protected Label label;
	protected RelationshipType relType = null;
	
	public Neo4jConnector(GraphDatabaseService graphDB, Label label, RelationshipType relType){
		this.graphDB = graphDB;
		this.label = label;
		this.relType = relType;
	}
	
	@Override
	public boolean fragmentExists(String fragment){
		Node entity = null;
		try (Transaction tx = graphDB.beginTx()) {
			for (Node node : graphDB.findNodesByLabelAndProperty(label, "name", fragment)) {
				entity = node;
			}
			tx.success();
		}
		if (entity != null) return true;
		return false;
	}
	
	@Override
	public LinkedList<String> getFragmentTargets(String fragment) {
		LinkedList<String> list = new LinkedList<String>();

		try (Transaction tx = graphDB.beginTx()) {
			Node entity = null;
			for (Node node : graphDB.findNodesByLabelAndProperty(label, "name", fragment)) {
				entity = node;
			}
			if (entity != null)
				if (relType != null) {
					for (Relationship r : entity.getRelationships(Direction.OUTGOING, relType)) {
						list.add((String) r.getEndNode().getProperty("name"));
					}
				} else {
					for (Relationship r : entity.getRelationships(Direction.OUTGOING)) {
						list.add((String) r.getEndNode().getProperty("name"));
					}
				}

			tx.success();
		}

		return list;
	}

	@Override
	public void close() {
		graphDB.shutdown();		
	}


}
