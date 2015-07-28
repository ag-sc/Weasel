package main.java.datatypes;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.lang.ref.SoftReference;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import main.java.datatypes.configuration.Config;
import main.java.utility.Stopwatch;

public class PageRankContainer {

	boolean inMemory = false;
	double[] pageRankArray;
	SoftReference<Map<Integer, Double>> softPageRankArray;
	Connection connection;
	Statement stmt;
	String sql;
	
	public PageRankContainer() {
		Config config = Config.getInstance();

		String connectorType = config.getParameter("dbConnector");
		switch (connectorType) {
		case "H2":
			inMemory = false;
			try {
				Class.forName("org.h2.Driver");
				connection = DriverManager.getConnection("jdbc:h2:" + config.getParameter("H2Path"), "sa", "");
				sql = "SELECT pageRank FROM EntityID WHERE id IS ";
				stmt = connection.createStatement();
				
				softPageRankArray = new SoftReference<Map<Integer, Double>>(new HashMap<Integer, Double>());
			} catch (ClassNotFoundException | SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				System.exit(-1);
			}
			break;
		case "inMemory":
			inMemory = true;
			Stopwatch sw = new Stopwatch(Stopwatch.UNIT.MINUTES);
			sw.start();
			System.out.println("PageRank not loaded yet. Loading now...");
			String pageRankArrayPath = Config.getInstance().getParameter("pageRankArrayPath");
			FileInputStream fileInputStream;
			try {
				fileInputStream = new FileInputStream(pageRankArrayPath);
				ObjectInputStream objectReader = new ObjectInputStream(fileInputStream);
				pageRankArray = (double[]) objectReader.readObject();
				objectReader.close();
				fileInputStream.close();
			} catch (IOException | ClassNotFoundException e) {
				e.printStackTrace();
				System.exit(-1);
			}
			System.out.println("Done. Took " + sw.stop() + " minutes.");
			break;
		default:
			System.err.println("Unrecognized connectorType: " + connectorType + " - aborting!");
			System.exit(-1);
		}
	}

	public double getPageRank(int index) {
		if(inMemory){
			return pageRankArray[index];
		}else{
			
			Map<Integer, Double> cache = softPageRankArray.get();
			if(cache == null){
				cache = new HashMap<Integer, Double>();
				softPageRankArray = new SoftReference<Map<Integer, Double>>(cache);
			}
			
			if(cache.containsKey(index)){
				return cache.get(index);
			}else{
				try {
					ResultSet result = stmt.executeQuery(sql + index);
					while (result.next()) {
						Double d = result.getDouble(1);
						cache.put(index, d);
						return d;
					}
				} catch (SQLException e) {
					System.err.println("Error getting pagerank for index: " + index);
					e.printStackTrace();
					cache.put(index, 0.0);
					return 0;
				}
			}		
		}
		return 0;
	}
}
