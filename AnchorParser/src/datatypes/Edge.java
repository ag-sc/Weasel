package datatypes;

import java.io.Serializable;

public class Edge<X, Y> implements Serializable {
	private static final long serialVersionUID = 8551637199843321885L;
	public X connectionType;
	public Y target;
	public double weight = 0.0;
	
	public Edge(X connectionType, Y target){
		this.connectionType = connectionType;
		this.target = target;
	}
}
