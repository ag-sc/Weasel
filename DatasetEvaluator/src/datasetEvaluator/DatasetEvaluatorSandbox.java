package datasetEvaluator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.TreeSet;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

import stopwatch.Stopwatch;
import neo4j.Neo4jCore;
import databaseConnectors.H2Connector;
import databaseConnectors.JDBMConnector;
import databaseConnectors.JustInTimeSemSigConnector;
import databaseConnectors.Neo4jConnector;
import datasetParser.KORE50Parser;
import entityLinker.EntityLinker;
import evaluation.BabelfyEvaluation;
import evaluation.EvaluationEngine;
import evaluation.RandomEvaluator;

public class DatasetEvaluatorSandbox {

	public static void main(String[] args) {
		try {
			// TestSet
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("../../data/DatasetParser/test/kore50.tsv"), "UTF8"));
			KORE50Parser parser = new KORE50Parser(br);
			
			// PageLinks
			GraphDatabaseService graphDB2 = new GraphDatabaseFactory().newEmbeddedDatabase( "../../data/DBs/PageLinksWithWeights" );
			Neo4jCore.registerShutdownHook( graphDB2 );
			
			// Evaluation Engine
			Neo4jConnector semSigConnector = new Neo4jConnector(graphDB2, Neo4jCore.wikiLinkLabel, null);
			EvaluationEngine evaluator = new BabelfyEvaluation(semSigConnector, 0.3, 10);
			
			// Connectors
//			GraphDatabaseService graphDB = new GraphDatabaseFactory().newEmbeddedDatabase( "../../data/DBs/Anchors_old" );
//			Neo4jCore.registerShutdownHook( graphDB );
//			Neo4jConnector anchors = new Neo4jConnector(graphDB, Neo4jCore.anchorLabel, null);
//			Neo4jConnector partialAnchors = new Neo4jConnector(graphDB, Neo4jCore.partialAnchorLabel, null);
//			Neo4jConnector checkupConnector = new Neo4jConnector(graphDB, Neo4jCore.entityLabel, null);
			String dbPathH2 = "E:/Master Project/data/H2/Anchors/h2Anchors";
			String partialAnchorSQL = "select ANCHOR  from ANCHORID where ID in (select PARTIALANCHORTOANCHOR.ANCHORID from PARTIALANCHORTOANCHOR, PARTIALANCHORID where PARTIALANCHORID.ID = PARTIALANCHORTOANCHOR.PARTIALANCHORID and PARTIALANCHOR is (?))";
			H2Connector partialAnchors = new H2Connector(dbPathH2, "sa", "", partialAnchorSQL);
			String anchorSQL = "select ENTITY from ENTITYID where ID in (select ANCHORTOENTITY.ENTITYID from ANCHORID , ANCHORTOENTITY where ANCHORID.ID = ANCHORTOENTITY.ANCHORID and ANCHOR is (?))";
			H2Connector anchors = new H2Connector(dbPathH2, "sa", "", anchorSQL);
			
			// Linker & Evaluation
			System.out.println("About to start evaluation.");
			EntityLinker linker = new EntityLinker(evaluator, anchors, partialAnchors, "../../data/stopwords.txt");
			DatasetEvaluator dataEvaluator = new DatasetEvaluator(parser, linker, anchors); // checkupConnector);
			Stopwatch sw = new Stopwatch(Stopwatch.UNIT.MINUTES);
			dataEvaluator.evaluate();
			System.out.println("Evaluation time: " + sw.stop() + " minutes");
				
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
