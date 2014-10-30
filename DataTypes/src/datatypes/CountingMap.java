package datatypes;

import java.util.HashMap;

public class CountingMap {
	
	public HashMap<String, Integer> map;
	
	public CountingMap() {
		map = new HashMap<String, Integer>();
	}
	
	public void increase(String key){
		Integer i = map.get(key);
		if(i == null) map.put(key, 1);
		else map.put(key, i + 1);
	}
	
	public int get(String key){
		Integer i = map.get(key);
		if(i == null) return 0;
		else return i;
	}

}
