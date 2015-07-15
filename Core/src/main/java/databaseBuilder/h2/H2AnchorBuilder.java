package main.java.databaseBuilder.h2;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.TreeSet;

import main.java.utility.Stopwatch;
import main.java.databaseBuilder.fileparser.AnchorFileParser;
import main.java.databaseBuilder.fileparser.StopWordParser;

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
		int totalNumberOfAnchorReferences = 0;
		String quadruple[];
		String searchQuery, insertQuery, updateQuery;
		Stopwatch sw = new Stopwatch(Stopwatch.UNIT.MINUTES);

		Class.forName("org.h2.Driver");
		Connection connection = DriverManager.getConnection("jdbc:h2:" + dbPath + ";LOG=0;CACHE_SIZE=65536;LOCK_MODE=0;UNDO_LOG=0", username, password);
		while ((quadruple = anchorParser.parse()) != null) {
			lineCounter++;
			try{
			totalNumberOfAnchorReferences += Integer.parseInt(quadruple[2]);
			}catch(Exception e){
				System.err.println("Error parsing number of anchor references for:" + quadruple[0] + " - " + quadruple[1]);
				e.printStackTrace();
				continue;
			}
			
			searchQuery = "SELECT ID FROM ANCHORID WHERE ANCHOR IS (?)";
			insertQuery = "INSERT INTO AnchorId(anchor) VALUES (?)";
			int source = getId(quadruple[0], searchQuery, insertQuery, connection);
			searchQuery = "SELECT ID FROM EntityId WHERE entity IS (?)";
			insertQuery = "INSERT INTO EntityId(entity) VALUES (?)";
			int sink = getId(quadruple[1], searchQuery, insertQuery, connection);
			
			searchQuery = "SELECT entityIdList FROM AnchorToEntity WHERE anchorId IS (?)";
			insertQuery = "INSERT INTO AnchorToEntity(anchorId, entityIdList) VALUES (?,?)";
			updateQuery = "UPDATE anchorToEntity SET entityIdList = (?) WHERE anchorId = (?)";

			String entry = Integer.toString(sink) + "_" + quadruple[2] + "_" + quadruple[3];
			addListEntry(source, entry, searchQuery, insertQuery, updateQuery, connection);

			if (lineCounter % 1000000 == 0){
				System.out.println("	processed lines: " + lineCounter + "\ttime passed: " + sw.stop() + " minutes");
				sw.start();
			}
				
		}
		System.out.println("Done with anchors. Processed lines: " + lineCounter);
		
//		Statement stmt = connection.createStatement();
//		String sql_tmp = "INSERT INTO MetaInfo(TOTALNUMBEROFANCHORREFERENCES) VALUES (" + totalNumberOfAnchorReferences + ")";
//		stmt.executeUpdate(sql_tmp);

		// create indices		
		System.out.println("Create index for AnchorId...");
		stmt = connection.createStatement();
		sql = "CREATE INDEX indexAnchorID ON AnchorId(id)";
		stmt.executeUpdate(sql);

		connection.commit();
		connection.close();
		
		long timeEnd = System.nanoTime();
		double passedTime = (timeEnd - timeStart) / 60000000000.0;
		System.out.println("Passed time: " + passedTime + " mins");
	}

}
