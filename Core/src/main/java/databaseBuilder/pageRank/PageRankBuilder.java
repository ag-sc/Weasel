package main.java.databaseBuilder.pageRank;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;

import main.java.utility.Stopwatch;
import main.java.datatypes.H2List;
import main.java.datatypes.PageRankNode;
import edu.uci.ics.jung.algorithms.scoring.PageRank;
import edu.uci.ics.jung.graph.DirectedSparseGraph;

public class PageRankBuilder {

	static Connection connection;
	static double initialPageRank;
	static final int stepSize = 10000;

	public static void run(String dbPathH2, String entityToEntityArrayPath, String pageRankArrayPath) throws ClassNotFoundException, SQLException, FileNotFoundException, IOException {
		System.out.println("Starting PageRank calculation.");

		PageRankNode[] pageRankArray;

		File f = new File(entityToEntityArrayPath);
		if (!f.exists() || f.isDirectory()) {
			System.out.println("Array file does not exist yet. Build it.");

			Class.forName("org.h2.Driver");
			connection = DriverManager.getConnection("jdbc:h2:" + dbPathH2, "sa", "");

			String sql = "SELECT max(id)  FROM ENTITYID ";
			int maxID = 0;
			try {
				PreparedStatement preparedStatement = connection.prepareStatement(sql);
				preparedStatement.executeQuery();
				ResultSet result = preparedStatement.getResultSet();
				while (result.next()) {
					maxID = result.getInt("max(id)");
				}
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			System.out.println("Max ID: " + maxID);

			Stopwatch sw = new Stopwatch(Stopwatch.UNIT.MINUTES);
			System.out.println("Start building the page rank array.");
			pageRankArray = new PageRankNode[maxID + 1];
			initialPageRank = 1.0 / maxID;
			for (int id = 0; id <= maxID; id += stepSize) {
				setLinkTargets(pageRankArray, id, Math.min(id + stepSize, maxID));

				if (id % 100000 == 0) {
					sw.stop();
					System.out.println(id + ":\t" + (((double) id / maxID) * 100) + "%" + "\ttime since last update: " + sw + " minutes\testimated remaining: "
							+ ((maxID - id) / 100000) * sw.doubleTime + " minutes");
					sw.start();
				}
			}

			// find all nodes with no incoming links and set their pageRank to 0
			boolean[] incomingLinks = new boolean[pageRankArray.length];
			for (int i = 0; i < pageRankArray.length; i++) {
				PageRankNode node = pageRankArray[i];
				if (node == null)
					pageRankArray[i] = new PageRankNode(0, new LinkedList<String>());
				else {
					for (int link : node.outgoing)
						incomingLinks[link] = true;
				}
			}

			for (int i = 0; i < incomingLinks.length; i++) {
				if (incomingLinks[i] == false) {
					pageRankArray[i].pagerankBuffer[0] = 0.0;
					pageRankArray[i].pagerankBuffer[1] = 0.0;
				}
			}

			System.out.println("Write array to binary file");
			try {
				ObjectOutputStream out;
				out = new ObjectOutputStream(new FileOutputStream(entityToEntityArrayPath));
				out.writeObject(pageRankArray);
				out.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else { // file exists
			System.out.println("File exists, load it");
			ObjectInputStream objectReader = new ObjectInputStream(new FileInputStream(entityToEntityArrayPath));
			pageRankArray = (PageRankNode[]) objectReader.readObject();
			objectReader.close();
			System.out.println("Array size: " + pageRankArray.length);
		}

		System.out.println("Fill graph.");
		DirectedSparseGraph<Integer, Integer> graph = new DirectedSparseGraph<Integer, Integer>();
		for(Integer i = 0; i< pageRankArray.length; i++){
			PageRankNode prn = pageRankArray[i];
			if(prn != null){
				graph.addVertex(i);
			}
		}
		Integer edgeCounter = 0;
		for(Integer i = 0; i< pageRankArray.length; i++){
			PageRankNode prn = pageRankArray[i];
			if(prn != null){
				for(Integer sink: prn.outgoing){
					graph.addEdge(edgeCounter++, i, sink);
				}
			}
		}
		System.out.println("Run pagerank.");
		Stopwatch swTemp = new Stopwatch(Stopwatch.UNIT.MINUTES);
		PageRank<Integer, Integer> ranker = new PageRank<Integer, Integer>(graph, 0.1);
		ranker.setMaxIterations(25);
		ranker.evaluate();
		System.out.println("passed time: " + swTemp.stop() + " minutes");
		
		double[] output = new double[pageRankArray.length];
		
		for (Integer i : graph.getVertices()) {
			output[i] = ranker.getVertexScore(i);
		}
		
		System.out.println("Write pageRank array to binary file");
		try {
			ObjectOutputStream out;
			out = new ObjectOutputStream(new FileOutputStream(pageRankArrayPath));
			out.writeObject(output);
			out.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("All done!");
	}

	private static void setLinkTargets(PageRankNode[] pageRankArray, int start, int finish) throws SQLException {
		final String linkTargetSQL = "select * from EntityToEntity where EntitySourceID > (?) and EntitySourceID <= (?)";
		try {
			PreparedStatement preparedStatement = connection.prepareStatement(linkTargetSQL);
			preparedStatement.setInt(1, start);
			preparedStatement.setInt(2, finish);
			preparedStatement.executeQuery();
			ResultSet result = preparedStatement.getResultSet();
			while (result.next()) {
				int id = result.getInt("ENTITYSOURCEID");
				String tmp = result.getString("ENTITYSINKIDLIST");
				LinkedList<String> resultList = H2List.stringToList(tmp);
				pageRankArray[id] = new PageRankNode(initialPageRank, resultList);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			connection.close();
			System.exit(-1);
		}
	}

}
