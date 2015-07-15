package datatypes;

public class EntityOccurance {
	private String entityName;
	private String fragment;
	private int indexStart;
	private int indexEnd;
	
	public EntityOccurance(String fragment, int indexStart, int indexEnd){
		this.fragment = fragment;
		this.indexEnd = indexEnd;
		this.indexStart = indexStart;
	}
	
	public EntityOccurance(EntityOccurance eo){
		this.fragment = new String(eo.getFragment());
		this.indexEnd = eo.getIndexEnd();
		this.indexStart = eo.getIndexStart();;
	}
	
	public void setName(String entityName){
		this.entityName = entityName;
	}
	
	public String getName(){
		return entityName;
	}
	
	public String getFragment(){
		return fragment;
	}
	
	public int getIndexStart() {
		return indexStart;
	}
	
	public int getIndexEnd(){
		return indexEnd;
	}
	
	public String toString(){
		return "Fragment '" + fragment +"' occurs character " + indexStart + " to " + indexEnd + ". Candidate Meaning: " + entityName;
	}
}
