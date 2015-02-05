import h2.H2AnchorBuilder;
import h2.H2DBCreator;
import h2.H2PageLinksBuilder;
import inmemory.MemoryDataContainerBuilderFromH2;
import inmemory.SemSigComputation;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import pageRank.PageRank;
import stopwatch.Stopwatch;
import vectorMap.VectorMapGenerator;
import wikipediaAbstracts.WikiDumpProcessor;
import configuration.Config;
import documentFrequency.DocumentFrequencyObjectBuilder;


public class FullDataBuilder {
	
	private static Config config;
	private static boolean forceOverride = false;

	public static void main(String[] args) {
		// load ini file
		String filepath = "../config.ini";
		if(args.length == 1) filepath = args[0];
		System.out.println("Using config file: " + filepath);
		IniLoader iniLoader = new IniLoader();
		iniLoader.parse(filepath);
		config = Config.getInstance();
		forceOverride = Boolean.parseBoolean(config.getParameter("forceOverride"));
		
		// build abstracts
		Stopwatch sw = new Stopwatch(Stopwatch.UNIT.HOURS);
		parseWikipediaAbstracts();
		buildDocumentFrequencyFile();
		buildDatabase();
		buildInMemoryDBObject();
		buildSemanticSignature();
		buildVectorMap();
		buildPageRankArray();
		System.out.println("All done! Total time: " + sw.stop() + " hours");
	} // main
	
	private static void parseWikipediaAbstracts(){
		String inputFile = config.getParameter("wikipediaDump");
		String outputFile = config.getParameter("cleanedAbstracts");
		
		if(!forceOverride){
			File f = new File(outputFile);
			if(f.exists() && !f.isDirectory()){
				return;
			}
		}

		System.out.println("Parse wikipedia dump '" + inputFile + "' to create abstracts file '" + outputFile + "'.");
		try {
			WikiDumpProcessor.run(outputFile, inputFile);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
	} // parseWikipediaAbstracts
	
	private static void buildDocumentFrequencyFile(){
		String inputFile = config.getParameter("cleanedAbstracts");
		String outputFile = config.getParameter("dfPath");
		if(!forceOverride){
			File f = new File(outputFile);
			if(f.exists() && !f.isDirectory()){
				return;
			}
		}
		
		System.out.println("Building document frequency file '" + outputFile + "'");
		try {
			DocumentFrequencyObjectBuilder.run(inputFile, outputFile);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
	} // buildDocumentFrequencyFile
	
	private static void buildDatabase(){
		String h2Path = config.getParameter("H2Path");
		String anchorFilePath = config.getParameter("anchorFilePath");
		String pageLinksFilePath = config.getParameter("pageLinksFilePath");
		String stopWordsPath = config.getParameter("stopwordsPath");
		
		String path = h2Path + ".mv.db";
		File f = new File(path);
		if (f.exists() && !f.isDirectory()) {
			if (!forceOverride){
				return;
			}else{
				f.delete();
			}
		}
		
		System.out.println("Build H2 Database...");
		H2DBCreator dbCreator = new H2DBCreator(h2Path);
        try {
			dbCreator.create();
		
			System.out.println("Build anchor part of DB.");
			H2AnchorBuilder builder1 = new H2AnchorBuilder(h2Path, anchorFilePath, "sa", "", stopWordsPath);
			builder1.run();
			
			System.out.println("Build pagelinks part of DB.");
        	H2PageLinksBuilder builder2 = new H2PageLinksBuilder(h2Path, pageLinksFilePath, "sa", "");
			builder2.run();
			
			Class.forName("org.h2.Driver");
			Connection connection = DriverManager.getConnection("jdbc:h2:" + h2Path, "sa", "");
			
			Statement stat = connection.createStatement();
	        stat.execute("SHUTDOWN COMPACT");
	        
//			H2WeightBuilder weightBuilder = new H2WeightBuilder(dbPath, "sa", "");
//			weightBuilder.run();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
	} // buildDatabase
	
	private static void buildInMemoryDBObject(){
		String h2Path = config.getParameter("H2Path");
		String wikiDumpPath = config.getParameter("wikipediaDump");
		String inMemoryDataContainerPath = config.getParameter("inMemoryDataContainerPath");
		
		if(!forceOverride){
			File f = new File(inMemoryDataContainerPath);
			if(f.exists() && !f.isDirectory()){
				return;
			}
		}
		
		System.out.println("Build inMemoryDBContainer");
		try {
			MemoryDataContainerBuilderFromH2.run(h2Path, inMemoryDataContainerPath, wikiDumpPath);
		} catch (ClassNotFoundException | IOException | SQLException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
	} // inMemoryDataContainerPath
	
	private static void buildSemanticSignature(){
		String pageLinksPath = config.getParameter("pageLinksFilePath");
		String semsigPath = config.getParameter("semSigPath");
		if(!forceOverride){
			File f = new File(semsigPath);
			if(f.exists() && !f.isDirectory()){
				return;
			}
		}
		
		System.out.println("Compute semantic signature");
		try {
			SemSigComputation.run(pageLinksPath, semsigPath);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
	} // buildSemanticSignature
	
	private static void buildVectorMap() {
		String dfPath = config.getParameter("dfPath");
		String abstractPath = config.getParameter("cleanedAbstracts");
		String semsigPath = config.getParameter("semSigPath");
		String inMemoryDataContainerPath = config.getParameter("inMemoryDataContainerPath");
		String vectorMapOutputPath = config.getParameter("vectorMapPath");
		
		if(!forceOverride){
			File f = new File(vectorMapOutputPath);
			if(f.exists() && !f.isDirectory()){
				return;
			}
		}
		System.out.println("Build VectorMap.");
		try {
			VectorMapGenerator.run(vectorMapOutputPath, dfPath, abstractPath, semsigPath, inMemoryDataContainerPath);
		} catch (ClassNotFoundException | IOException | SQLException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
	} // buildVectorMap
	
	private static void buildPageRankArray(){
		String h2Path = config.getParameter("H2Path");
		String entityToEntityArrayPath = config.getParameter("entityToEntityArrayPath");
		String pageRankArrayPath = config.getParameter("pageRankArrayPath");
		if(!forceOverride){
			File f = new File(pageRankArrayPath);
			if(f.exists() && !f.isDirectory()){
				return;
			}
		}
		System.out.println("Build PageRankArray.");
		
		try {
			PageRank.run(h2Path, entityToEntityArrayPath, pageRankArrayPath);
		} catch (ClassNotFoundException | SQLException | IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
	} // buildPageRankArray

}
