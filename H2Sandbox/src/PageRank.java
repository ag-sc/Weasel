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

import stopwatch.Stopwatch;
import datatypes.H2List;
import datatypes.PageRankNode;

public class PageRank {

	// static final String dbPathH2 =
	// "E:/Master Project/data/H2/AnchorsPlusPagelinks/h2_anchors_pagelinks";
	// static final String arrayFileName =
	// "../../data/pageRank/entityToEntity.bin";
	// static final String pageRankFileName =
	// "../../data/pageRank/pageRankArray.bin";

	static final String dbPathH2 = "~/data/h2_anchors_pagelinks";
	static final String arrayFileName = "entityToEntity.bin";
	static final String pageRankFileName = "pageRankArray.bin";

	static Connection connection;
	static double initialPageRank;
	static final int stepSize = 10000;

	public PageRank() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) throws ClassNotFoundException, SQLException, FileNotFoundException, IOException {
		System.out.println("Starting PageRank calculation.");

		PageRankNode[] pageRankArray;

		File f = new File(arrayFileName);
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

			System.out.println("Write array to binary file");
			try {
				ObjectOutputStream out;
				out = new ObjectOutputStream(new FileOutputStream(arrayFileName));
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
			ObjectInputStream objectReader = new ObjectInputStream(new FileInputStream(arrayFileName));
			pageRankArray = (PageRankNode[]) objectReader.readObject();
			objectReader.close();
			System.out.println("Array size: " + pageRankArray.length);
		}

		// Iterate PageRank
		System.out.println("Start on PageRank iteration");
		int currentReadBuffer = 0;
		double maxPageRankChange = 0;
		double maxPageRank = Double.MIN_VALUE;
		double minPageRank = Double.MAX_VALUE;

		int sanityCounter = 0;
		Stopwatch sw = new Stopwatch(Stopwatch.UNIT.SECONDS);
		do {
			maxPageRankChange = 0;
			maxPageRank = Double.MIN_VALUE;
			minPageRank = Double.MAX_VALUE;

			int currentWriteBuffer = (currentReadBuffer + 1) % 2;

			for (PageRankNode node : pageRankArray) {
				if (node == null)
					continue;
				node.pagerankBuffer[currentWriteBuffer] = 0;
			}

			for (PageRankNode node : pageRankArray) {
				if (node == null)
					continue;
				int numberOfNodes = 0;
				for (int id : node.outgoing) {
					if (pageRankArray[id] != null)
						numberOfNodes++;
				}

				if (numberOfNodes > 0) {
					double outputRank = node.pagerankBuffer[currentReadBuffer] / numberOfNodes;
					for (int id : node.outgoing) {
						if (pageRankArray[id] != null)
							pageRankArray[id].pagerankBuffer[currentWriteBuffer] += outputRank;
					}
				} else {
					node.pagerankBuffer[currentWriteBuffer] = node.pagerankBuffer[currentReadBuffer];
				}

			}

			for (PageRankNode node : pageRankArray) {
				if (node == null)
					continue;

				if (node.pagerankBuffer[currentWriteBuffer] != 0 && node.pagerankBuffer[currentWriteBuffer] < minPageRank)
					minPageRank = node.pagerankBuffer[currentWriteBuffer];
				if (node.pagerankBuffer[currentWriteBuffer] > maxPageRank)
					maxPageRank = node.pagerankBuffer[currentWriteBuffer];

				double difference = Math.abs(node.pagerankBuffer[currentWriteBuffer] - node.pagerankBuffer[currentReadBuffer]);
				if (difference > maxPageRankChange) {
					maxPageRankChange = difference;
				}
			}

			currentReadBuffer = (currentReadBuffer + 1) % 2;
			sanityCounter++;
			System.out.println("Done with iteration " + sanityCounter + "\ttime:" + sw.stop() + " s");
			System.out.println("\tMaximum pagerank change: " + maxPageRankChange + "\tMaximum pagerank: " + maxPageRank + "\tMinimum pagerank(excludes 0): " + minPageRank);
			sw.start();
		} while (maxPageRankChange > 1E-10 && sanityCounter < 100);

		System.out.println("Normalize...");
		for (PageRankNode node : pageRankArray) {
			node.pagerankBuffer[0] = node.pagerankBuffer[currentReadBuffer] / maxPageRank;
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
