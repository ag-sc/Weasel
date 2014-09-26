package h2;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import stopwatch.Stopwatch;
import fileparser.WikiParser;

public class H2PageLinksBuilder {

	String dbPath, username, password;
	WikiParser parser;
	PreparedStatement preparedStatement;
	Statement stmt;
	String sql;
	ResultSet generatedKeys, result;

	public H2PageLinksBuilder(String dbPath, String pageLinksFilePath, String username, String password) throws IOException {
		this.dbPath = dbPath;
		this.username = username;
		this.password = password;
		parser = new WikiParser(pageLinksFilePath);
	}

	private int getId(String s, Connection connection) throws Exception {
		int value = -1;
		sql = "SELECT ID FROM ENTITYID WHERE ENTITY IS (?)";
		preparedStatement = connection.prepareStatement(sql);
		preparedStatement.setString(1, s);
		preparedStatement.executeQuery();
		result = preparedStatement.getResultSet();
		while (result.next()) {
			value = result.getInt("ID");
		}
		if (value == -1) {
			sql = "INSERT INTO ENTITYID(ENTITY) VALUES (?)";
			preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			preparedStatement.setString(1, s);
			preparedStatement.executeUpdate();
			generatedKeys = preparedStatement.getGeneratedKeys();
			if (generatedKeys.next()) {
				value = generatedKeys.getInt(1);
			} else {
				throw new Exception("No return id.");
			}
		}

		return value;
	}

	public void run() throws Exception {
		long timeStart = System.nanoTime();
		int lineCounter = 0;
		String tuple[];

		Stopwatch sw = new Stopwatch(Stopwatch.UNIT.MINUTES);

		Class.forName("org.h2.Driver");
		Connection connection = DriverManager.getConnection("jdbc:h2:" + dbPath + ";LOG=0;CACHE_SIZE=65536;LOCK_MODE=0;UNDO_LOG=0", username, password);
		while ((tuple = parser.parseTuple()) != null) {
			if (tuple.length == 2) {
				lineCounter++;

				int source = getId(tuple[0], connection);
				int sink = getId(tuple[1], connection);

				// add connection
				sql = "INSERT INTO ENTITYTOENTITY(ENTITYSOURCEID, ENTITYSINKID) VALUES(?, ?)";
				preparedStatement = connection.prepareStatement(sql);
				preparedStatement.setInt(1, source);
				preparedStatement.setInt(2, sink);
				preparedStatement.execute();

				if (lineCounter % 1000000 == 0) {
					sw.stop();
					System.out.println("Processed lines:\t" + lineCounter + "\tTime since last message: " + sw);
					sw.start();
				}
			}
		}

		connection.close();

		long timeEnd = System.nanoTime();
		double passedTime = (timeEnd - timeStart) / 60000000000.0;
		System.out.println("Passed time: " + passedTime + " mins");
	}

}
