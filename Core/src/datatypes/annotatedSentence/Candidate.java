package datatypes.annotatedSentence;

public class Candidate implements Comparable<Candidate>{

	private final String entity;
	public int count;
	
	
	public Candidate(String entity, int count) {
		this.entity = entity;//.toLowerCase();
		this.count = count;
	}


	@Override
	public int compareTo(Candidate o) {
		return entity.compareTo(o.entity);
	}
	
	@Override
	public int hashCode(){
		return entity.hashCode();
	}
	
	public String getEntity(){
		return entity;
	}
	
	public String toString(){
		return "(" + entity + ":" + count + ")";
	}

}
