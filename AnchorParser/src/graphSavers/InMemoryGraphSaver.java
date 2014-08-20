package graphSavers;

import java.util.HashMap;
import java.util.LinkedList;

import datatypes.Edge;

public class InMemoryGraphSaver extends GraphSaver{

	private HashMap<String, String[]> map;
	
	public InMemoryGraphSaver(){
		map = new HashMap<String, String[]>();
	}
	
	@Override
	public void store(String subject, LinkedList<Edge<String, String>> currentList) {
		String stringArray[] = new String[currentList.size()];
		for(int i = 0; i < currentList.size(); i++){
			stringArray[i] = currentList.get(i).target;
		}
		map.put(subject, stringArray);
	}
	
	public HashMap<String, String[]> getMap(){
		return map;
	}
	
}
