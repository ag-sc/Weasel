import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;

import datatypes.TermFrequency;


public class AnchorMapGenerator {

	public static HashMap<String, LinkedList<TermFrequency>> generateURIKeyMap(String fileName) throws FileNotFoundException {
		
		AnchorFileReader anchorReader = new AnchorFileReader(fileName);
		HashMap<String, LinkedList<TermFrequency>> URIKeyMap = new HashMap<String, LinkedList<TermFrequency>>();
		String[] triplet;
		
		while((triplet = anchorReader.getTriplet()) != null){
			LinkedList<TermFrequency> foundList;
			TermFrequency termFrequency = new TermFrequency(triplet[0].toLowerCase(), Integer.parseInt(triplet[2]));
			
			if((foundList = URIKeyMap.get(triplet[1].toLowerCase())) == null){ // key not found, create new list entry
				LinkedList<TermFrequency> newList = new LinkedList<TermFrequency>();
				newList.add(termFrequency);
				URIKeyMap.put(triplet[1].toLowerCase(), newList);
			}else{ // key found, add string to list
				//TODO: Do I have to make sure there are no double entries?
				foundList.add(termFrequency);
			}
		}
		
		return URIKeyMap;
	}

	public static void saveURIKeyMapToFile(HashMap<String, LinkedList<TermFrequency>> uriKeyMap, String fileName) throws IOException{
		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(fileName));
		out.writeObject(uriKeyMap);
		out.close();
	}
	
	public static void saveMapToTextFile (HashMap<String, LinkedList<TermFrequency>> uriKeyMap, String fileName) throws IOException{
		 PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(fileName)));
		 
		 for (Entry<String, LinkedList<TermFrequency>> entry : uriKeyMap.entrySet()) {
			    String key = entry.getKey();
			    LinkedList<TermFrequency> value = entry.getValue();

			    out.print(key);
			    for(TermFrequency tf: value){
			    	out.print("\t" + tf.term + "\t" + tf.frequency);
			    }
			    out.println();
			    
			}
		 
		 out.close();
	}
	
	public static void savePartialKeyMapToTextFile (HashMap<String, LinkedList<String>> partialKeyMap, String fileName) throws IOException{
		 PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(fileName)));
		 
		 for (Entry<String, LinkedList<String>> entry : partialKeyMap.entrySet()) {
			    String key = entry.getKey();
			    LinkedList<String> value = entry.getValue();

			    out.print(key);
			    for(String tf: value){
			    	out.print("\t" + tf);
			    }
			    out.println();
			    
			}
		 
		 out.close();
	}

	@SuppressWarnings("unchecked")
	public static HashMap<String, LinkedList<TermFrequency>> loadURIKeyMapFromFile(String fileName) throws IOException, ClassNotFoundException {
		FileInputStream fileInputStream = new FileInputStream(fileName);
		ObjectInputStream objectReader = new ObjectInputStream(fileInputStream);
		HashMap<String, LinkedList<TermFrequency>> uriKeyMap = new HashMap<String, LinkedList<TermFrequency>>();
		uriKeyMap = (HashMap<String, LinkedList<TermFrequency>>) objectReader.readObject(); 
		objectReader.close();
		fileInputStream.close();
		return uriKeyMap;
	}

	public static HashMap<String, LinkedList<TermFrequency>> generateAnchorKeyMapFromURIKeyMapTextFile(String fileName) throws FileNotFoundException{
		AnchorFileReader anchorReader = new AnchorFileReader(fileName);
		HashMap<String, LinkedList<TermFrequency>> AnchorKeyMap = new HashMap<String, LinkedList<TermFrequency>>();
		String[] splitLine;

		while((splitLine = anchorReader.getLine()) != null){
			LinkedList<TermFrequency> foundList;
			
			for(int i = 1; i < splitLine.length; i += 2){
				TermFrequency termFrequency = new TermFrequency(splitLine[0], Integer.parseInt(splitLine[i+1]));
				
				if((foundList = AnchorKeyMap.get(splitLine[i])) == null){ // key not found, create new list entry
					LinkedList<TermFrequency> newList = new LinkedList<TermFrequency>();
					newList.add(termFrequency);
					AnchorKeyMap.put(splitLine[i], newList);
				}else{ // key found, add string to list
					//TODO: Do I have to make sure there are no double entries?
					foundList.add(termFrequency);
				}
			}
		}
		
		return AnchorKeyMap;
	}

	public static HashMap<String, LinkedList<String>> generatePartialAnchorKeyMapFromAnchorKeyMapTextFile(String fileName) throws FileNotFoundException {
		AnchorFileReader anchorReader = new AnchorFileReader(fileName);
		HashMap<String, LinkedList<String>> partialAnchorKeyMap = new HashMap<String, LinkedList<String>>();
		String[] splitLine;
		int counter = 0;

		while((splitLine = anchorReader.getLine()) != null){
			if(counter % 100000 == 0) System.out.println("counter: " + counter);
			
			String[] partialAnchors = splitLine[0].split(" ");
			
			if (partialAnchors.length > 1) {
				for (String part: partialAnchors) {
					// TODO: Some better heuristic as to what to ignore
					if (part.length() > 1) {
						LinkedList<String> foundList;
						if ((foundList = partialAnchorKeyMap.get(part)) == null) { // key not found, create new list entry
							LinkedList<String> newList = new LinkedList<String>();
							newList.add(splitLine[0]);
							partialAnchorKeyMap.put(part, newList);
						} else { // key found, add string to list
							// TODO: removing doubles costs too much time
							//if(!foundList.contains(splitLine[0])) foundList.add(splitLine[0]);
							foundList.add(splitLine[0]);
						}
					}
				}
			}
			counter++;
		}
		
		return partialAnchorKeyMap;
	}
	
}
