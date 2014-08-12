package datatypes;

import java.io.Serializable;

public class TermFrequency implements Serializable, Comparable<TermFrequency>{
	private static final long serialVersionUID = 138026313864867362L;
	public String term = "";
	public int frequency = 0;
	
	public TermFrequency(String term, int frequency) {
		this.term = term;
		this.frequency = frequency;
	}
	
	@Override
	public String toString(){
		return "(" + term + " - " + frequency + ")";
	}

	@Override
	public int compareTo(TermFrequency tf) {
		return this.term.compareTo(tf.term);
	}
}
