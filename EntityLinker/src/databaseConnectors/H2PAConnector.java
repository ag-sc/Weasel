package databaseConnectors;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;

public class H2PAConnector extends H2Connector {

	public H2PAConnector(String dbPath, String username, String password, String sql) throws ClassNotFoundException, SQLException {
		super(dbPath, username, password, sql);
		// TODO Auto-generated constructor stub
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
				list.add(result.getString(1));
			}
		} catch (SQLException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return list;
	}
	
}
