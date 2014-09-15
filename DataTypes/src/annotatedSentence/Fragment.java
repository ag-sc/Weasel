package annotatedSentence;

public class Fragment implements Comparable<Fragment>{

	public double probability = 0.0;
	int start;
	int stop;
	String value;
	
	public Fragment(int start, int stop, String value, double probability) {
		this.start = start;
		this.stop = stop;
		this.value = value;
		this.probability = probability;
	}
	
	public Fragment(int start, int stop, String value) {
		this(start, stop, value, 0.0);
	}

	@Override
	public int compareTo(Fragment o) {
		if(probability < o.probability) return -1;
		else if (probability > o.probability) return 1;
		else return 0;
	}
	
	public String getValue(){
		return value;
	}

}
