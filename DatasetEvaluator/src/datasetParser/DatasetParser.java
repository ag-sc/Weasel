package datasetParser;

import java.io.IOException;
import java.util.LinkedList;

import datatypes.AnnotatedSentence;

public abstract interface DatasetParser {
	
	public abstract AnnotatedSentence parse() throws IOException;
	
}
