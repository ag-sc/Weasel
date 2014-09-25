package stopwatch;

public class Stopwatch {

	public static enum UNIT{
		MILLISECONDS(1000000.0),
		SECONDS(1000000000.0),
		MINUTES(60000000000.0),
		HOURS(3600000000000.0);
		
		private double value;
		
		UNIT(double value){
			this.value = value;
		}
		
		public double getValue(){
			return value;
		}
	}
	
	long start, stop;
	double divisor;
	String time; 
	
	public Stopwatch(){
		this(UNIT.MILLISECONDS);
	}
	
	public Stopwatch(UNIT u) {
		start = System.nanoTime();
		stop = start;
		divisor = u.getValue();
	}
	
	public void start(){
		start = System.nanoTime();
	}
	
	public String stop(){
		stop = System.nanoTime();
		Double passedTime = (stop - start) / divisor;
		time = passedTime.toString();
		return time;
	}
	
	public String toString(){
		return time;
	}

}
