package fileparser;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class AnchorFileParser extends FileParser{
	private BufferedReader br = null;
	private int lineCounter = 0;
	
	public AnchorFileParser(String filePath) throws FileNotFoundException, UnsupportedEncodingException{
		br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), "UTF8"));
	}
	
	public String[] parseTuple() throws IOException {
		String[] triplet = parseTriplet();
		String[] tuple = new String[2];
		tuple[0] = triplet[0];
		tuple[1] = triplet[1];
		return tuple;
	}

	public String[] parseTriplet() throws IOException {
		String line;
		String[] triplet;

		do {
			line = br.readLine();
			if (line == null)
				return null;

			triplet = line.split("\\t");
			if (triplet.length != 3) {
				continue;
			}

			String stringPattern = ".*?resource/(.+)";
			Pattern resourcePattern = Pattern.compile(stringPattern);
			Matcher matcher = resourcePattern.matcher(triplet[1]);
			if (matcher.find())
				triplet[1] = matcher.group(1);

			lineCounter++;
		} while (triplet.length != 3);

		return triplet;
	}
	
	public String[] getLine() {
		try{
			String line;
			String[] splitLine;
			
			line = br.readLine();
			if(line == null) return null;
				
			if(lineCounter % 10000 == 0) System.out.println("Processing line: " + lineCounter);
			splitLine = line.split("\\t");	
			lineCounter++;
			return splitLine;
			
		}catch(IOException e){
			e.printStackTrace();
		}
		return null;
	}
	
}
