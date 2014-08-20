import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;

import graphSavers.InMemoryGraphSaver;


public class DBSandbox {

	public static void main(String[] args) {
		
		InMemoryGraphSaver graphSaver = new InMemoryGraphSaver();
		MPFileParser parser = new MPFileParser(graphSaver);
		HashMap<String, String[]> map;
		
		try {
			parser.parse("../../data/Mappingbased Properties/mappingbased_properties_cleaned_en.nt");
			map = graphSaver.getMap();
			System.out.println(map.entrySet().size());
			
//			for(Entry<String, String[]> e: map.entrySet()){
//				System.out.println(e.getKey() + ":");
//				for(String s: e.getValue()){
//					System.out.println("	" + s);
//				}
//			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println("Done!");
	}

}
