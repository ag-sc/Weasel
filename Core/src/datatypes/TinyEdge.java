package datatypes;

import java.io.Serializable;

public class TinyEdge implements Comparable<TinyEdge>, Serializable{
	private static final long serialVersionUID = -7147322759738145354L;
	public String target;
	public float weight;
	
	public TinyEdge(String target){
		this.target = new String(target);
	}
	
	@Override
	public int compareTo(TinyEdge edge) {
		return target.compareTo(edge.target);
	}
	
	public String toString(){
		return target + " (" + weight +")";
	}
}
