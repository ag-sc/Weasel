package main.java.databaseBuilder.h2;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.TreeSet;

import main.java.utility.Stopwatch;
import main.java.datatypes.H2List;

public class H2WeightBuilder extends H2Core {

	Connection connection;
	int relationCounter = 0;

	public H2WeightBuilder(String dbPath, String username, String password) {
		super(dbPath, username, password);
	}

	private String getRelationships(int id) throws SQLException {
		String idList = null;
		sql = "SELECT entitySinkIdList FROM EntityToEntity WHERE entitySourceId IS (?)";
		preparedStatement = connection.prepareStatement(sql);
		preparedStatement.setInt(1, id);
		preparedStatement.executeQuery();
		result = preparedStatement.getResultSet();
		while (result.next()) {
			idList = result.getString(1);
			break;
		}
		return idList;
	}

	public void triangleWeight(int id) throws SQLException {
		LinkedList<String> sourceList = H2List.stringToList(getRelationships(id));
		if (sourceList == null)
			return;
		LinkedList<String> weightList = new LinkedList<String>();

		for (String sink : sourceList) {
			Double weight = 1.0;
			LinkedList<String> sinkList =H2List.stringToList( getRelationships(Integer.parseInt(sink)));
			if (sinkList != null) {
				for (String sinkSink : sinkList) {
					TreeSet<String> sinkSinkSet = H2List.stringToSet(getRelationships(Integer.parseInt(sinkSink)));
					if(sinkSinkSet.contains(Integer.toString(id))) weight += 1.0;
				}
			}
			weightList.add(weight.toString());
			relationCounter++;
		}

		sql = "UPDATE EntityToEntity SET WeightList = (?) WHERE entitySourceID = (?)";
		preparedStatement = connection.prepareStatement(sql);
		preparedStatement.setString(1, H2List.listToString(weightList));
		preparedStatement.setInt(2, id);
		preparedStatement.execute();
	}

	public void run() throws ClassNotFoundException, SQLException {
		relationCounter = 0;
		Class.forName("org.h2.Driver");
		connection = DriverManager.getConnection("jdbc:h2:" + dbPath + ";LOG=0;CACHE_SIZE=65536;LOCK_MODE=0;UNDO_LOG=0", username, password);

		int maxID = -1;
		sql = "SELECT MAX(id) FROM EntityId";
		preparedStatement = connection.prepareStatement(sql);
		preparedStatement.executeQuery();
		result = preparedStatement.getResultSet();
		while (result.next()) {
			maxID = result.getInt(1);
		}

		System.out.println("Max id is: " + maxID);
		Stopwatch sw = new Stopwatch(Stopwatch.UNIT.MILLISECONDS);
		if (maxID > 0) {
			for (int id = 1; id < maxID; id++) {
				triangleWeight(id);

				if (id % 1000 == 0) {
					System.out.println("Processed: " + id + "\t - time since last: " + sw.stop());
					sw.start();
				}
			}
		}

		connection.close();
	}

}
