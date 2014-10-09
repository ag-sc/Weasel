package annotatedSentence;

import java.util.LinkedList;
import java.util.TreeSet;

public class Fragment implements Comparable<Fragment>{

	public double probability = 0.0;
	public TreeSet<String> candidates;
	public int start, stop;
	String id;
	String entity = "";
	
	public Fragment(int start, int stop, String id, double probability) {
		this.start = start;
		this.stop = stop;
		this.id = id;
		this.probability = probability;
		candidates = new TreeSet<String>();
	}
	
	public Fragment(int start, int stop) {
		this(start, stop, null, 0.0);
	}

	@Override
	public int compareTo(Fragment o) {
		if(probability < o.probability) return -1;
		else if (probability > o.probability) return 1;
		else return 0;
	}
	
	public void setID(String id){
		this.id = new String(id);
	}
	
	public String getID(){
		return id;
	}
	
	public void setEntity(String entity){
		this.entity = new String(entity);
	}
	
	public String getEntity(){
		return entity;
	}
	
	public void addCandidats(LinkedList<String> newCandidates){
		candidates.addAll(newCandidates);
	}
	
	public void addCandidats(TreeSet<String> newCandidates){
		candidates.addAll(newCandidates);
	}
	
	public int length(){
		return stop - start + 1;
	}

}
