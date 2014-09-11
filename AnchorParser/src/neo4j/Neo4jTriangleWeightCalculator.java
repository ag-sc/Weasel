package neo4j;

import java.util.TreeSet;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.tooling.GlobalGraphOperations;

public class Neo4jTriangleWeightCalculator extends Neo4jPrototype {

	public Neo4jTriangleWeightCalculator(String dbPath) {
		super(dbPath);
		// TODO Auto-generated constructor stub
	}

	@Override
	void _run(String[] args) {
		long timeStart = System.nanoTime();
		int relCounter = 0;
		for(Relationship r: GlobalGraphOperations.at(graphDB).getAllRelationships()){
			int weight = 1;
			TreeSet<Long> sourceSet = new TreeSet<Long>();
			for(Relationship rSource: r.getStartNode().getRelationships(Direction.OUTGOING)){
				if(!rSource.getEndNode().equals(r.getEndNode())) sourceSet.add(rSource.getEndNode().getId());
			}
			for(Relationship rSink: r.getEndNode().getRelationships(Direction.OUTGOING)){
				if(sourceSet.contains(rSink.getEndNode().getId())) weight++;
			}
			
			setWeightFor(r, weight);
			relCounter++;
			if(relCounter % 100000 == 0) System.out.println(relCounter);
		}
		long timeEnd = System.nanoTime();
		double passedTime = (timeEnd - timeStart) / 60000000000.0;
		System.out.println("Passed time: " + passedTime + " mins");
		System.out.println("nr of relations: " + relCounter);
	}
	
	private void setWeightFor(Relationship r, int weight){
		r.setProperty("weight", weight);
		changeCounter++;
		checkTransactions();
	}
	
	public static void main(String[] args) {
		Neo4jTriangleWeightCalculator calc = new Neo4jTriangleWeightCalculator("PageLinks");
		String[] tmp = new String[1];
		calc.run(tmp);
	}

}
