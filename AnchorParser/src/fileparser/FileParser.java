package fileparser;

import java.io.IOException;

public abstract class FileParser {
	
	public abstract String[] parseTuple() throws IOException;
	public abstract void close();
}
