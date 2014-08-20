import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class AnchorFileReader {
	private static final Logger logger = Logger.getLogger(AnchorFileReader.class.getName());
	private static ConsoleHandler consoleHandler = new ConsoleHandler();
	private BufferedReader br = null;
	private int lineCounter = 0;
	
	AnchorFileReader(String fileName) throws FileNotFoundException{
		//consoleHandler.setLevel(Level.ALL);
		//logger.addHandler(consoleHandler);
		//logger.setLevel(Level.ALL);
		
		br = new BufferedReader(new FileReader(fileName));
	}

	public String[] getTriplet() {
		try{
			String line;
			String[] triplet;
			
			do{
				line = br.readLine();
				if(line == null) return null;
				
				if(lineCounter % 10000 == 0) logger.log(Level.FINER, "Processing line: " + lineCounter);
				
				triplet = line.split("\\t");
				if(triplet.length != 3){
					logger.log(Level.FINEST, "Faulty line: " + lineCounter);
					continue;
				}
				
				String stringPattern = ".*?resource/(.+)";
				Pattern resourcePattern = Pattern.compile(stringPattern);
				Matcher matcher = resourcePattern.matcher(triplet[1]);
				if(matcher.find()) triplet[1] = matcher.group(1);
				
				lineCounter++;
			}while(triplet.length != 3);
			
			return triplet;
			
		}catch(IOException e){
			logger.log(Level.SEVERE, "ReadLine IOException: \n" + e.getMessage());
			return null;
		}
	}
	
	public String[] getLine() {
		try{
			String line;
			String[] splitLine;
			
			line = br.readLine();
			if(line == null) return null;
				
			if(lineCounter % 10000 == 0) logger.log(Level.FINER, "Processing line: " + lineCounter);
			splitLine = line.split("\\t");	
			lineCounter++;
			return splitLine;
			
		}catch(IOException e){
			logger.log(Level.SEVERE, "ReadLine IOException: \n" + e.getMessage());
			return null;
		}
	}
	
}
