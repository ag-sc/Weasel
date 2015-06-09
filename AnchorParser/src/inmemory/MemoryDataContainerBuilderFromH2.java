package inmemory;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import stopwatch.Stopwatch;
import datatypes.InMemoryDataContainer;
import datatypes.StringEncoder;
import edu.jhu.nlp.wikipedia.PageCallbackHandler;
import edu.jhu.nlp.wikipedia.WikiPage;
import edu.jhu.nlp.wikipedia.WikiXMLParser;
import edu.jhu.nlp.wikipedia.WikiXMLParserFactory;

public class MemoryDataContainerBuilderFromH2 {
	
	static int abstractCounter = 0;

	public static void run(String h2Connection, String inMemoryDataContainerPath) throws IOException, SQLException, ClassNotFoundException {
		System.out.println("Starting inMemoryDataContainer creation...");
		Stopwatch sw = new Stopwatch(Stopwatch.UNIT.SECONDS);
		final Map<String, Integer> entityToID = new HashMap<String, Integer>();
		final Map<String, Integer> anchorIDMap = new HashMap<String, Integer>();
		int[][] anchorToCandidates;
		int[][] anchorToCandidatesCount;
		String[] idToEntity;
		
		Class.forName("org.h2.Driver");
//		Connection connection = DriverManager.getConnection("jdbc:h2:~/anchor_db/h2/h2_anchors", "sa", "");
		Connection connection = DriverManager.getConnection("jdbc:h2:" + h2Connection + ";IFEXISTS=TRUE", "sa", "");
		
		// get max values
		String sql = "SELECT max(id)  FROM ENTITYID ";
		int maxEntityID = 0;
		try {
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			preparedStatement.executeQuery();
			ResultSet result = preparedStatement.getResultSet();
			while (result.next()) {
				maxEntityID = result.getInt("max(id)");
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		System.out.println("Max Entity ID: " + maxEntityID);
		
		sql = "SELECT max(id)  FROM ANCHORID ";
		int maxAnchorID = 0;
		try {
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			preparedStatement.executeQuery();
			ResultSet result = preparedStatement.getResultSet();
			while (result.next()) {
				maxAnchorID = result.getInt("max(id)");
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		System.out.println("Max Anchor ID: " + maxAnchorID);
		
		// entities
		System.out.println("Work on all entities");
		sql = "SELECT *  FROM ENTITYID where id is (?)";
		idToEntity = new String[maxEntityID + 1];
		final Map<Integer, Integer> redirects = new HashMap<Integer, Integer>();
		final Set<Integer> disambiguations = new HashSet<Integer>();
		for(int i = 1; i <= maxEntityID; i++){
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setInt(1, i);
			preparedStatement.executeQuery();
			ResultSet result = preparedStatement.getResultSet();
			String entityName = null;
			Integer redirect = null;
			Boolean isDismabiguation = null;
			while (result.next()) {
				entityName = result.getString("ENTITY");
				redirect = result.getInt("REDIRECTTO");
				isDismabiguation  = result.getBoolean("ISDISAMBIGUATION");
			}
			idToEntity[i] = entityName;
			entityToID.put(entityName, i);
			if(redirect > -1) redirects.put(i, redirect);
			if(isDismabiguation == true) disambiguations.add(i);
			
			if(i % 100000 == 0) System.out.println(i + " / " + maxEntityID);
		}
		System.out.println("Done. Number of redirects: " + redirects.size() + " - Number of disambiguations: " + disambiguations.size());
		
		// anchors
		System.out.println("Work on all anchors");
		sql = "SELECT anchor  FROM anchorid where id is (?)";
		for(int i = 1; i <= maxAnchorID; i++){
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setInt(1, i);
			preparedStatement.executeQuery();
			ResultSet result = preparedStatement.getResultSet();
			String tmp = null;
			while (result.next()) {
				tmp = result.getString("ANCHOR");
			}
			anchorIDMap.put(tmp, i);
			
			if(i % 100000 == 0) System.out.println(i + " / " + maxAnchorID);
		}
		
		// anchor to entity
		int totalNumberOfAnchorReferences = 0;
		System.out.println("Work on anchor to entity");
		anchorToCandidates = new int[maxAnchorID + 1][];
		anchorToCandidatesCount = new int[maxAnchorID + 1][];
		sql = "SELECT entityidlist  FROM ANCHORTOENTITY where id is (?)";
		for(int i = 1; i <= maxAnchorID; i++){
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setInt(1, i);
			preparedStatement.executeQuery();
			ResultSet result = preparedStatement.getResultSet();
			String tmp = null;
			while (result.next()) {
				tmp = result.getString("entityidlist");
			}
			String[] splitList = tmp.split(" ");
			anchorToCandidates[i] = new int[splitList.length];
			anchorToCandidatesCount[i] = new int[splitList.length];
			
			for (int j = 0; j < splitList.length; j++) {
				String[] idPlusCount = splitList[j].split("_");
				if(idPlusCount.length != 2){
					System.err.println(tmp);
					break;
				}
				Integer int1 = Integer.parseInt(idPlusCount[0]);
				if(int1 == null) System.out.println(idPlusCount[0]);
				anchorToCandidates[i][j] = int1;
				Integer int2 = Integer.parseInt(idPlusCount[1]);
				if(int1 == null) System.out.println(idPlusCount[1]);
				anchorToCandidatesCount[i][j] = int2;
				totalNumberOfAnchorReferences += int2;
			}

			if(i % 100000 == 0) System.out.println(i + " / " + maxAnchorID);
		}

		
		// Save data to file
		System.out.println("Write data to file...");
		InMemoryDataContainer data = new InMemoryDataContainer();
		data.anchorID = anchorIDMap;		
		data.anchorToCandidates = anchorToCandidates;		
		data.anchorToCandidatesCount = anchorToCandidatesCount;
		data.idToEntity = idToEntity;
		data.entityToID = entityToID;
		data.redirects = redirects;
		data.disambiguation = disambiguations;
		data.totalNumberOfCandidateReferences = totalNumberOfAnchorReferences;
		
		try {
			ObjectOutputStream out;
			out = new ObjectOutputStream(new FileOutputStream(inMemoryDataContainerPath));
			out.writeObject(data);
			out.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("All done! It took " + sw.stop() + " seconds.");
	}

}
