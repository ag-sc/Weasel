package databaseConnectors;

import java.util.LinkedList;

import jdbm.PrimaryHashMap;
import jdbm.RecordManager;
import jdbm.RecordManagerFactory;

public class JDBMConnector extends DatabaseConnector{

	@Override
	public String[] lookUpFragment(String fragment) {
		RecordManager recman = RecordManagerFactory.createRecordManager(dbName);
		PrimaryHashMap<String, LinkedList<T>> dbMap = recman.hashMap(mapName);
		return null;
	}

}
