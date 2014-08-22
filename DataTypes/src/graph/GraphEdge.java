package graph;

public class GraphEdge <T extends Comparable<T>>{
	public Node<T> source;
	public Node<T> sink;
	
	public GraphEdge(Node<T> source, Node<T> sink){
		this.source = source;
		this.sink = sink;
	}
}
