import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import tfidf.DocumentFrequency;
import datatypes.Tuple;


public class BigMapCombiner {

	public BigMapCombiner() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) throws ClassNotFoundException, IOException {
		HashMap<Integer, Tuple<ArrayList<Integer>, HashMap<Integer, Float>>> bigMap = new HashMap<Integer, Tuple<ArrayList<Integer>, HashMap<Integer, Float>>>(9000000);

		for(int i = 1; i <= 36; i++){
			System.out.println("Working on bigmap_" + i);
			HashMap<Integer, Tuple<ArrayList<Integer>, HashMap<Integer, Float>>> map;
			FileInputStream fileInputStream = new FileInputStream("../../data/Wikipedia Abstracts/map/bigmap_" + i);
			ObjectInputStream objectReader = new ObjectInputStream(fileInputStream);
			map = (HashMap<Integer, Tuple<ArrayList<Integer>, HashMap<Integer, Float>>>) objectReader.readObject(); 
			objectReader.close();
			
			bigMap.putAll(map);
		}
		
		System.out.println("All maps loaded. Writing...");
		
		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("../../data/Wikipedia Abstracts/bigmap/bigmap"));
		out.writeObject(bigMap);
		out.close();
		
		System.out.println("All done.");
	}

}
