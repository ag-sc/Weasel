package main.java.databaseBuilder.fileparser;

import java.io.Closeable;
import java.io.IOException;

/**
 * @author Felix Tristram
 * To allow for different parsers on the same reference they all inherit from FileParser.
 * It is designed for input files which only contain tuples.
 */
public abstract class FileParser implements Closeable{
	
	/**
	 * Return the next String tuple in the file.
	 * @return The next String tuple.
	 * @throws IOException
	 */
	public abstract String[] parseTuple() throws IOException;
	@Override
	public abstract void close();
}
