package h2;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Map.Entry;
import java.util.TreeSet;

import datatypes.CountingMap;
import stopwatch.Stopwatch;
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
		Stopwatch sw = new Stopwatch(Stopwatch.UNIT.SECONDS);

		int johnCounter = 0;
		int johnLength = 0;
		CountingMap cm = new CountingMap();
//		Class.forName("org.h2.Driver");
//		Connection connection = DriverManager.getConnection("jdbc:h2:" + dbPath + ";LOG=0;CACHE_SIZE=65536;LOCK_MODE=0;UNDO_LOG=0", username, password);
		while ((triplet = anchorParser.parseTriplet()) != null) {
			lineCounter++;
			
//			searchQuery = "SELECT ID FROM ANCHORID WHERE ANCHOR IS (?)";
//			insertQuery = "INSERT INTO AnchorId(anchor) VALUES (?)";
//			int source = getId(triplet[0], searchQuery, insertQuery, connection);
//			searchQuery = "SELECT ID FROM EntityId WHERE entity IS (?)";
//			insertQuery = "INSERT INTO EntityId(entity) VALUES (?)";
//			int sink = getId(triplet[1], searchQuery, insertQuery, connection);
//			
//			searchQuery = "SELECT entityIdList FROM AnchorToEntity WHERE anchorId IS (?)";
//			insertQuery = "INSERT INTO AnchorToEntity(anchorId, entityIdList) VALUES (?,?)";
//			updateQuery = "UPDATE anchorToEntity SET entityIdList = (?) WHERE anchorId = (?)";
//
//			addListEntry(source, Integer.toString(sink), searchQuery, insertQuery, updateQuery, connection);
			
			
			String[] splitAnchor = triplet[0].split(" ");
			if (splitAnchor.length > 1) {
				if(splitAnchor[0].equals("John")){
					johnCounter++;
					johnLength += triplet[0].length();
					
					String shortest = "";
					int count = 1000000;
					for(String s: splitAnchor){
						if(s.length() > 3 && cm.get(s) < count){
							shortest = s;
							count = cm.get(s);
						}
					}
					cm.increase(shortest);
				}
//				for (String partialAnchor : splitAnchor) {
//					if (!stopWords.contains(partialAnchor.toLowerCase())) {
//						searchQuery = "SELECT ID FROM PartialAnchorID WHERE PartialAnchor IS (?)";
//						insertQuery = "INSERT INTO PartialAnchorID(PartialAnchor) VALUES (?)";
//						source = getId(partialAnchor, searchQuery, insertQuery, connection);
//						
//						searchQuery = "SELECT entityIdList FROM PartialAnchorToEntity WHERE partialAnchorId IS (?)";
//						insertQuery = "INSERT INTO PartialAnchorToEntity(partialAnchorId, entityIdList) VALUES (?,?)";
//						updateQuery = "UPDATE PartialAnchorToEntity SET entityIdList = (?) WHERE partialAnchorId = (?)";
//
//						addListEntry(source, Integer.toString(sink), searchQuery, insertQuery, updateQuery, connection);
//						
//						sql = "INSERT INTO PartialAnchorToAnchor(partialAnchorId, anchor) VALUES (?,?)";
//						preparedStatement = connection.prepareStatement(sql);
//						preparedStatement.setInt(1, source);
//						preparedStatement.setString(2, triplet[0]);
//						preparedStatement.execute();
//					}
//				}
			}

			if (lineCounter % 1000000 == 0){
				System.out.println("	processed lines: " + lineCounter + "\ttime passed: " + sw.stop());
				sw.start();
			}
				
		}

		// create indices		
//		System.out.println("Create index for AnchorId...");
//		stmt = connection.createStatement();
//		sql = "CREATE INDEX indexAnchorID ON AnchorId(id)";
//		stmt.executeUpdate(sql);
//		
//		System.out.println("Create index for PartialAnchorId...");
//		stmt = connection.createStatement();
//		sql = "CREATE INDEX indexPartialAnchorID ON PartialAnchorId(id)";
//		stmt.executeUpdate(sql);
//
//		connection.close();

		long timeEnd = System.nanoTime();
		double passedTime = (timeEnd - timeStart) / 60000000000.0;
		System.out.println("Passed time: " + passedTime + " mins");
		
		System.out.println(johnCounter + " - avg length: " + ((double)johnLength / (double)johnCounter));
		int longest = 0;
		String name = "";
		for(Entry<String, Integer> e: cm.map.entrySet()){
			if(e.getValue() > longest){
				longest = e.getValue();
				name = e.getKey();
			}
		}
		System.out.println("Longest: " + name + " - length: " + longest);
	}

}
