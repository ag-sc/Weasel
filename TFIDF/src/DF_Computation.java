import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;


public class DF_Computation implements Serializable{

	private static final long serialVersionUID = -982745857460278060L;

	HashMap<String,Integer> df;
	
	public int no_documents = 0;
	
	
	public DF_Computation()
	{
		df = new HashMap<String,Integer>();
	}
	
	public void addDocument(TreeSet<String> content)
	{
		//String[] tokens = content.split(" ");
		
		for (String token: content)
		{
			update(token,1);
		}
		
		no_documents++;
		
	}
	
	public Integer getDF(String token)
	{
		
		if (df.containsKey(token))
		{
			return df.get(token); 
		}
		
		return 0;
	}
	
	public double getIDF(String token)
	{
		if (df.containsKey(token))
		{
			return Math.log( ((double) no_documents) / df.get(token).doubleValue());
		}
		else
		{
			return 0.0;
		}

	}
	
	
	
	public void update(String token, int value)
	{
		if (df.containsKey(token))
		{
			df.put(token, new Integer(df.get(token).intValue()+value));
		}
		else
		{
			df.put(token, new Integer(value));
		}
	}
	
	
	
}
