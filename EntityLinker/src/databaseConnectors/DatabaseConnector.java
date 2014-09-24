package databaseConnectors;

import java.util.LinkedList;

public abstract class DatabaseConnector {
	
	public abstract LinkedList<String> getFragmentTargets(String fragment);
	public abstract boolean fragmentExists(String fragment);
	public abstract void close();
}
