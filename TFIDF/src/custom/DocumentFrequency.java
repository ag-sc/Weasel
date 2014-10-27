package custom;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.TreeSet;


public class DocumentFrequency implements Serializable{
	
	private static final long serialVersionUID = -9086930652499136727L;
	HashMap<String, Integer> wordID;
	HashMap<Integer, String> idToWord;
	HashMap<Integer, Integer> documentFrequency;

	public int numberOfDocuments = 0;
	private int idCounter = 0;
	private boolean reverseMapReady = false;
	
	public DocumentFrequency() {
		documentFrequency = new HashMap<Integer, Integer>();
		wordID = new HashMap<String, Integer>();
	}
	
	public void addDocument(String document){
		numberOfDocuments++;
		
		TreeSet<String> set = new TreeSet<String>();
		for(String s: document.toLowerCase().split(" ")) set.add(s);
		
		for(String word: set){
			Integer id = wordID.get(word);
			if(id != null){
				documentFrequency.put(id, documentFrequency.get(id) + 1);
			}else{
				wordID.put(word, idCounter);
				documentFrequency.put(idCounter, 1);
				idCounter++;
			}
		}
		reverseMapReady = false;
	}
	
	public void createReverseMap(){
		idToWord = new HashMap<Integer, String>();
		for(Entry<String, Integer> e: wordID.entrySet()){
			idToWord.put(e.getValue(), e.getKey());
		}
		reverseMapReady = true;
	}
	
	public Integer getFrequency(String word){
		Integer id = wordID.get(word);
		if(id != null){
			return documentFrequency.get(id);
		}else{
			return 0;
		}
	}
	
	public Integer getWordID(String word){
		return wordID.get(word);
	}
	
	public String getWordFromID(int id){
		if(!reverseMapReady) createReverseMap();
		return idToWord.get(id);
	}

}
