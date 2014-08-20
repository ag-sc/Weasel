package graphAccess;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.TreeSet;

import datatypes.Edge;
import datatypes.TinyEdge;

public class InMemoryGraphAccess extends GraphAccess{

	private HashMap<String, TreeSet<TinyEdge>> map;
	
	public InMemoryGraphAccess(){
		map = new HashMap<String, TreeSet<TinyEdge>>();
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
