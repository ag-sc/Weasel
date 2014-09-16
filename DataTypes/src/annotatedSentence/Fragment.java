package annotatedSentence;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.TreeSet;

public class Fragment implements Comparable<Fragment>{

	public double probability = 0.0;
	public TreeSet<String> candidates;
	int start;
	int stop;
	String value;
	
	public Fragment(int start, int stop, String value, double probability) {
		this.start = start;
		this.stop = stop;
		this.value = value;
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
	
	public void setValue(String value){
		this.value = value;
	}
	
	public String getValue(){
		return value;
	}
	
	public void addCandidats(LinkedList<String> newCandidates){
		candidates.addAll(newCandidates);
	}
	
	public void addCandidats(TreeSet<String> newCandidates){
		candidates.addAll(newCandidates);
	}

}
