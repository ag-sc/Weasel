package main.java.databaseBuilder.h2;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;

import main.java.utility.Stopwatch;
import main.java.databaseBuilder.fileparser.WikiParser;

public class H2PageLinksBuilder extends H2BuilderCore {

	WikiParser parser;

	public H2PageLinksBuilder(String dbPath, String pageLinksFilePath, String username, String password) throws IOException {
		super(dbPath, username, password);
		parser = new WikiParser(pageLinksFilePath);
	}

	public void run() throws Exception {
		long timeStart = System.nanoTime();
		int lineCounter = 0;
		String tuple[];
		String searchQuery, insertQuery, updateQuery;

		Stopwatch sw = new Stopwatch(Stopwatch.UNIT.MINUTES);

		Class.forName("org.h2.Driver");
		Connection connection = DriverManager.getConnection("jdbc:h2:" + dbPath + ";LOG=0;CACHE_SIZE=65536;LOCK_MODE=0;UNDO_LOG=0", username, password);
		while ((tuple = parser.parseTuple()) != null) {
			if (tuple.length == 2) {
				lineCounter++;

				searchQuery = "SELECT ID FROM ENTITYID WHERE ENTITY IS (?)";
				insertQuery = "INSERT INTO ENTITYID(ENTITY) VALUES (?)";
				int source = getId(tuple[0], searchQuery, insertQuery, connection);
				int sink = getId(tuple[1], searchQuery, insertQuery, connection);

				searchQuery = "SELECT entitySinkIdList FROM EntityToEntity WHERE entitySourceId IS (?)";
				insertQuery = "INSERT INTO EntityToEntity(entitySourceId, entitySinkIdList) VALUES (?,?)";
				updateQuery = "UPDATE EntityToEntity SET entitySinkIdList = (?) WHERE entitySourceId = (?)";
				addListEntry(source, Integer.toString(sink), searchQuery, insertQuery, updateQuery, connection);

				if (lineCounter % 1000000 == 0) {
					sw.stop();
					System.out.println("Processed lines:\t" + lineCounter + "\tTime since last message: " + sw + " minutes");
					sw.start();
				}
			}
		}
		
		connection.commit();
		connection.close();

		long timeEnd = System.nanoTime();
		double passedTime = (timeEnd - timeStart) / 60000000000.0;
		System.out.println("Passed time: " + passedTime + " mins");
	}

}
