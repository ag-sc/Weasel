package main.java.datatypes.databaseConnectors;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.TreeSet;

import main.java.datatypes.H2List;

public class H2Connector extends DatabaseConnector {

	String dbPath, sql;
	Connection connection;
	
	public H2Connector(String dbPath, String username, String password, String sql) throws ClassNotFoundException, SQLException {
		this.dbPath = dbPath;
		this.sql = sql;
		
		// remove ".mv.db" ending from dbPath as H2 adds it automatically
		this.dbPath = dbPath.replace(".mv.db", "");
		
		Class.forName("org.h2.Driver");
		DriverManager.getConnection("jdbc:h2:" + this.dbPath, username, password);
//		if(connectToLocalServer) connection = DriverManager.getConnection("jdbc:h2:" + this.dbPath, username, password);
//		else connection = DriverManager.getConnection("jdbc:h2:" + this.dbPath, username, password);
		
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
	public boolean entityExists(String fragment) {
		if(resolveName(fragment) != null) return true;
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

	@Override
	public int getRedirect(Integer id) {
		// TODO Auto-generated method stub
		return -1;
	}

	@Override
	public boolean isDisambiguation(Integer id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int totalNumberOfEntities() {
		// TODO Auto-generated method stub
		return 0;
	}

//	@Override
//	public int getTotalNumberOfReferences() {
//		int number = 0;
//		Statement stmt;
//		try {
//			stmt = connection.createStatement();
//			String sql_tmp = "SELECT TOTALNUMBEROFANCHORREFERENCES from metainfo where id is 1";
//			stmt.executeQuery(sql_tmp);
//			ResultSet result = stmt.getResultSet();
//			while (result.next()) {
//				number = result.getInt(1);
//				break;
//			}
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return number;
//	}

}
