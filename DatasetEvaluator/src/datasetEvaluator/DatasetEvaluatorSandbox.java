package datasetEvaluator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.TreeSet;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

import neo4j.Neo4jCore;
import databaseConnectors.JDBMConnector;
import databaseConnectors.Neo4jConnector;
import datasetParser.KORE50Parser;
import entityLinker.EntityLinker;
import evaluation.BabelfyEvaluation;
import evaluation.EvaluationEngine;
import evaluation.RandomEvaluator;

public class DatasetEvaluatorSandbox {

	public static void main(String[] args) {
		try {					
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("../../data/DatasetParser/test/kore50.tsv"), "UTF8"));
			KORE50Parser parser = new KORE50Parser(br);
			GraphDatabaseService graphDB2 = new GraphDatabaseFactory().newEmbeddedDatabase( "../../data/DBs/PageLinksWithWeights" );
			Neo4jCore.registerShutdownHook( graphDB2 );
			Neo4jConnector connector = new Neo4jConnector(graphDB2, Neo4jCore.wikiLinkLabel, null);
			EvaluationEngine evaluator = new BabelfyEvaluation(connector, 0.3, 10);
			//EvaluationEngine evaluator = new RandomEvaluator();
//			JDBMConnector linkerConnector = new JDBMConnector("../../data/Wikipedia Anchor/db/anchorKeyMap", "anchorKeyMap");
//			JDBMConnector checkupConnector = new JDBMConnector("../../data/Wikipedia Anchor/db/uriKeyMap", "uriKeyMap");
			GraphDatabaseService graphDB = new GraphDatabaseFactory().newEmbeddedDatabase( "../../data/DBs/Anchors" );
			Neo4jCore.registerShutdownHook( graphDB );
			Neo4jConnector anchors = new Neo4jConnector(graphDB, Neo4jCore.anchorLabel, null);
			Neo4jConnector partialAnchors = new Neo4jConnector(graphDB, Neo4jCore.partialAnchorLabel, null);
			Neo4jConnector checkupConnector = new Neo4jConnector(graphDB, Neo4jCore.entityLabel, null);
			EntityLinker linker = new EntityLinker(evaluator, anchors, partialAnchors, "../../data/stopwords.txt");
			
			DatasetEvaluator dataEvaluator = new DatasetEvaluator(parser, linker, checkupConnector);
			dataEvaluator.evaluate();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
