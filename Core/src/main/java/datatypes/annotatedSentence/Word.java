package main.java.datatypes.annotatedSentence;

public class Word {

	private String value;
	private Fragment dominantFragment;
	
	public Word(String value) {
		this.value = value;
		dominantFragment = null;
	}

	public Fragment getDominantFragment() {
		return dominantFragment;
	}

	public void setDominantFragment(Fragment dominantFragment) {
		this.dominantFragment = dominantFragment;
	}

	public String getValue() {
		return value;
	}	
	
	public String toString() {
		if (dominantFragment != null && dominantFragment.entity != null)
			return value + "\t" + dominantFragment.entity;
		else
			return value;
	}
	
}
