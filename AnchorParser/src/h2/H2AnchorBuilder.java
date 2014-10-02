package h2;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.TreeSet;

import fileparser.AnchorFileParser;
import fileparser.StopWordParser;

public class H2AnchorBuilder extends H2BuilderCore{

	TreeSet<String> stopWords;

	public H2AnchorBuilder(String dbPath, String anchorFilePath, String username, String password, String stopWordsPath) throws IOException {
		super(dbPath, username, password);
		anchorParser = new AnchorFileParser(anchorFilePath);
		stopWords = StopWordParser.parseStopwords(stopWordsPath);
	}

	public void run() throws Exception {
		long timeStart = System.nanoTime();
		int lineCounter = 0;
		String triplet[];
		String searchQuery, insertQuery, updateQuery;

		Class.forName("org.h2.Driver");
		Connection connection = DriverManager.getConnection("jdbc:h2:" + dbPath + ";LOG=0;CACHE_SIZE=65536;LOCK_MODE=0;UNDO_LOG=0", username, password);
		while ((triplet = anchorParser.parseTriplet()) != null) {
			lineCounter++;
			
			searchQuery = "SELECT ID FROM ANCHORID WHERE ANCHOR IS (?)";
			insertQuery = "INSERT INTO AnchorId(anchor) VALUES (?)";
			int source = getId(triplet[0], searchQuery, insertQuery, connection);
			searchQuery = "SELECT ID FROM EntityId WHERE entity IS (?)";
			insertQuery = "INSERT INTO EntityId(entity) VALUES (?)";
			int sink = getId(triplet[1], searchQuery, insertQuery, connection);
			
			searchQuery = "SELECT entityIdList FROM AnchorToEntity WHERE anchorId IS (?)";
			insertQuery = "INSERT INTO AnchorToEntity(anchorId, entityIdList) VALUES (?,?)";
			updateQuery = "UPDATE anchorToEntity SET entityIdList = (?) WHERE anchorId = (?)";

			addListEntry(source, sink, searchQuery, insertQuery, updateQuery, connection);
			
			String[] splitAnchor = triplet[0].split(" ");
			if (splitAnchor.length > 1) {
				for (String partialAnchor : splitAnchor) {
					if (!stopWords.contains(partialAnchor.toLowerCase())) {
						searchQuery = "SELECT entityIdList FROM PartialAnchorToEntity WHERE partialAnchorId IS (?)";
						insertQuery = "INSERT INTO PartialAnchorToEntity(partialAnchorId, entityIdList) VALUES (?,?)";
						updateQuery = "UPDATE PartialAnchorToEntity SET entityIdList = (?) WHERE partialAnchorId = (?)";

						addListEntry(source, sink, searchQuery, insertQuery, updateQuery, connection);
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
		sql = "CREATE INDEX indexPartialAnchor ON PartialAnchorToEntity(partialAnchorId)";
		stmt.executeUpdate(sql);

		connection.close();

		long timeEnd = System.nanoTime();
		double passedTime = (timeEnd - timeStart) / 60000000000.0;
		System.out.println("Passed time: " + passedTime + " mins");
	}

}
