package main.java.datatypes.databaseConnectors;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import main.java.utility.Stopwatch;

public class ConnectorFactory {

	private static final Map<String, DatabaseConnector> dbReferences = new HashMap<String, DatabaseConnector>();
	
	private ConnectorFactory() {
		// TODO Auto-generated constructor stub
	}

	public static InMemoryConnector getInMemoryConnector(String inMemoryDataContainerFilePath) throws ClassNotFoundException, IOException{
		if(dbReferences.containsKey(inMemoryDataContainerFilePath)){
			return (InMemoryConnector) dbReferences.get(inMemoryDataContainerFilePath);
		}else{
			System.out.println("Connector '" + inMemoryDataContainerFilePath + "' not loaded yet. Loading file...");
			Stopwatch sw = new Stopwatch(Stopwatch.UNIT.MINUTES);
			InMemoryConnector connector = new InMemoryConnector(inMemoryDataContainerFilePath);
			dbReferences.put(inMemoryDataContainerFilePath, connector);
			System.out.println("Connector loaded. Time taken: " + sw.stop() + " minutes");
			return connector;
		}
	}
	
	public static H2Connector getH2Connector(String h2FilePath, String sql) throws ClassNotFoundException, SQLException{
		H2Connector connector = new H2Connector(h2FilePath, "sa", "", sql);
		return connector;
	}
	
	public static boolean deleteConnectorReference(String connectorReferencePath){
		if(dbReferences.containsKey(connectorReferencePath)){
			dbReferences.remove(connectorReferencePath);
			return true;
		}else{
			return false;
		}
	}
}
