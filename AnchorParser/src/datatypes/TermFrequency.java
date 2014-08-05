package datatypes;

import java.io.Serializable;

public class TermFrequency implements Serializable{
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
}
