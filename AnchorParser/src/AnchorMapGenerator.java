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
import java.util.TreeSet;

import jdbm.PrimaryHashMap;
import jdbm.RecordManager;
import jdbm.RecordManagerFactory;
import datatypes.TermFrequency;


public class AnchorMapGenerator {
	
	public static HashMap<String, LinkedList<TermFrequency>> generateURIKeyMap(String fileName) throws FileNotFoundException {
		
		AnchorFileReader anchorReader = new AnchorFileReader(fileName);
		HashMap<String, LinkedList<TermFrequency>> URIKeyMap = new HashMap<String, LinkedList<TermFrequency>>();
		String[] triplet;
		
		while((triplet = anchorReader.getTriplet()) != null){
			//for(String s: triplet) System.out.println(s);
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
	
	public static <T> void saveMapToJDMB(HashMap<String, LinkedList<T>> map, String dbName, String mapName) throws IOException{
		System.out.println("Writing map to DB '"+dbName+"' as map '"+mapName+"'");
		RecordManager recman = RecordManagerFactory.createRecordManager(dbName);
		//recman.defrag();
		PrimaryHashMap<String, LinkedList<T>> dbMap = recman.hashMap(mapName);
		
		
		int counter = 0;
		for(Entry<String, LinkedList<T>> entry: map.entrySet()){
			//System.out.println("attempt to put: " + entry.getKey());
			//System.out.println("  " + entry.getValue());
			dbMap.put(entry.getKey(), entry.getValue());
			if(counter++ % 100000 == 0){
				System.out.println(counter-1 + " of " + map.entrySet().size());
				recman.commit();
			}
		}
		recman.commit();		
		recman.close();
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
			
			//if (splitLine[0].length() > 1) {
				for (int i = 1; i < splitLine.length; i += 2) {
					TermFrequency termFrequency = new TermFrequency(
							splitLine[0], Integer.parseInt(splitLine[i + 1]));

					if ((foundList = AnchorKeyMap.get(splitLine[i])) == null) { // key not found, create new list entry
						LinkedList<TermFrequency> newList = new LinkedList<TermFrequency>();
						newList.add(termFrequency);
						AnchorKeyMap.put(splitLine[i], newList);
					} else { // key found, add string to list
								//TODO: Do I have to make sure there are no double entries?
						foundList.add(termFrequency);
					}
				}
			//}
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

	public static void generateDatabase(AnchorFileReader anchorReader, String dbPath) throws FileNotFoundException, IOException {
		/// URI table
		String[] triplet;
		int counter = 0;
		
		RecordManager recman = RecordManagerFactory.createRecordManager(dbPath);
		PrimaryHashMap<String, TreeSet<TermFrequency>> uriMap = recman.hashMap("uri");
		
		while((triplet = anchorReader.getTriplet()) != null){
			TreeSet<TermFrequency> foundTreeSet;
			TermFrequency termFrequency = new TermFrequency(triplet[0].toLowerCase(), Integer.parseInt(triplet[2]));
			
			if((foundTreeSet = uriMap.get(triplet[1].toLowerCase())) == null){ // key not found, create new list entry
				TreeSet<TermFrequency> newTreeSet = new TreeSet<TermFrequency>();
				newTreeSet.add(termFrequency);
				uriMap.put(triplet[1].toLowerCase(), newTreeSet);
			}else{ // key found, add string to list
				foundTreeSet.add(termFrequency);
				uriMap.put(triplet[1].toLowerCase(), foundTreeSet);
			}
			
			if(counter++ % 100000 == 0){
				System.out.println("Processed: " + (counter - 1));
				recman.commit();
			}
		}
		recman.commit();
		System.out.println("Finished URI table...");
		/*
		/// Anchor table
		PrimaryHashMap<String, TreeSet<TermFrequency>> anchorMap = recman.hashMap("anchor");
		counter = 0;
		
		for(Entry<String, TreeSet<TermFrequency>> entry: uriMap.entrySet()){
			for(TermFrequency termFrequency: entry.getValue()){
				TreeSet<TermFrequency> foundTreeSet;
				TermFrequency newTermFrequency = new TermFrequency(entry.getKey(), termFrequency.frequency);
				
				if((foundTreeSet = anchorMap.get(termFrequency.term)) == null){ // key not found, create new list entry
					TreeSet<TermFrequency> newTreeSet = new TreeSet<TermFrequency>();
					newTreeSet.add(newTermFrequency);
					anchorMap.put(termFrequency.term, newTreeSet);
				}else{ // key found, add string to list
					foundTreeSet.add(newTermFrequency);
					anchorMap.put(termFrequency.term, foundTreeSet);
				}
				
				if(counter++ % 100000 == 0){
					System.out.println("Processed: " + (counter - 1));
					recman.commit();
				}
			}
		}
		recman.commit();
		System.out.println("Finished anchor table...");
		
		/// Partial anchor table
		PrimaryHashMap<String, TreeSet<String>> partialAnchorMap = recman.hashMap("partial");
		counter = 0;
		
		for(Entry<String, TreeSet<TermFrequency>> entry: anchorMap.entrySet()){
			String[] anchorParts = entry.getKey().split(" ");
			if(anchorParts.length > 1){
				for (String part: anchorParts) {
					if (part.length() > 1) {
						TreeSet<String> foundTreeSet;
						if ((foundTreeSet = partialAnchorMap.get(part)) == null) { // key not found, create new list entry
							TreeSet<String> newTreeSet = new TreeSet<String>();
							newTreeSet.add(entry.getKey());
							partialAnchorMap.put(part, newTreeSet);
						} else { // key found, add string to list
							foundTreeSet.add(entry.getKey());
							partialAnchorMap.put(part, foundTreeSet);
						}
						if (counter++ % 100000 == 0) {
							System.out.println("Processed: " + (counter - 1));
							recman.commit();
						}
					}
				}
			}
		}
		recman.commit();
		System.out.println("Finished partial anchor table...");
		*/
		recman.close();
	}
	
}
