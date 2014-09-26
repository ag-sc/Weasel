package h2;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.TreeSet;

import fileparser.AnchorFileParser;
import fileparser.StopWordParser;

public class H2AnchorBuilder {

	String dbPath, username, password;
	AnchorFileParser anchorParser;
	PreparedStatement preparedStatement;
	Statement stmt;
	String sql;
	ResultSet generatedKeys, result;
	TreeSet<String> stopWords;

	public H2AnchorBuilder(String dbPath, String anchorFilePath, String username, String password, String stopWordsPath) throws IOException {
		this.dbPath = dbPath;
		this.username = username;
		this.password = password;
		anchorParser = new AnchorFileParser(anchorFilePath);
		stopWords = StopWordParser.parseStopwords(stopWordsPath);
	}

	public void run() throws Exception {
		long timeStart = System.nanoTime();
		int lineCounter = 0;
		String triplet[];

		Class.forName("org.h2.Driver");
		Connection connection = DriverManager.getConnection("jdbc:h2:" + dbPath + ";LOG=0;CACHE_SIZE=65536;LOCK_MODE=0;UNDO_LOG=0", username, password);
		while ((triplet = anchorParser.parseTriplet()) != null) {
			lineCounter++;
			

			int sink = -1;
			int source = -1;

			// source
			sql = "SELECT ID FROM ANCHORID WHERE ANCHOR IS (?)";
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setString(1, triplet[0]);
			preparedStatement.executeQuery();
			result = preparedStatement.getResultSet();
			while (result.next()) {
				source = result.getInt("ID");
			}
			if (source == -1) {
				sql = "INSERT INTO AnchorId(anchor) VALUES (?)";
				preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				preparedStatement.setString(1, triplet[0]);
				preparedStatement.executeUpdate();
				generatedKeys = preparedStatement.getGeneratedKeys();
				if (generatedKeys.next()) {
					source = generatedKeys.getInt(1);
				} else {
					throw new Exception("No return id.");
				}
			}

			// sink
			sql = "SELECT ID FROM ENTITYID WHERE ENTITY IS (?)";
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setString(1, triplet[1]);
			preparedStatement.executeQuery();
			ResultSet result = preparedStatement.getResultSet();
			while (result.next()) {
				sink = result.getInt("ID");
			}
			if (sink == -1) {
				sql = "MERGE INTO EntityId(entity) VALUES (?)";
				preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				preparedStatement.setString(1, triplet[1]);
				preparedStatement.executeUpdate();
				generatedKeys = preparedStatement.getGeneratedKeys();
				if (generatedKeys.next()) {
					sink = generatedKeys.getInt(1);
				} else {
					throw new Exception("No return id.");
				}
			}

			// add connection
			sql = "INSERT INTO AnchorToEntity(anchorId,entityId) VALUES(?, ?)";
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setInt(1, source);
			preparedStatement.setInt(2, sink);
			preparedStatement.execute();
			
			String[] splitAnchor = triplet[0].split(" ");
			if (splitAnchor.length > 1) {
				for (String partialAnchor : splitAnchor) {
					if (!stopWords.contains(partialAnchor.toLowerCase())) {
						// partial anchor
						int partialAnchorId = -1;
						sql = "SELECT ID FROM partialAnchorId WHERE partialAnchor IS (?)";
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setString(1, partialAnchor);
						preparedStatement.executeQuery();
						result = preparedStatement.getResultSet();
						while (result.next()) {
							partialAnchorId = result.getInt("ID");
						}
						if (partialAnchorId == -1) {
							sql = "INSERT INTO PartialAnchorId(partialAnchor) VALUES (?)";
							preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
							preparedStatement.setString(1, partialAnchor);
							preparedStatement.executeUpdate();
							generatedKeys = preparedStatement.getGeneratedKeys();
							if (generatedKeys.next()) {
								partialAnchorId = generatedKeys.getInt(1);
							} else {
								throw new Exception("No return id.");
							}
						}
						
						// add connection
						sql = "INSERT INTO PartialAnchorToAnchor(partialAnchorId,anchorId) VALUES(?, ?)";
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setInt(1, partialAnchorId);
						preparedStatement.setInt(2, source);
						preparedStatement.execute();
					}
				}
			}

			if (lineCounter % 100000 == 0)
				System.out.println("	processed lines: " + lineCounter);
		}

		// create indices
		System.out.println("Create index for EntityId...");
		stmt = connection.createStatement();
		sql = "CREATE INDEX indexEntityID ON EntityId(id)";
		stmt.executeUpdate(sql);
		
		System.out.println("Create index for AnchorId...");
		stmt = connection.createStatement();
		sql = "CREATE INDEX indexAnchorID ON AnchorId(id)";
		stmt.executeUpdate(sql);
		
		System.out.println("Create index for PartialAnchorId...");
		stmt = connection.createStatement();
		sql = "CREATE INDEX indexPartialAnchorID ON PartialAnchorId(id)";
		stmt.executeUpdate(sql);
		
		System.out.println("Create index for AnchorToEntity...");
		stmt = connection.createStatement();
		sql = "CREATE INDEX indexAnchor ON AnchorToEntity(anchorId)";
		stmt.executeUpdate(sql);

		System.out.println("Create index for PartialAnchorToAnchor...");
		stmt = connection.createStatement();
		sql = "CREATE INDEX indexPartialAnchor ON PartialAnchorToAnchor(partialAnchorId)";
		stmt.executeUpdate(sql);

		connection.close();

		long timeEnd = System.nanoTime();
		double passedTime = (timeEnd - timeStart) / 60000000000.0;
		System.out.println("Passed time: " + passedTime + " mins");
	}

}
