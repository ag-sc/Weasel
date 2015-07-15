package databaseBuilder.h2;

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
        Connection connection = DriverManager.getConnection("jdbc:h2:" + dbPath, username, password);

        // EntityId Table
        stmt = connection.createStatement();  
        sql = "CREATE TABLE EntityId " +
              "(entity VARCHAR(MAX) not NULL, " +
              " id INTEGER AUTO_INCREMENT, " + 
              " redirectTo INTEGER DEFAULT -1, " + 
              " isDisambiguation BOOLEAN DEFAULT FALSE, " + 
              " SemanticSignature VARCHAR(MAX), " + 
              " PRIMARY KEY ( entity ))"; 
        stmt.executeUpdate(sql);
        
        // AnchorId Table
        stmt = connection.createStatement();  
        sql = "CREATE TABLE AnchorId " +
              "(anchor VARCHAR(MAX) not NULL, " +
              " id INTEGER AUTO_INCREMENT, " + 
              " PRIMARY KEY ( anchor ))"; 
        stmt.executeUpdate(sql);
        
        // AnchorToEntity Table
        stmt = connection.createStatement();  
        sql = "CREATE TABLE AnchorToEntity " +
              "(id INTEGER AUTO_INCREMENT, " +
              " anchorId INTEGER not NULL, " + 
              " entityIdList VARCHAR(MAX) not NULL, " + 
              " PRIMARY KEY ( id ))"; 
        stmt.executeUpdate(sql);
        
        // EntityToEntity Table
        stmt = connection.createStatement();  
        sql = "CREATE TABLE EntityToEntity " +
              "(id INTEGER AUTO_INCREMENT, " +
              " entitySourceId INTEGER not NULL, " + 
              " entitySinkIdList VARCHAR(MAX) not NULL, " + 
              " WeightList VARCHAR(MAX), " + 
              " PRIMARY KEY ( id ))"; 
        stmt.executeUpdate(sql);
        
        // Meta Table
        stmt = connection.createStatement();  
        sql = "CREATE TABLE MetaInfo " +
              "(id INTEGER AUTO_INCREMENT," +
              " totalNumberOfAnchorReferences INTEGER not NULL)"; 
        stmt.executeUpdate(sql);
        
        // Build the important indices to speed up database building
        System.out.println("Create index for EntityID...");
		stmt = connection.createStatement();
		sql = "CREATE INDEX indexEntityID ON EntityId(id)";
		stmt.executeUpdate(sql);
        
        System.out.println("Create index for AnchorToEntity...");
		stmt = connection.createStatement();
		sql = "CREATE INDEX indexAnchor ON AnchorToEntity(anchorId)";
		stmt.executeUpdate(sql);
		
		System.out.println("Create index for EntityToEntity...");
		stmt = connection.createStatement();
		sql = "CREATE INDEX entitySourceIndex ON EntityToEntity(entitySourceId)";
		stmt.executeUpdate(sql);
        
        connection.close();
        
        System.out.println("All done.");
	}

}
