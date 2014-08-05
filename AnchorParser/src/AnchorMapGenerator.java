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
import java.util.Iterator;
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
			TermFrequency termFrequency = new TermFrequency(triplet[0], Integer.parseInt(triplet[2]));
			
			if((foundList = URIKeyMap.get(triplet[1])) == null){ // key not found, create new list entry
				LinkedList<TermFrequency> newList = new LinkedList<TermFrequency>();
				newList.add(termFrequency);
				URIKeyMap.put(triplet[1], newList);
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
	
}
