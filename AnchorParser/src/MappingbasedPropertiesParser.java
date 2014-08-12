import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import datatypes.Tuple;
import jdbm.PrimaryHashMap;
import jdbm.RecordManager;
import jdbm.RecordManagerFactory;


public class MappingbasedPropertiesParser {

	public static void main(String[] args) {
		int j = 0;
		for(int i = 0; i < 251; i++){
			j += i;
		}
		System.out.println(j);
		
		try {
			generateDB("../../data/Mappingbased Properties/mappingbased_properties_cleaned_en.nt",
					   "../../data/Mappingbased Properties/db_01");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void generateDB(String source, String DBPath) throws IOException,FileNotFoundException{
		long time = System.currentTimeMillis();
		
		RecordManager recman = RecordManagerFactory.createRecordManager(DBPath);
		PrimaryHashMap<String, LinkedList<Tuple<String, String>>> dbMap = recman.hashMap("tuples");
		
		String line, subject, predicate, object;
		String latestKey = "";
		LinkedList<Tuple<String, String>> currentList = new LinkedList<Tuple<String, String>>();
		BufferedReader br = new BufferedReader(new FileReader(source));
		
		String stringPattern = "<.*?resource/([^>]+)>";
		Pattern resourcePattern = Pattern.compile(stringPattern);
		stringPattern = "<.*?ontology/([^>]+)>";
		Pattern predicatePattern = Pattern.compile(stringPattern);
		
		int linecounter = 0;
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
				if(linecounter != 0) dbMap.put(latestKey, currentList);
				latestKey = subject;
				currentList = new LinkedList<Tuple<String, String>>();
				currentList.add(new Tuple<String, String>(predicate, object));
			}else{
				currentList.add(new Tuple<String, String>(predicate, object));
			}
			
			//dbMap.put(subject, );
			
			if(linecounter % 100000 == 0) {
				recman.commit();
				System.out.println("lines: " + linecounter + " ("+(System.currentTimeMillis()-time)/1000.0+"s since last)");
				time = System.currentTimeMillis();
			}
			if(linecounter % 1000000 == 0) {
				System.out.println("defrag...");
				recman.defrag();
			}
			linecounter++;
			//System.out.println(subject + " " + predicate + " " + object);
		}
		dbMap.put(latestKey, currentList);
		recman.close();
		br.close();
	}

}
