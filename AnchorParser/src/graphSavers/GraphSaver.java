package graphSavers;

import java.util.LinkedList;

import datatypes.Edge;

public abstract class GraphSaver {

	public abstract void store(String subject, LinkedList<Edge<String, String>> currentList);
	
}
