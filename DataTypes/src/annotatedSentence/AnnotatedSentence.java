package annotatedSentence;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;

public class AnnotatedSentence {

	private LinkedList<Word> wordList;
	private HashMap<Integer, HashMap<Integer, Fragment>> fragmentMap;
	private LinkedList<Fragment> fragmentList;
	
	public AnnotatedSentence(String sentence[]) {
		wordList = new LinkedList<Word>();
		for (String word : sentence) {
			getWordList().add(new Word(word));
		}
		fragmentMap = new HashMap<Integer, HashMap<Integer, Fragment>>();
		for (int i = 0; i < sentence.length; i++)
			fragmentMap.put(i, new HashMap<Integer, Fragment>());
	} // AnnotatedSentence

	public void addFragment(Fragment f) {
		HashMap<Integer, Fragment> tmp = fragmentMap.get(f.start);
		Fragment retrievedFragment = tmp.get(f.stop);
		if(retrievedFragment != null){
			retrievedFragment.addCandidats(f.candidates);
		}else{
			tmp.put(f.stop, f);
		}
	} // addFragment
	
	public HashMap<Integer, HashMap<Integer, Fragment>> getFragmentMap(){
		return fragmentMap;
	} // getFragmentMap

	public LinkedList<Fragment> buildFragmentList(){
		fragmentList = new LinkedList<Fragment>();
		for(HashMap<Integer, Fragment> submap: fragmentMap.values()){
			for(Fragment f: submap.values()){
				fragmentList.add(f);
			}
		}
		return fragmentList;
	}
	
	public void assign(double minimumScore) {
		// create fragment list
		fragmentList = buildFragmentList();
		
		Collections.sort(fragmentList);
		Collections.reverse(fragmentList);

		for (Fragment f : fragmentList) {
			if(f.probability < minimumScore) break;
			boolean dominated = false;
			for (int i = f.start; i <= f.stop; i++) {
				if (getWordList().get(i).getDominantFragment() != null
						&& getWordList().get(i).getDominantFragment().length() >= f.length()) {
					dominated = true;
					break;
				}
			}
			if (dominated)
				continue;
			else {
				for (int i = f.start; i <= f.stop; i++) {
					getWordList().get(i).setDominantFragment(f);
				}
			}
		}
	} // assign

	public LinkedList<Word> getWordList() {
		return wordList;
	}

//	public LinkedList<Fragment> getFragmentList() {
//		return fragmentList;
//	}

	
}
