package annotatedSentence;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class AnnotatedSentence {

	private List<Fragment> fragmentList;
	private Map<Integer, Integer> foundEntities;
	
	public AnnotatedSentence() {
		fragmentList = new ArrayList<Fragment>();
	} // AnnotatedSentence
	
	public void appendFragment(Fragment f){
		fragmentList.add(f);		
	}
	
	public List<Fragment> getFragmentList(){
		return fragmentList;
	}
	
	public Map<Integer, Integer> getFoundEntities() {
		if(foundEntities != null) return foundEntities;
		return new HashMap<Integer, Integer>();
	}

	public void setFoundEntities(Map<Integer, Integer> foundEntities) {
		this.foundEntities = foundEntities;
	}

	public void assign(double minimumScore) {
		// create fragment list
		
		Collections.sort(fragmentList);
		Collections.reverse(fragmentList);

		for (Fragment f : fragmentList) {
			if(f.probability < minimumScore) f.setEntity(null);
		}
	} // assign

	public int length(){
		return fragmentList.size();
	}
	
	public String getSentence(){
		StringBuilder sb = new StringBuilder();
		for(Fragment f: fragmentList){
			sb.append(f.originWord);
			sb.append(" ");
		}
		return sb.toString();
	}
	
}
