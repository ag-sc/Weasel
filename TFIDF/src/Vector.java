import java.util.HashMap;


public class Vector {

	HashMap<String,Double> Vec;
	
	public Vector()
	{
		Vec = new HashMap<String,Double>();
	}
	
	public void addValue(String dimension, Double value)
	{
		Vec.put(dimension, value );
	}
	
	public Double getValue(String dimension)
	{
		if (Vec.containsKey(dimension))
		{
			return Vec.get(dimension);
		}
		else
			return 0.0;
	}
	
	
	public double computeEuclideanDistance(Vector vec)
	{
		Vector vector = this.clone();
		
		Vector vector2 = vec.clone();
		vector2.scale(-1.0);
		
		vector.add(vector2);
		
		return vector.norm() * vector.norm();
	}

	private double norm () {
	
		double value = 0.0;
		
		for (String dimension: Vec.keySet())
		{
			value += Vec.get(dimension) * Vec.get(dimension);
		}
		
		return Math.sqrt(value);
	}

	public void add(Vector vector) {
		
		for (String dimension: Vec.keySet())
		{
			Vec.put(dimension, Vec.get(dimension)+ vector.getValue(dimension));
		}
		
	}

	public void scale(double factor) {
			
		for (String dimension: Vec.keySet())
		{
			Vec.put(dimension, new Double(Vec.get(dimension).doubleValue() * factor));
		}
	}
	
	
	public Vector clone()
	{
		Vector vector = new Vector();
		
		for (String dimension: Vec.keySet())
		{
			vector.addValue(dimension, Vec.get(dimension));
		}
		
		return vector;
	}
	
}
