package datasetParser;

import java.io.IOException;
import java.util.LinkedList;

import datatypes.AnnotatedSentenceDeprecated;

public abstract interface DatasetParser {
	
	public abstract AnnotatedSentenceDeprecated parse() throws IOException;
	
}
