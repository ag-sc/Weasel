package h2;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.TreeSet;

import datatypes.H2List;
import fileparser.AnchorFileParser;

public class H2BuilderCore {
	
	String dbPath, username, password;
	AnchorFileParser anchorParser;
	PreparedStatement preparedStatement;
	Statement stmt;
	String sql;
	ResultSet generatedKeys, result;
	
	public H2BuilderCore(String dbPath, String username, String password){
		this.dbPath = dbPath;
		this.username = username;
		this.password = password;
	}

	protected int getId(String s, String searchQuery, String insertQuery, Connection connection) throws Exception {
		int value = -1;

		// source
		sql = "SELECT ID FROM ANCHORID WHERE ANCHOR IS (?)";
		preparedStatement = connection.prepareStatement(searchQuery);
		preparedStatement.setString(1, s);
		preparedStatement.executeQuery();
		result = preparedStatement.getResultSet();
		while (result.next()) {
			value = result.getInt("ID");
		}
		if (value == -1) {
			sql = "INSERT INTO AnchorId(anchor) VALUES (?)";
			preparedStatement = connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS);
			preparedStatement.setString(1, s);
			preparedStatement.executeUpdate();
			generatedKeys = preparedStatement.getGeneratedKeys();
			if (generatedKeys.next()) {
				value = generatedKeys.getInt(1);
			} else {
				throw new Exception("No return id.");
			}
		}
		
		return value;
	}
	
	protected void addListEntry(int id, int newListEntry, String searchQuery, String insertQuery, String updateQuery, Connection connection) throws SQLException{
		String list = null;
		String newEntry = Integer.toString(newListEntry);
		preparedStatement = connection.prepareStatement(searchQuery);
		preparedStatement.setInt(1, id);
		preparedStatement.executeQuery();
		result = preparedStatement.getResultSet();
		while (result.next()) {
			list = result.getString(1);
		}
		if (list == null) {
			preparedStatement = connection.prepareStatement(insertQuery);
			preparedStatement.setInt(1, id);
			preparedStatement.setString(2, newEntry);
			preparedStatement.execute();
		}else{
			TreeSet<String> set = H2List.stringToSet(list);
			if(!set.contains(newEntry)){
				set.add(newEntry);
				String newList = H2List.setToString(set);
				preparedStatement = connection.prepareStatement(updateQuery);
				preparedStatement.setString(1, newList);
				preparedStatement.setInt(2, id);
				preparedStatement.execute();
			}
		}
	}

}
