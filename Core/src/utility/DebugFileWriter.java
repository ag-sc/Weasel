package utility;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;


public class DebugFileWriter {

	public static void write(String string){
		write(string, "debugFile.txt");
	}
	
	public static void write(String string, String filePath){
		try {
			BufferedWriter fw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath), "UTF8"));
			fw.write(string);
			fw.close();
		} catch (Exception e) {
			System.err.println("Exception in DebugFileWriter.write operation: ");
			e.printStackTrace();
		}
	}
}
