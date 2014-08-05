import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.LinkedList;


public class AnchorMapGenerator {

	public static HashMap<String, LinkedList<String>> generateURIKeyMap(String fileName) throws FileNotFoundException {
		
		AnchorFileReader anchorReader = new AnchorFileReader(fileName);
		HashMap<String, LinkedList<String>> URIKeyMap = new HashMap<String, LinkedList<String>>();
		String[] triplet;
		
		while((triplet = anchorReader.getTriplet()) != null){
			LinkedList<String> foundList;
			if((foundList = URIKeyMap.get(triplet[1])) == null){ // key not found, create new list entry
				LinkedList<String> newList = new LinkedList<String>();
				newList.add(triplet[0]);
				URIKeyMap.put(triplet[1], newList);
			}else{ // key found, add string to list
				//TODO: Do I have to make sure there are no double entries?
				foundList.add(triplet[0]);
			}
		}
		
		return URIKeyMap;
	}

	public static void saveURIKeyMapToFile(HashMap<String, LinkedList<String>> uriKeyMap, String fileName) throws IOException{
		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(fileName));
		out.writeObject(uriKeyMap);
		out.close();
	}

	@SuppressWarnings("unchecked")
	public static HashMap<String, LinkedList<String>> loadURIKeyMapFromFile(String fileName) throws IOException, ClassNotFoundException {
		FileInputStream fileInputStream = new FileInputStream(fileName);
		ObjectInputStream objectReader = new ObjectInputStream(fileInputStream);
		HashMap<String, LinkedList<String>> uriKeyMap = new HashMap<String, LinkedList<String>>();
		uriKeyMap = (HashMap<String, LinkedList<String>>) objectReader.readObject(); 
		objectReader.close();
		fileInputStream.close();
		return uriKeyMap;
	}
	
}
