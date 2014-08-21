package datatypes;

import java.util.HashMap;
import java.util.LinkedList;


public class Graph <T extends Comparable<T>>{
	public HashMap<T, Node<T>> nodeMap;
	
	public Graph(){
		nodeMap = new HashMap<T, Node<T>>();
	}
	
	public void addNode(T content){
		nodeMap.put(content, new Node<T>(content));
	}
	
	public Node<T> getNode(T content){
		return nodeMap.get(content);
	}
	
	public boolean addEdge(T contentSource, T contentSink){
		Node<T> source = getNode(contentSource);
		Node<T> sink = getNode(contentSink);
		if(source != null && sink != null) addEdge(source, sink);
		else return false;
		return true;
	}
	
	public void addEdge(Node<T> source, Node<T> sink){
		GraphEdge<T> edge = new GraphEdge<T>(source, sink);
		source.outgoingEdges.add(edge);
		sink.incomingEdges.add(edge);
	}
	
	public boolean removeEdge(T contentSource, T contentSink){
		Node<T> source = getNode(contentSource);
		Node<T> sink = getNode(contentSink);
		if(source != null && sink != null) return removeEdge(source, sink);
		else return false;
	}
	
	public boolean removeEdge(Node<T> source, Node<T> sink){
		GraphEdge<T> edge = null;
		for(GraphEdge<T> tmp: source.outgoingEdges){
			if(tmp.sink.equals(sink)){
				edge = tmp;
				break;
			}
		}
		if(edge == null) return false;
		boolean result = true;
		result = (result && source.outgoingEdges.remove(edge));
		result = (result && sink.incomingEdges.remove(edge));
		return result;
	}
	
	public boolean removeNode(T content){
		Node<T> node = getNode(content);
		if(node == null) return false;
		removeNode(node);
		return true;
	}
	
	public void removeNode(Node<T> node){
		LinkedList<GraphEdge<T>> tmpList = new LinkedList<GraphEdge<T>>();
		for(GraphEdge<T> edge: node.outgoingEdges){
			tmpList.add(edge);
		}
		for(GraphEdge<T> edge: tmpList){
			removeEdge(node, edge.sink);
		}
		
		tmpList = new LinkedList<GraphEdge<T>>();
		for(GraphEdge<T> edge: node.incomingEdges){
			tmpList.add(edge);
		}
		for(GraphEdge<T> edge: tmpList){
			removeEdge(edge.source, node);
		}
		
		nodeMap.remove(node.content);
	}
	
	public int size(){
		return nodeMap.size();
	}
	
	public double avrgDegree(){
		double tmp = 0.0;
		for(Node<T> node: nodeMap.values()){
			tmp += node.degree();
		}
		return tmp / ((double)size());
	}
}





