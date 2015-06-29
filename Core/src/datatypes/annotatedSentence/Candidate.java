package datatypes.annotatedSentence;

public class Candidate implements Comparable<Candidate>{

	private final String entity;
	public int count;
	private int totalNumberOfReferencesToEntity;
	private double referenceProbability = 0.0;
	
	public Candidate(String entity, int count, int totalNumberOfReferencesToEntity) {
		this.entity = entity;//.toLowerCase();
		this.count = count;
		this.totalNumberOfReferencesToEntity = totalNumberOfReferencesToEntity;
		if(this.totalNumberOfReferencesToEntity != 0){
			referenceProbability = (double) this.count / (double) this.totalNumberOfReferencesToEntity;
		}
	}


	@Override
	public int compareTo(Candidate o) {
		return entity.compareTo(o.entity);
	}
	
	@Override
	public int hashCode(){
		return entity.hashCode();
	}
	
	public double getReferenceProbability(){
		return referenceProbability;
	}
	
	public String getEntity(){
		return entity;
	}
	
	public String toString(){
		return "(" + entity + ":" + count + ")";
	}

}
