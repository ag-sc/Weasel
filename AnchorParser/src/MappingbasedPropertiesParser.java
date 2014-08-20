import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import datatypes.Edge;
import jdbm.PrimaryHashMap;
import jdbm.RecordManager;
import jdbm.RecordManagerFactory;


public class MappingbasedPropertiesParser {
	
	public static void main(String[] args) {		
		try {
			//generateDB("../../data/Mappingbased Properties/mappingbased_properties_cleaned_en.nt","../../data/Mappingbased Properties/db/db_01");
			generateUriMapping("../../data/Mappingbased Properties/mappingbased_properties_cleaned_en.nt",
								"../../data/Mappingbased Properties/db/db_02");
//			calculateWeights("../../data/Mappingbased Properties/db/db_01");
//			RecordManager recman = RecordManagerFactory.createRecordManager("../../data/test");
//			PrimaryHashMap<Integer, LinkedList<Tuple<Integer, Integer>>> dbMap = recman.hashMap("tuples");
//			//PrimaryHashMap<Integer, Integer> dbMap = recman.hashMap("tuples");
//			long time = System.currentTimeMillis();
//			for(int i = 0; i < 15000000; i++){
//				LinkedList<Tuple<Integer, Integer>> list = new LinkedList<Tuple<Integer, Integer>>();
//				//int random = (int) (Math.random() * 30);
//				int random = 4;
//				if(i % 1000 == 0) random = (int) (Math.random() * 200);
//				for(int j = 0; j < random; j++){
//					list.add(new Tuple<Integer, Integer>((int) (Math.random() * 300), (int) (Math.random() * 300)));
//				}
//				dbMap.put(((int) (Math.random() * 3000000)), list);
//				
//				if(i % 100000 == 0) {
//					recman.commit();
//					System.out.println("lines: " + i + " ("+(System.currentTimeMillis()-time)/1000.0+"s since last)");
//					time = System.currentTimeMillis();
//				}
//			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void generateUriMapping(String source, String DBPath) throws IOException,FileNotFoundException{
		long time = System.currentTimeMillis();
		TreeSet<String> treeSet = new TreeSet<String>();
		
		BufferedReader br = new BufferedReader(new FileReader(source));
		String stringPattern = "<.*?resource/([^>]+)>";
		Pattern resourcePattern = Pattern.compile(stringPattern);
		
		String line, subject, latestKey = "";
		int id = 0, lineCounter = 0;
		while((line = br.readLine()) != null){
			if(line.contains("Romeo_and_Juliet")) System.out.println(line);
			lineCounter++;
			Matcher matcher1 = resourcePattern.matcher(line);
			if(matcher1.find()) subject = matcher1.group(1);
			else continue;
			if(latestKey.compareTo(subject) != 0){
				
				if(!treeSet.contains(subject)){
					treeSet.add(subject);
					
				}
				latestKey = subject;
			}
			
			if(lineCounter % 1000000 == 0) {
				System.out.println("lines: " + lineCounter + " ("+(System.currentTimeMillis()-time)/1000.0+"s since start)");
				//time = System.currentTimeMillis();
			}
		}
		
		System.out.println("TreeSet entries: " + treeSet.size());
		
//		RecordManager recman = RecordManagerFactory.createRecordManager(DBPath);
//		PrimaryHashMap<Integer, String> intToUri = recman.hashMap("intToUri");
//		PrimaryHashMap<String, Integer> uriToInt = recman.hashMap("UriToInt");
//		
//		time = System.currentTimeMillis();
//		int counter = 0;
//		for(String uri: treeSet){
//			intToUri.put(counter, uri);
//			uriToInt.put(uri, counter);
//			counter++;
//		}
//		recman.commit();
//		System.out.println("lines: " + counter + " ("+(System.currentTimeMillis()-time)/1000.0+"s since last)");
//		recman.close();
	}
	
	private static void calculateWeights(String DBPath) throws IOException,FileNotFoundException{
		RecordManager recman = RecordManagerFactory.createRecordManager(DBPath);
		PrimaryHashMap<Integer, LinkedList<Edge<Integer, Integer>>> dbMap = recman.hashMap("edges");
		
		int counter = 0;
		long time = System.currentTimeMillis();
		for(Entry<Integer, LinkedList<Edge<Integer, Integer>>> entry: dbMap.entrySet()){
			double totalTriangles = 0;
			
			for(Edge<Integer, Integer> edge: entry.getValue()){
				if (edge.target != null) {
					double weight = 1;
					System.out.println((int) edge.target);
					for (Edge<Integer, Integer> otherEdge : dbMap
							.get((int) edge.target)) {
						for (Edge<Integer, Integer> firstEdge : entry
								.getValue()) {
							if (otherEdge.target == firstEdge.target) {
								totalTriangles += 1;
								weight += 1;
							}
						}
					}
					edge.weight = weight;
				}
			}
			
			for(Edge<Integer, Integer> edge: entry.getValue()){
				if(totalTriangles > 0) edge.weight /= totalTriangles;
				System.out.println(edge.target + " - weight: " + edge.weight);
			}
			
			counter++;
			if(counter % 100000 == 0){
				//recman.commit();
				System.out.println("lines: " + counter + " ("+(System.currentTimeMillis()-time)/1000.0+"s since last)");
				time = System.currentTimeMillis();
			}
		}
		
		recman.close();
	}
	
	public static void generateDB(String source, String DBPath) throws IOException,FileNotFoundException{
		//generateUriMapping(source, DBPath);
		System.out.println("Done with Uri-ID mapping.");
		long time = System.currentTimeMillis();
		
		RecordManager recman = RecordManagerFactory.createRecordManager(DBPath);
		PrimaryHashMap<Integer, LinkedList<Edge<Integer, Integer>>> dbMap = recman.hashMap("edges");
		PrimaryHashMap<Integer, String> intToUri = recman.hashMap("intToUri");
		PrimaryHashMap<String, Integer> uriToInt = recman.hashMap("UriToInt");

		String line, subject, predicate, object;
		String latestKey = "";
		LinkedList<Edge<Integer, Integer>> currentList = new LinkedList<Edge<Integer, Integer>>();
		BufferedReader br = new BufferedReader(new FileReader(source));
		
		String stringPattern = "<.*?resource/([^>]+)>";
		Pattern resourcePattern = Pattern.compile(stringPattern);
		stringPattern = "<.*?ontology/([^>]+)>";
		Pattern predicatePattern = Pattern.compile(stringPattern);
		
		int linecounter = 0;
		int keyCounter = 0;
		double avgLength = 0;
		double avgListLength = 0;
		int longestList = 0;
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
				if(linecounter != 0) dbMap.put(uriToInt.get(latestKey), currentList);
				latestKey = subject;
				
				keyCounter++;
				avgLength += latestKey.length();
				avgListLength += currentList.size();
				
				//if(currentList.size() > longestList) longestList = currentList.size();
				if(currentList.size() > 10) longestList++;
				currentList = new LinkedList<Edge<Integer, Integer>>();
				currentList.add(new Edge<Integer, Integer>(0, uriToInt.get(object)));
			}else{
				currentList.add(new Edge<Integer, Integer>(0, uriToInt.get(object)));
			}
			
			//dbMap.put(subject, );
			
			if(linecounter % 100000 == 0) {
				recman.commit();
				System.out.println("lines: " + linecounter + " ("+(System.currentTimeMillis()-time)/1000.0+"s since last) - longestList: " +longestList);
				time = System.currentTimeMillis();
				longestList = 0;
			}
			if(linecounter % 8000000 == 0 && linecounter != 0) {
				System.out.println("defrag...");
				recman.defrag();
			}
			linecounter++;
			//System.out.println(subject + " " + predicate + " " + object);
		}
		dbMap.put(uriToInt.get(latestKey), currentList);
		recman.close();
		System.out.println("nr of keys: " + keyCounter + " avg lengt: " + (avgLength/(double)keyCounter)
				+ " avg list lengt: " + (avgListLength/(double)keyCounter));
		br.close();
	}

}
