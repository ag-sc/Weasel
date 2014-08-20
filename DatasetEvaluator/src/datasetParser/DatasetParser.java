package datasetParser;

import java.io.IOException;
import java.util.LinkedList;

public abstract interface DatasetParser {
	
	public boolean goToNext() throws IOException;
	public String getSentence();
	public LinkedList<String> getEntities();
	
}
