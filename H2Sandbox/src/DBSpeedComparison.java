import java.sql.SQLException;
import java.util.LinkedList;
import java.util.TreeSet;

import neo4j.Neo4jCore;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

import stopwatch.Stopwatch;
import databaseConnectors.H2Connector;
import databaseConnectors.Neo4jConnector;


public class DBSpeedComparison {
	
	static String lookup = "Goethe";

	public DBSpeedComparison() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		try {
			Stopwatch sw = new Stopwatch(Stopwatch.UNIT.MILLISECONDS);
			LinkedList<String> listH2, listNeo4j;
			System.out.println("Performing test for '" + lookup + "'");
			
			// H2
			sw.start();
			String dbPathH2 = "E:/Master Project/data/H2/Anchors/h2Anchors";
			String partialAnchorSQL = "select ANCHOR  from ANCHORID where ID in (select PARTIALANCHORTOANCHOR.ANCHORID from PARTIALANCHORTOANCHOR, PARTIALANCHORID where PARTIALANCHORID.ID = PARTIALANCHORTOANCHOR.PARTIALANCHORID and PARTIALANCHOR is (?))";
			H2Connector partialAnchorsH2 = new H2Connector(dbPathH2, "sa", "", partialAnchorSQL);
			sw.stop();
			System.out.println("H2 DB startup time: " + sw + " ms");
			
			// test
			sw.start();
			listH2 = partialAnchorsH2.getFragmentTargets(lookup);
			sw.stop();
			System.out.println("H2 Lookup: " + sw + " ms - list lenght: " + listH2.size());

			partialAnchorsH2.close();
			
			// Neo4j
			sw.start();
			GraphDatabaseService graphDB = new GraphDatabaseFactory().newEmbeddedDatabase("../../data/DBs/Anchors");
			Neo4jCore.registerShutdownHook(graphDB);
			Neo4jConnector partialAnchorsNeo4j = new Neo4jConnector(graphDB, Neo4jCore.partialAnchorLabel, null);
			sw.stop();
			System.out.println("Neo4j DB startup time: " + sw + " ms");

			sw.start();
			listNeo4j = partialAnchorsNeo4j.getFragmentTargets(lookup);
			sw.stop();
			System.out.println("Neo4j Lookup: " + sw + " ms - list lenght: " + listNeo4j.size());
			
			partialAnchorsNeo4j.close();
			
			// debug
			TreeSet<String> tmp1 = new TreeSet<String>();
			for(String s: listH2) tmp1.add(s);
			
			TreeSet<String> tmp2 = new TreeSet<String>();
			for(String s: listNeo4j){
				if(!tmp2.contains(s)) tmp2.add(s);
				else System.out.println(s);
			}
			
			System.out.println("l1: " + tmp1.size() + " - l2: " + tmp2.size());
			
			int counter = 0;
			for(String s: listNeo4j){
				if(!tmp1.contains(s)) System.out.println(counter + ": " + s);
				counter++;
			}

		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
