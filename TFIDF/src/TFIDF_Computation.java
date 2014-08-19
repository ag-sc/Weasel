import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;


public class TFIDF_Computation {

	DF_Computation df_computation;
	
	FileWriter Writer;
	
	HashMap<String,Integer> tf; 
	
	
	public TFIDF_Computation(FileWriter writer) throws IOException
	{
		Writer = writer;
		
		writer.write("# docID:	token tf df idf tf*idf = tf * log(no_docs / df) ");
	}
	
	public void addDocument(String id, String document) throws IOException
	{
		tf = new HashMap<String,Integer>();
		
		String[] tokens = document.split(" ");
		
		String token;
		
		for (int i=0; i < tokens.length; i++)
		{
			token = tokens[i];
			token = token.toLowerCase();
			token = token.replaceAll("\\.", "");
			token = token.replaceAll(",", "");
			token = token.replaceAll(":", "");
			token = token.replaceAll("!", "");
			token = token.replaceAll("\\?", "");
			
			
			update(token,1);
		}
		
		Writer.write(id+":");
		
		for (String entry: tf.keySet())
		{
			Writer.write("\t"+entry+" "+tf.get(entry)+" "+df_computation.getDF(entry)+" "+df_computation.getIDF(entry)+" "+(tf.get(entry) * df_computation.getIDF(entry)));
		}
		
		Writer.write("\n");
		
		Writer.flush();
		
		
		
	}
	
	public void SetDF_Computation(DF_Computation df_computation)
	{
		this.df_computation = df_computation;
	}
	
	
	public void update(String token, int value)
	{
		if (tf.containsKey(token))
		{
			tf.put(token, new Integer(tf.get(token).intValue()+value));
		}
		else
		{
			tf.put(token, new Integer(value));
		}
	}
	
}
