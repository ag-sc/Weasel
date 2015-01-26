package annotatedSentence;

import java.util.Collection;
import java.util.LinkedList;
import java.util.TreeSet;

public class Fragment implements Comparable<Fragment>{

	public double probability = 0.0;
	private TreeSet<Candidate> candidates;
	public int start, stop;
	String id;
	String entity = "";
	public String originWord ="<none>";
	
	public Fragment(int start, int stop, String id, double probability) {
		this.start = start;
		this.stop = stop;
		this.id = id;
		this.probability = probability;
		candidates = new TreeSet<Candidate>();
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
	
	public TreeSet<Candidate> getCandidates(){
		return candidates;
	}
	
	public boolean containsEntity(String entity){
		return candidates.contains(new Candidate(entity, 0));
	}
	
	public int getCandidatesSize(){
		return candidates.size();
	}
	
	public void addCandidateStrings(Collection<String> newCandidates){
		for(String s: newCandidates){
			String[] tmp = s.split("_");
			candidates.add(new Candidate(tmp[0], Integer.parseInt(tmp[1])));
//			candidates.add(new Candidate(s, (int)Math.floor(Math.random() * 100)));
		}
	}
	
	public void addCandidates(Collection<Candidate> newCandidates){
		candidates.addAll(newCandidates);
	}
	
	public int length(){
		return stop - start + 1;
	}

}
