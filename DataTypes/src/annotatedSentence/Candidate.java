package annotatedSentence;

public class Candidate implements Comparable<Candidate>{

	private final String word;
	public int count;
	
	
	public Candidate(String word, int count) {
		this.word = word.toLowerCase();
		this.count = count;
	}


	@Override
	public int compareTo(Candidate o) {
		return word.compareTo(o.word);
	}
	
	@Override
	public int hashCode(){
		return word.hashCode();
	}
	
	public String getWord(){
		return word;
	}
	
	public String toString(){
		return "(" + word + ":" + count + ")";
	}

}
