package datasetParser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KORE50Parser implements DatasetParser{

	private String currentSentence = null;
	private LinkedList<String> currentEntities = null;
	private BufferedReader br = null;
	private String line;
	
	public KORE50Parser(File file) throws IOException {
		//br = new BufferedReader(new FileReader(file));
		br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF8"));
	}

	public boolean goToNext() throws IOException {
		while((line = br.readLine()) != null){
			if(line.split(" ")[0].equals("-DOCSTART-")){
				String tmpSentence = "";
				LinkedList<String> tmpEntities= new LinkedList<String>();
				
				while((line = br.readLine()) != null){
					if(line.equals(".")){
						currentSentence = tmpSentence.trim() + ".";
						currentEntities = tmpEntities;
						return true;
					}else{						
						String tmp;
						String unicodeString = "\\\\u(\\w\\w\\w\\w)";
						Pattern unicodePattern = Pattern.compile(unicodeString);
						Matcher matcher = unicodePattern.matcher(line);
						while(matcher.find()){
							tmp = matcher.group(1);
//							System.out.println("found unicode: " + tmp);
//							System.out.println("translates to: " + (char)Integer.parseInt(tmp, 16));
							String tmp3 = "\\\\u" + tmp;
							String tmp4 = Character.toString((char)Integer.parseInt(tmp, 16));
							line = line.replaceAll(tmp3, tmp4);
						}
						
						String[] splitLine = line.split("\\t");
						if(splitLine[0].equals(",")){
							tmpSentence = tmpSentence.trim() + splitLine[0] + " ";
						}else{
							tmpSentence += splitLine[0] + " ";
						}
						
						if(splitLine.length == 4 && !splitLine[3].equals("--NME--")){
							tmpEntities.add(splitLine[3]);
							if(splitLine[3].contains("Eva")) System.out.println(splitLine[3]);
						}
					}
				}
			}
		}
		return false;
	}

	public String getSentence() {
		return currentSentence;
	}

	public LinkedList<String> getEntities() {
		return currentEntities;
	}

}
