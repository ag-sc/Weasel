package databaseConnectors;

import java.util.LinkedList;

public abstract class DatabaseConnector {
	
	public abstract String resolveID (String id);
	public abstract Integer resolveName(String name);
	public abstract LinkedList<String> getFragmentTargets(String fragment);
	public abstract boolean fragmentExists(String fragment);
	public abstract void close();
	public abstract int getRedirect(Integer id);
	public abstract boolean isDisambiguation(Integer id);
}
