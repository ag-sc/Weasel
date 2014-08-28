import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import datatypes.Edge;
import graphAccess.GraphAccess;


public class MPFileParser {
	GraphAccess graphSaver;
	
	public MPFileParser(GraphAccess graphSaver){
		this.graphSaver = graphSaver;
	}
	
	public void parse(String MPFilePath) throws IOException, FileNotFoundException{
		BufferedReader br = new BufferedReader(new FileReader(MPFilePath));
		String line, subject, predicate, object, latestKey = "";
		int linecounter = 0;
		LinkedList<Edge<String, String>> currentList = new LinkedList<Edge<String, String>>();
		
		String stringPattern = "<.*?resource/([^>]+)>";
		Pattern resourcePattern = Pattern.compile(stringPattern);
		stringPattern = "<.*?ontology/([^>]+)>";
		Pattern predicatePattern = Pattern.compile(stringPattern);
		
		long time = System.currentTimeMillis();
		while((line = br.readLine()) != null){
			Matcher matcher1 = resourcePattern.matcher(line);
			Matcher matcher2 = predicatePattern.matcher(line);
			
			if(matcher1.find()) subject = matcher1.group(1);
			else continue;
			
			if(matcher2.find()) predicate = matcher2.group(1);
			else continue;
			
			if(matcher1.find()) object = matcher1.group(1);
			else continue;
			
			if(latestKey.compareTo(subject) != 0){
				if(linecounter != 0){
					graphSaver.store(latestKey, currentList);
				}
				latestKey = subject;
				
				currentList = new LinkedList<Edge<String, String>>();
				currentList.add(new Edge<String, String>(predicate, object));
			}else{
				currentList.add(new Edge<String, String>(predicate, object));
			}
			linecounter++;
			if(linecounter % 100000 == 0) {
				System.out.println("lines: " + linecounter + " ("+(System.currentTimeMillis()-time)/1000.0+"s since last)");
				time = System.currentTimeMillis();
			}
		}
		graphSaver.store(latestKey, currentList);
		
		br.close();
	}
}
