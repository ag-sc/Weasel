import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Set;



public class KMeansClustering {

	List<Vector> Vectors;
	
	Vector[] Means;
	
	HashMap<Integer,Integer> Vector2Cluster;
	HashMap<Integer,Set<Integer>> Cluster2Vector;
	
	int K;
	
	KMeansClustering(int k)
	{
		k=K;
		Vectors = new ArrayList<Vector>();
		Vector2Cluster = new HashMap<Integer,Integer>();
	}
	
	public void addVector(Vector vector)
	{
		Vectors.add(vector);
	}
	
	public void cluster(int max)
	{
		Means = initializeMeans();
		
		int iter = 0;
		
		Boolean assignment_changed = true;

		int closestMeans;
		
		Integer vectorID;
		
		while (assignment_changed && iter < max)	
		{
			assignment_changed = false;
			
			vectorID = 0;
			
			for (Vector vector: Vectors)
			{				
				closestMeans = getClosestMeans(vector);
				
				if (closestMeans != Vector2Cluster.get(vectorID)) assignment_changed = true;
				
				Vector2Cluster.put(vectorID, new Integer(closestMeans));
				Cluster2Vector.get(closestMeans).add(vectorID);
				
				vectorID++;
			}
			
			recomputeMeans();
			
			iter++;
			
		}
		
		
	}
	
	private void recomputeMeans() {
		
		Vector mean;
		
		Set<Integer> vectors;
				
		Vector vec;
		
		for (Integer cluster: Cluster2Vector.keySet())
		{
			vectors = Cluster2Vector.get(cluster);
			
			double norm = 1.0 / ((double) vectors.size()); 
			
			mean = new Vector();
			
			for (Integer vector: vectors)
			{
				
				vec = Vectors.get(vector.intValue()).clone();
				vec.scale(norm);
				
				mean.add(vec);
			}
		}
		
	}

	private int getClosestMeans(Vector vector) {
		
		int closestMean = 0;
		int minDistance = Integer.MAX_VALUE;
		
		int m = 0;
		
		for (Vector mean: Means)
		{
			
			if (mean.computeEuclideanDistance(vector) < minDistance)
			{
				closestMean = m;
			}
			
			m++;
		}
		
		return closestMean;
	}

	private Vector[] initializeMeans() {
		
		Vector[] vectors = new Vector[K];
		
		for (int i=0; i < K; i++)
		{
			vectors[i] = Vectors.get(getRandomInteger(0,Vectors.size()-1)); 
		}
		
		return vectors;
	}

	public Vector[] getMeans()
	{
		return Means;
	}
	
	public int getRandomInteger(int min, int max) {

	 
	    Random rand = new Random();

	    int randomNum = rand.nextInt((max - min) + 1) + min;

	    return randomNum;
	}
	
	
}
