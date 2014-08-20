package datatypes;

public class TinyEdge implements Comparable<TinyEdge>{
	public String target;
	public double weight;
	
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
