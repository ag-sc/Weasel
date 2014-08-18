package datasetParser;

public abstract interface DatasetParser {
	
	public boolean next();
	public String getSentence();
	public String[] getEntities();
	
}
