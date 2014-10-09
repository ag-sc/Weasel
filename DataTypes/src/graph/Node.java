package graph;

import java.util.HashSet;

public class Node <T extends Comparable<T>> implements Comparable<Node<T>>{
	public HashSet<GraphEdge<T>> incomingEdges;
	public HashSet<GraphEdge<T>> outgoingEdges;
	public T content;
	
	public Node(T content){
		this.content = content;
		incomingEdges = new HashSet<GraphEdge<T>>();
		outgoingEdges = new HashSet<GraphEdge<T>>();
	}
	
	public int degree(){
		return incomingEdges.size() + outgoingEdges.size();
	}

	@Override
	public int compareTo(Node<T> node) {
		return content.compareTo(node.content);
	}
	
	public String toString(){
		return content.toString();
	}

}
