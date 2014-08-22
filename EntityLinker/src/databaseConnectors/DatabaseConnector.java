package databaseConnectors;

import java.util.LinkedList;

public abstract class DatabaseConnector {
	
	public abstract LinkedList<String> lookUpFragment(String fragment);
}
