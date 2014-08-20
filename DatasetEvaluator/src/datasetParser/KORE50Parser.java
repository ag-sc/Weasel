package datasetParser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;

public class KORE50Parser implements DatasetParser{

	private String currentSentence = null;
	private LinkedList<String> currentEntities = null;
	private BufferedReader br = null;
	private String line;
	
	public KORE50Parser(File file) throws IOException {
		br = new BufferedReader(new FileReader(file));
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
						String[] splitLine = line.split("\\t");
						if(splitLine[0].equals(",")){
							tmpSentence = tmpSentence.trim() + splitLine[0] + " ";
						}else{
							tmpSentence += splitLine[0] + " ";
						}
						
						if(splitLine.length == 4 && !splitLine[3].equals("--NME--")){
							tmpEntities.add(splitLine[3]);
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
