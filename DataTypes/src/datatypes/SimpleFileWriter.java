package datatypes;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

public class SimpleFileWriter {
	PrintWriter writer;
	
	public SimpleFileWriter(String filepath) {
		try {
			writer = new PrintWriter(filepath, "UTF-8");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void close(){
		writer.flush();
		writer.close();
	}
	
	public void writeln(String line){
		writer.println(line);
	}

}
