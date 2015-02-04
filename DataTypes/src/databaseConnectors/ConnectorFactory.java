package databaseConnectors;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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
			InMemoryConnector connector = new InMemoryConnector(inMemoryDataContainerFilePath);
			dbReferences.put(inMemoryDataContainerFilePath, connector);
			System.out.println("Connector loaded.");
			return connector;
		}
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
