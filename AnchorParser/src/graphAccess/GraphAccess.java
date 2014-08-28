package graphAccess;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.TreeSet;
import java.util.Map.Entry;

import datatypes.Edge;
import datatypes.TinyEdge;

public abstract class GraphAccess {
	
	public abstract void addNode(String node);
	
	public abstract void removeNode(String node);
	
	public abstract void addEdge(String nodeSource, String nodeSink);
	
	public abstract void removeEdge(String nodeSource, String nodeSink);
	
	public abstract TreeSet<String> getIncomingEdges(String node);
	
	public abstract TreeSet<String> getOutgoingEdges(String node);
	
	public abstract Iterator<String>  getGraphIterator();
	
}
