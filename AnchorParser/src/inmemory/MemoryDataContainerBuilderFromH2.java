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
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;

import stopwatch.Stopwatch;
import datatypes.H2List;
import datatypes.InMemoryDataContainer;
import datatypes.PageRankNode;
import fileparser.AnchorFileParser;

public class MemoryDataContainerBuilderFromH2 {

	public MemoryDataContainerBuilderFromH2() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) throws IOException, SQLException, ClassNotFoundException {
		System.out.println("Starting inMemoryDataContainer creation...");
		Stopwatch sw = new Stopwatch(Stopwatch.UNIT.SECONDS);
		Map<String, Integer> entityToID = new HashMap<String, Integer>();
		Map<String, Integer> anchorIDMap = new HashMap<String, Integer>();
		HashMap<Integer, LinkedList<String>>  anchorToCandidateMap = new HashMap<Integer, LinkedList<String>>();
		int[][] anchorToCandidates;
		int[][] anchorToCandidatesCount;
		String[] idToEntity;
		
		Class.forName("org.h2.Driver");
//		Connection connection = DriverManager.getConnection("jdbc:h2:~/anchor_db/h2/h2_anchors", "sa", "");
		Connection connection = DriverManager.getConnection("jdbc:h2:E:/Master Project/data/toyData/anchorH2", "sa", "");
		
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
		sql = "SELECT entity  FROM ENTITYID where id is (?)";
		idToEntity = new String[maxEntityID + 1];
		for(int i = 1; i <= maxEntityID; i++){
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setInt(1, i);
			preparedStatement.executeQuery();
			ResultSet result = preparedStatement.getResultSet();
			String tmp = null;
			while (result.next()) {
				tmp = result.getString("ENTITY");
			}
			idToEntity[i] = tmp;
			entityToID.put(tmp, i);
			
			if(i % 100000 == 0) System.out.println(i + " / " + maxEntityID);
		}
		
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
		
		try {
			ObjectOutputStream out;
			out = new ObjectOutputStream(new FileOutputStream("E:/Master Project/data/toyData/inMemoryDataContainer_fromH2.bin"));
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
