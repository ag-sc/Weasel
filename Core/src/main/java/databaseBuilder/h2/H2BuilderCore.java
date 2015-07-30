package main.java.databaseBuilder.h2;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import main.java.databaseBuilder.fileparser.AnchorFileParser;
import main.java.datatypes.H2List;

/**
 * @author Felix Tristram
 * Base class that other H2 related database builder classes inherit from.
 * Contains functionality for searching and inserting in the H2 database.
 */
public class H2BuilderCore extends H2Core{
	
	AnchorFileParser anchorParser;
	
	public H2BuilderCore(String dbPath, String username, String password){
		super(dbPath, username, password);
	}

	/**
	 * Return the id of an anchor, create a new DB entry if it does not exist yet.
	 * @param anchor The anchor String to search for.
	 * @param searchQuery SQL query for finding the id.
	 * @param insertQuery SQL query for creating the new entry for anchor.
	 * @param connection Connection object for the used database.
	 * @return The found or generated id.
	 * @throws Exception
	 */
	protected int getId(String anchor, String searchQuery, String insertQuery, Connection connection) throws Exception {
		int value = -1;

		// source
		sql = "SELECT ID FROM ANCHORID WHERE ANCHOR IS (?)";
		preparedStatement = connection.prepareStatement(searchQuery);
		preparedStatement.setString(1, anchor);
		preparedStatement.executeQuery();
		result = preparedStatement.getResultSet();
		while (result.next()) {
			value = result.getInt("ID");
		}
		if (value == -1) {
			sql = "INSERT INTO AnchorId(anchor) VALUES (?)";
			preparedStatement = connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS);
			preparedStatement.setString(1, anchor);
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
	
	/**
	 * Search for a given list entry. Create it if it does not exists, append to it if it does.
	 * @param id The id of the desired entry.
	 * @param newEntry New entry for the list.
	 * @param searchQuery SQL for finding the list with the given id.
	 * @param insertQuery SQL for creating a list where none exists yet.
	 * @param updateQuery SQL for appending to a already existing list.
	 * @param connection Connection object for the database.
	 * @throws SQLException
	 */
	protected void addListEntry(int id, String newEntry, String searchQuery, String insertQuery, String updateQuery, Connection connection) throws SQLException{
		String list = null;
		preparedStatement = connection.prepareStatement(searchQuery);
		preparedStatement.setInt(1, id);
		preparedStatement.executeQuery();
		result = preparedStatement.getResultSet();
		while (result.next()) {
			list = result.getString(1); break;
		}
		if (list == null) {
			preparedStatement = connection.prepareStatement(insertQuery);
			preparedStatement.setInt(1, id);
			preparedStatement.setString(2, newEntry);
			preparedStatement.execute();
		}else{
			list += H2List.delimiter + newEntry;
			preparedStatement = connection.prepareStatement(updateQuery);
			preparedStatement.setString(1, list);
			preparedStatement.setInt(2, id);
			preparedStatement.execute();
			
			
//			TreeSet<String> set = H2List.stringToSet(list);
//			if(!set.contains(newEntry)){
//				set.add(newEntry);
//				String newList = H2List.setToString(set);
//				preparedStatement = connection.prepareStatement(updateQuery);
//				preparedStatement.setString(1, newList);
//				preparedStatement.setInt(2, id);
//				preparedStatement.execute();
//			}
		}
	}

}
