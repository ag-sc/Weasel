package tfidf;

import java.util.HashMap;

public class TermFrequency {

	HashMap<String,Integer> tfMap; 
	
	public TermFrequency() {
		tfMap = new HashMap<String, Integer>();
	}
	
	public void addTerm(String term){
		if (tfMap.containsKey(term)){
			tfMap.put(term, new Integer(tfMap.get(term).intValue()+1));
		}else{
			tfMap.put(term, new Integer(1));
		}
	}
	
	public int getFrequency(String term){
		Integer frequency = tfMap.get(term);
		if(frequency != null){
			return frequency;
		}else{
			return 0;
		}
	}

}
