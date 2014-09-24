package h2;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class H2DBCreator {

	String dbPath;
	String username;
	String password;
	
	Statement stmt;
	String sql;
	
	public H2DBCreator(String dbPath) {
		this(dbPath, "sa", "");
	}
	
	public H2DBCreator(String dbPath, String username, String password) {
		this.dbPath = dbPath;
		this.username = username;
		this.password = password;
	}
	
	public void create() throws SQLException, ClassNotFoundException{
		Class.forName("org.h2.Driver");
        Connection conn = DriverManager.getConnection("jdbc:h2:" + dbPath, username, password);

        // EntityId Table
        stmt = conn.createStatement();  
        sql = "CREATE TABLE EntityId " +
              "(entity VARCHAR(10000) not NULL, " +
              " id INTEGER AUTO_INCREMENT, " + 
              " PRIMARY KEY ( entity ))"; 
        stmt.executeUpdate(sql);
        
        // AnchorId Table
        stmt = conn.createStatement();  
        sql = "CREATE TABLE AnchorId " +
              "(anchor VARCHAR(10000) not NULL, " +
              " id INTEGER AUTO_INCREMENT, " + 
              " PRIMARY KEY ( anchor ))"; 
        stmt.executeUpdate(sql);
        
        // PartialAnchorId Table
        stmt = conn.createStatement();  
        sql = "CREATE TABLE PartialAnchorId " +
              "(partialAnchor VARCHAR(1024) not NULL, " +
              " id INTEGER AUTO_INCREMENT, " + 
              " PRIMARY KEY ( partialAnchor ))"; 
        stmt.executeUpdate(sql);
        
        // AnchorToEntity Table
        stmt = conn.createStatement();  
        sql = "CREATE TABLE AnchorToEntity " +
              "(id INTEGER AUTO_INCREMENT, " +
              " anchorId INTEGER not NULL, " + 
              " entityId INTEGER not NULL, " + 
              " PRIMARY KEY ( id ))"; 
        stmt.executeUpdate(sql);
        
        // PartialAnchorToAnchor Table
        stmt = conn.createStatement();  
        sql = "CREATE TABLE PartialAnchorToAnchor " +
              "(id INTEGER AUTO_INCREMENT, " +
              " partialAnchorId INTEGER not NULL, " + 
              " anchorId INTEGER not NULL, " + 
              " PRIMARY KEY ( id ))"; 
        stmt.executeUpdate(sql);
        
     // EntityToEntity Table
        stmt = conn.createStatement();  
        sql = "CREATE TABLE EntityToEntity " +
              "(id INTEGER AUTO_INCREMENT, " +
              " entitySourceId INTEGER not NULL, " + 
              " entitySinkId INTEGER not NULL, " + 
              " PRIMARY KEY ( id ))"; 
        stmt.executeUpdate(sql);
        
        conn.close();
        
        System.out.println("All done.");
	}

}
