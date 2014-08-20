package graphAccess;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.TreeSet;
import java.util.Map.Entry;

import datatypes.Edge;
import datatypes.TinyEdge;

public abstract class GraphAccess {

	public abstract void store(String subject, LinkedList<Edge<String, String>> currentList);
	
	public abstract TreeSet<TinyEdge> query(String key);
	
	public abstract Iterator<Entry<String, TreeSet<TinyEdge>>>  getGraphIterator();
	
}
