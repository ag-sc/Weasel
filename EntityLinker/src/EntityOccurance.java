
public class EntityOccurance {
	private String entityName;
	private int indexStart;
	private int indexEnd;
	
	public EntityOccurance(String entityName, int indexStart, int indexEnd){
		this.entityName = entityName;
		this.indexEnd = indexEnd;
		this.indexStart = indexStart;
	}
	
	public String getName(){
		return entityName;
	}
	
	public int getIndexStart() {
		return indexStart;
	}
	
	public int getIndexEnd(){
		return indexEnd;
	}
}
