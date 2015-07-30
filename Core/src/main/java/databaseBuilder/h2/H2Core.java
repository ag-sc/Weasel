package main.java.databaseBuilder.h2;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * @author Felix Tristram
 * Base class that all other H2 related classes are inheriting from. Essentially just a container for variables.
 */
public class H2Core {
	String dbPath, username, password;
	PreparedStatement preparedStatement;
	Statement stmt;
	String sql;
	ResultSet generatedKeys, result;

	public H2Core(String dbPath, String username, String password) {
		this.dbPath = dbPath;
		this.username = username;
		this.password = password;
	}

}
