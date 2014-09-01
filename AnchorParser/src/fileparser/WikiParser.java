package fileparser;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WikiParser extends FileParser implements Closeable{
	
	private BufferedReader br;
	private String stringPattern1 = "<.*?resource/([^>]+)>";
	private String stringPattern2 = "<.*?resource/([^>]+)>";
	private Pattern resourcePattern1;
	private Pattern resourcePattern2;
	private Matcher matcher1;
	private Matcher matcher2;
	
	public WikiParser(String filePath) throws IOException{
		br = new BufferedReader(new FileReader(filePath));
		setPatters(stringPattern1, stringPattern2);
	}
	
	public void setPatters(String pattern1, String pattern2){
		stringPattern1 = new String(pattern1);
		stringPattern2 = new String(pattern2);
		resourcePattern1 = Pattern.compile(stringPattern1);
		resourcePattern2 = Pattern.compile(stringPattern2);
	}
	
	@Override
	public String[] parseTuple() throws IOException {
		String tuple[] = new String[2];
		String line;
		
		if((line = br.readLine()) != null){
			String[] splitLine = line.split(" ");
			if(splitLine.length != 4) return new String[1];
			
			matcher1 = resourcePattern1.matcher(splitLine[0]);
			matcher2 = resourcePattern2.matcher(splitLine[2]);
			
			if(matcher1.find()) tuple[0] = matcher1.group(1);
			else return new String[1];
			
			if(matcher2.find()) tuple[1] = matcher2.group(1);
			else{
				return new String[1];
			}
			
		}else return null;
		
		return tuple;
	}

	@Override
	public void close() throws IOException {
		br.close();
	}
	

}
