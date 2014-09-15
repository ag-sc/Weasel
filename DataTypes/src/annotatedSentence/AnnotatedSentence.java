package annotatedSentence;

import java.util.Collections;
import java.util.LinkedList;

public class AnnotatedSentence {

	private LinkedList<Word> wordList;
	private LinkedList<Fragment> fragmentList;
	
	public AnnotatedSentence(String sentence[]) {
		wordList = new LinkedList<Word>();
		for(String word: sentence){
			getWordList().add(new Word(word));
		}
		fragmentList = new LinkedList<Fragment>();
	} // AnnotatedSentence

	public void addFragment(Fragment f) {
		getFragmentList().add(f);
	} // addFragment

	public void assign() {
		Collections.sort(getFragmentList());
		Collections.reverse(getFragmentList());

		for (Fragment f : getFragmentList()) {
			boolean dominated = false;
			for (int i = f.start; i <= f.stop; i++) {
				if (getWordList().get(i).getDominantFragment() != null) {
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

	public LinkedList<Fragment> getFragmentList() {
		return fragmentList;
	}

	
}
