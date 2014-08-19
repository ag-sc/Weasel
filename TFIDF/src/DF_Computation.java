import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;


public class DF_Computation {

	HashMap<String,Integer> df;
	
	int no_documents = 0;
	
	
	public DF_Computation()
	{
		df = new HashMap<String,Integer>();
	}
	
	public void addDocument(String content)
	{
		String[] tokens = content.split(" ");
				
		Set<String> tokenSet = new HashSet<String>();
		
		
		for (int i=0; i < tokens.length; i++)
		{
			String token;
			
			token = tokens[i];
			token = token.toLowerCase();
			token = token.replaceAll("\\.", "");
			token = token.replaceAll(",", "");
			token = token.replaceAll(":", "");
			token = token.replaceAll("!", "");
			token = token.replaceAll("\\?", "");
			
			tokenSet.add(token);
		}
		
		for (String token: tokenSet)
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
