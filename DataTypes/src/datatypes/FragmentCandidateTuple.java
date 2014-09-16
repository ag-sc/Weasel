package datatypes;

import annotatedSentence.Fragment;

public class FragmentCandidateTuple implements Comparable<FragmentCandidateTuple>{
	public String candidate;
	public Fragment fragment;
	
	public double weight;
	public double score;
	
	public FragmentCandidateTuple(String candidate, Fragment fragment){
		this.candidate = candidate;
		this.fragment = fragment;
	}

	@Override
	public int compareTo(FragmentCandidateTuple tuple) {
		return candidate.compareTo(tuple.candidate);
	}
}
