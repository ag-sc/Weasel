package databaseConnectors;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.TreeSet;

import datatypes.H2List;

public class H2Connector extends DatabaseConnector {

	String dbPath, sql;
	Connection connection;
	
	public H2Connector(String dbPath, String username, String password, String sql) throws ClassNotFoundException, SQLException {
		this(dbPath, username, password, sql, true);
	}
	
	public H2Connector(String dbPath, String username, String password, String sql, boolean connectToLocalServer) throws ClassNotFoundException, SQLException {
		this.dbPath = dbPath;
		this.sql = sql;
		
		Class.forName("org.h2.Driver");
		if(connectToLocalServer) connection = DriverManager.getConnection("jdbc:h2:tcp://localhost:9092/" + dbPath, username, password);
		else connection = DriverManager.getConnection("jdbc:h2:" + dbPath, username, password);
		
	}
	
	private String _simpleQuery(String input, String columnName, String sql){
		String value = null;
		try {
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setString(1, input);
			preparedStatement.executeQuery();
			ResultSet result = preparedStatement.getResultSet();
			while (result.next()) {
				value = result.getString(columnName);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return value;
	}
	
	public String resolveID (String id){
		String sql = "SELECT Entity FROM EntityID WHERE ID IS (?)";
		return _simpleQuery(id, "Entity", sql);
	}
	
	public Integer resolveName(String name){
		String sql = "SELECT id FROM EntityID WHERE ENTITY IS (?)";
		String tmp = _simpleQuery(name, "id", sql);
		if(tmp != null) return Integer.parseInt(tmp);
		return null;
	}

	@Override
	public LinkedList<String> getFragmentTargets(String fragment) {
		LinkedList<String> list = new LinkedList<String>();
		
		try {
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setString(1, fragment);
			preparedStatement.executeQuery();
			ResultSet result = preparedStatement.getResultSet();
			String tmp = null;
			while (result.next()) {
				tmp = result.getString(1);
			}
			TreeSet<String> tmpSet = H2List.stringToSet(tmp);
			list = new LinkedList<String>(tmpSet);
		} catch (SQLException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return list;
	}

	@Override
	public boolean fragmentExists(String fragment) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void close() {
		try {
			connection.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
