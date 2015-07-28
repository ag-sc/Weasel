package main.java.databaseBuilder.h2;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map.Entry;

import main.java.datatypes.VectorEntry;
import main.java.utility.Stopwatch;

public class H2RemainingDataWriter extends H2Core{

	String vectorMapFilePath;
	String pageRankArrayPath;
	Stopwatch sw = new Stopwatch(Stopwatch.UNIT.MINUTES);
	
	public H2RemainingDataWriter(String dbPath, String username, String password, String vectorMapFilePath, String pageRankArrayPath) {
		super(dbPath, username, password);
		this.vectorMapFilePath = vectorMapFilePath;
		this.pageRankArrayPath = pageRankArrayPath;
	}

	public void run() throws SQLException, ClassNotFoundException, IOException{
		Class.forName("org.h2.Driver");
		Connection connection = DriverManager.getConnection("jdbc:h2:" + dbPath + ";LOG=0;CACHE_SIZE=65536;LOCK_MODE=0;UNDO_LOG=0", username, password);
		processVectorObject(connection);
		processPageRankObject(connection);        
        connection.close();
	}
	
	private void processPageRankObject(Connection connection) throws SQLException, IOException, ClassNotFoundException{
		sw.start();
		System.out.println("PageRank not loaded yet. Loading now...");
		FileInputStream fileInputStream = new FileInputStream(pageRankArrayPath);
		ObjectInputStream objectReader = new ObjectInputStream(fileInputStream);
		double[] pageRankArray = (double[]) objectReader.readObject();
		objectReader.close();
		fileInputStream.close();
		System.out.println("Done. Took " + sw.stop() + " minutes. Add database tables.");
//		double[] pageRankArray = new double[10];
//		for(int id = 0; id < pageRankArray.length; id++) pageRankArray[id] = id;
		
		stmt = connection.createStatement();
        
        for(int id = 0; id < pageRankArray.length; id++){
        	double pr = pageRankArray[id];
        	sql = "UPDATE EntityId SET pageRank = (?) WHERE id = (?)";
        	preparedStatement = connection.prepareStatement(sql);
        	preparedStatement.setDouble(1, pr);
    		preparedStatement.setInt(2, id);
    		preparedStatement.execute();
        }
        System.out.println("All entries processed.");
	}
	
	private void processVectorObject(Connection connection) throws ClassNotFoundException, IOException, SQLException{
		sw.start();
        System.out.println("Load vectorMap...");
		FileInputStream fileInputStream = new FileInputStream(vectorMapFilePath);
		ObjectInputStream objectReader = new ObjectInputStream(fileInputStream);
		System.out.println("Vector map not loaded yet. Loading now...");
		HashMap<Integer, VectorEntry> vectorMap = (HashMap<Integer, VectorEntry>) objectReader.readObject();
		objectReader.close();
		fileInputStream.close();
		System.out.println("Done. Took " + sw.stop() + " minutes. Add database tables.");
			
        stmt = connection.createStatement();  
        
        String semSigVectorSQL = "UPDATE EntityId SET semSigVector = (?) WHERE id = (?)";
        String semSigCountSQL = "UPDATE EntityId SET semSigCount = (?) WHERE id = (?)";
        String tfVectorSQL = "UPDATE EntityId SET tfVector = (?) WHERE id = (?)";
        String tfScoreSQL = "UPDATE EntityId SET tfCount = (?) WHERE id = (?)";
        for(Entry<Integer, VectorEntry> e: vectorMap.entrySet()){
        	Integer id = e.getKey();
        	VectorEntry vEntry = e.getValue();
        	
        	// semSigVector
        	StringBuilder sb = new StringBuilder();
        	for(int i: vEntry.semSigVector){
        		if(i == -1) break;
        		sb.append(i);
        		sb.append(" ");
        	}
        	executeStatement(connection, semSigVectorSQL, id, sb.toString().trim());
        	
        	// semSigCount
        	sb = new StringBuilder();
        	for(int i: vEntry.semSigCount){
        		if(i == 0) break;
        		sb.append(i);
        		sb.append(" ");
        	}
        	executeStatement(connection, semSigCountSQL, id, sb.toString().trim());
        	
        	// tfVector
        	sb = new StringBuilder();
        	for(int i: vEntry.tfVector){
        		if(i == -1) break;
        		sb.append(i);
        		sb.append(" ");
        	}
        	executeStatement(connection, tfVectorSQL, id, sb.toString().trim());
        	
        	// tfScore
        	sb = new StringBuilder();
        	for(float f: vEntry.tfScore){
        		if(f == 0.0) break;
        		sb.append(f);
        		sb.append(" ");
        	}
        	executeStatement(connection, tfScoreSQL, id, sb.toString().trim());
        }
        System.out.println("All entries processed.");
	}
	
	private void executeStatement(Connection connection, String sql, Integer id, String entryValues) throws SQLException{
		preparedStatement = connection.prepareStatement(sql);
		preparedStatement.setString(1, entryValues);
		preparedStatement.setInt(2, id);
		preparedStatement.execute();
	}
}












