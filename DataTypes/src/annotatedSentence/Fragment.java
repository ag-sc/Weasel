package annotatedSentence;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.LinkedList;
import java.util.TreeSet;

import configuration.Config;

public class Fragment implements Comparable<Fragment>{

	public double probability = 0.0;
	private TreeSet<Candidate> candidates;
	public int start, stop;
	String id;
	String entity = null;
	public String originWord ="<none>";
	String originEntity = "";
	
	public String getOriginEntity() {
		return originEntity;
	}

	public void setOriginEntity(String originEntity) {
		this.originEntity = originEntity;
	}

	public Fragment(String originWord){
		this.originWord = originWord;
		candidates = new TreeSet<Candidate>();
	}
	
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
	
	public void setEntity(String entity){
		if(entity == null) this.entity = null;
		else this.entity = new String(entity);
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
			if(tmp[0] == null) System.out.println("tmp[0] == null for: " + s);
			if(tmp.length == 2){
				candidates.add(new Candidate(tmp[0], Integer.parseInt(tmp[1])));
			}else{
				candidates.add(new Candidate(s, (int)Math.floor(Math.random() * 100))); // legacy code for databases without anchor count
			}	
		}
	}
	
	public void addCandidates(Collection<Candidate> newCandidates){
		candidates.addAll(newCandidates);
	}
	
	public int length(){
		return stop - start + 1;
	}

}
