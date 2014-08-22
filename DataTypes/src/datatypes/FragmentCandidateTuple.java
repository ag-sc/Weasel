package datatypes;

public class FragmentCandidateTuple implements Comparable<FragmentCandidateTuple>{
	public String candidate;
	public EntityOccurance entityOccurance;
	
	public FragmentCandidateTuple(String candidate, EntityOccurance entityOccurance){
		this.candidate = candidate;
		this.entityOccurance = entityOccurance;
	}

	@Override
	public int compareTo(FragmentCandidateTuple tuple) {
		return candidate.compareTo(tuple.candidate);
	}
}
