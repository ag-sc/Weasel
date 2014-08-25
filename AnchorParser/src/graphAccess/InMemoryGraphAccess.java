package graphAccess;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.TreeSet;

import datatypes.Edge;
import datatypes.TermFrequency;
import datatypes.TinyEdge;

public class InMemoryGraphAccess extends GraphAccess{

	private HashMap<String, TreeSet<TinyEdge>> map;
	
	public InMemoryGraphAccess(){
		map = new HashMap<String, TreeSet<TinyEdge>>();
	}
	
	@SuppressWarnings("unchecked")
	public void loadGraph(String fileName) throws IOException, ClassNotFoundException{
		FileInputStream fileInputStream = new FileInputStream(fileName);
		ObjectInputStream objectReader = new ObjectInputStream(fileInputStream);
		map = (HashMap<String, TreeSet<TinyEdge>>) objectReader.readObject(); 
		objectReader.close();
		fileInputStream.close();
	}
	
	@Override
	public void store(String subject, LinkedList<Edge<String, String>> currentList) {
		TreeSet<TinyEdge> tmp = new TreeSet<TinyEdge>();
		for(int i = 0; i < currentList.size(); i++){
			tmp.add(new TinyEdge(currentList.get(i).target));
		}
		map.put(subject, tmp);
	}
	
	public HashMap<String, TreeSet<TinyEdge>> getMap(){
		return map;
	}

	@Override
	public TreeSet<TinyEdge> query(String key) {
		return map.get(key);
	}

	@Override
	public Iterator<Entry<String, TreeSet<TinyEdge>>> getGraphIterator() {
		return map.entrySet().iterator();
	}
	
}
