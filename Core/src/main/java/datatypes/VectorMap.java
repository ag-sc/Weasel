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

import main.java.datatypes.configuration.Config;
import main.java.utility.Stopwatch;

public class VectorMap {

	boolean inMemory = false;
	HashMap<Integer, VectorEntry> vectorMap;
	SoftReference<HashMap<Integer, VectorEntry>> softVectorMap;
	HashMap<Integer, SoftReference<VectorEntry>> softMap2;
	Connection connection;
	Statement stmt;
	String sql;
	int cacheCounter = 0;
	int accessCounter = 0;

	public VectorMap() {
		Config config = Config.getInstance();

		String connectorType = config.getParameter("dbConnector");
		switch (connectorType) {
		case "H2":
			inMemory = false;
			try {
				Class.forName("org.h2.Driver");
				connection = DriverManager.getConnection("jdbc:h2:" + config.getParameter("H2Path"), "sa", "");
				sql = "SELECT semSigVector, semSigCount, tfVector, tfCount FROM EntityID WHERE id IS ";
				stmt = connection.createStatement();
				
				softVectorMap = new SoftReference<>(new HashMap<Integer, VectorEntry>());
				softMap2 = new HashMap<Integer, SoftReference<VectorEntry>>();
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
			FileInputStream fileInputStream;
			try {
				System.out.println("Vector map not loaded yet. Loading now...");
				fileInputStream = new FileInputStream(config.getParameter("vectorMapPath"));
				ObjectInputStream objectReader = new ObjectInputStream(fileInputStream);
				vectorMap = (HashMap<Integer, VectorEntry>) objectReader.readObject();
				objectReader.close();
				fileInputStream.close();
			} catch (IOException | ClassNotFoundException e) {
				// TODO Auto-generated catch block
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

	private VectorEntry getFromDB(int index){
		try {
			accessCounter++;
			if (accessCounter % 500 == 0)
				System.out.println("number of getVectorEntry calls: " + accessCounter);

			VectorEntry vEntry = new VectorEntry();
			ResultSet result = stmt.executeQuery(sql + index);
			String semSigVector, semSigCount, tfVector, tfScore;
			while (result.next()) {
				semSigVector = result.getString(1);
				semSigCount = result.getString(2);
				tfVector = result.getString(3);
				tfScore = result.getString(4);

				if (semSigVector == null || semSigCount == null || tfVector == null || tfScore == null) {
					// System.err.println("No vector entry found for entity: "
					// + index);
					break;
				}
				// System.out.println(index);

				semSigVector = semSigVector.trim();
				semSigCount = semSigCount.trim();
				tfVector = tfVector.trim();
				tfScore = tfScore.trim();

				String[] vectorArray, countArray;
				vectorArray = semSigVector.split(" ");
				countArray = semSigCount.split(" ");
				try {
					for (int i = 0; i < vectorArray.length; i++) {
						if (vectorArray[i].isEmpty())
							continue;
						vEntry.semSigVector[i] = Integer.parseInt(vectorArray[i]);
						vEntry.semSigCount[i] = Integer.parseInt(countArray[i]);
					}

					vectorArray = tfVector.split(" ");
					countArray = tfScore.split(" ");
					for (int i = 0; i < vectorArray.length; i++) {
						if (vectorArray[i].isEmpty())
							continue;
						vEntry.tfVector[i] = Integer.parseInt(vectorArray[i]);
						vEntry.tfScore[i] = Float.parseFloat(countArray[i]);
					}
				} catch (NumberFormatException e) {
					System.err.println("Number format exception for entry: " + index);
					System.err.println("semSigVector: " + semSigVector);
					System.err.println("semSigCount: " + semSigCount);
					System.err.println("tfVector: " + tfVector);
					System.err.println("tfScore: " + tfScore);
					e.printStackTrace();
					return vEntry;
				}
				break;
			}

			return vEntry;
			
		} catch (SQLException e) {
			System.err.println("Error getting vectorEntry for index: " + index);
			e.printStackTrace();
			VectorEntry vEntry = new VectorEntry();
			return vEntry;
		}
	}
	
	public VectorEntry getVectorEntry(int index) {
		if (inMemory) {
			return vectorMap.get(index);
		} else {

			if(softMap2.containsKey(index)){
				VectorEntry ve = softMap2.get(index).get();
				if(ve != null){
					return ve;
				}
			}
			VectorEntry ve = getFromDB(index);
			softMap2.put(index, new SoftReference<VectorEntry>(ve));
			return ve;
			
			//HashMap<Integer, VectorEntry> cache = softVectorMap.get();
//			if (cache == null) {
//				cache = new HashMap<Integer, VectorEntry>();
//				softVectorMap = new SoftReference<HashMap<Integer, VectorEntry>>(cache);
//			}
//
//			if (cache.containsKey(index)) {
//				cacheCounter++;
//				if (cacheCounter % 500 == 0)
//					System.out.println("number of cached calls: " + cacheCounter);
//				return cache.get(index);
//			} else {
//				return getFromDB(index, cache);
//			}
		}
	}
}
