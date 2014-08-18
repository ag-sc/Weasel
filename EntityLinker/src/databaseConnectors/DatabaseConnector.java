package databaseConnectors;

import java.io.IOException;
import java.util.LinkedList;

public abstract class DatabaseConnector {
	
	public abstract LinkedList<String> lookUpFragment(String fragment);
}
