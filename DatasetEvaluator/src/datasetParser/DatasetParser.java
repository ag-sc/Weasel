package datasetParser;

import java.io.IOException;
import datatypes.AnnotatedSentenceDeprecated;

public abstract interface DatasetParser {
	
	public abstract AnnotatedSentenceDeprecated parse() throws IOException;
	
}
