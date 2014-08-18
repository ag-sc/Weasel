package datasetParser;

import java.io.File;

public class KORE50Parser implements DatasetParser{

	private String currentSentence = null;
	private String[] currentEntities = null;
	
	public KORE50Parser(File file) {
		// TODO Auto-generated constructor stub
	}

	public boolean next() {
		// TODO Auto-generated method stub
		return false;
	}

	public String getSentence() {
		return currentSentence;
	}

	public String[] getEntities() {
		return currentEntities;
	}

}
