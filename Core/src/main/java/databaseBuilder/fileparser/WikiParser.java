package main.java.databaseBuilder.fileparser;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import main.java.datatypes.StringEncoder;
import main.java.datatypes.configuration.Config;

public class WikiParser extends FileParser implements Closeable{
	
	protected BufferedReader br;
	protected String stringPattern1 = "<.*?resource/([^>]+)>";
	protected String stringPattern2 = "<.*?resource/([^>]+)>";
	protected Pattern resourcePattern1;
	protected Pattern resourcePattern2;
	protected Matcher matcher1;
	protected Matcher matcher2;
	private boolean useURLEncoding = false;
	
	public WikiParser(String filePath) throws IOException{
		br  = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), "UTF8"));
		setPatters(stringPattern1, stringPattern2);
		useURLEncoding = Boolean.parseBoolean(Config.getInstance().getParameter("useURLEncoding"));
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
			//if(splitLine.length != 4) return new String[1];
			
			matcher1 = resourcePattern1.matcher(splitLine[0]);
			matcher2 = resourcePattern2.matcher(splitLine[2]);
			
			if(matcher1.find()) tuple[0] = matcher1.group(1);
			else return new String[1];
			
			if(matcher2.find()) tuple[1] = matcher2.group(1);
			else{
				return new String[1];
			}
			
			if(useURLEncoding){
				tuple[0] = StringEncoder.encodeString(tuple[0]);
				tuple[1] = StringEncoder.encodeString(tuple[1]);
			}
			
		}else return null;
		
		return tuple;
	}

	@Override
	public void close() {
		try {
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

}
