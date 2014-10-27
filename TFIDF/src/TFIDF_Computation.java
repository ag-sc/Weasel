import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;

import datatypes.TFIDFResult;


public class TFIDF_Computation {

	DF_Computation df_computation;
	
	FileWriter Writer;
	
	HashMap<String,Integer> tf; 
	
	
	public TFIDF_Computation(FileWriter writer) throws IOException
	{
		Writer = writer;
		
		writer.write("# docID:\ttoken\ttf\tdf\tidf\ttf*idf = tf * log(no_docs / df) \n");
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
		
		
		LinkedList<TFIDFResult> result = new LinkedList<TFIDFResult>();
		for(String tmpToken: tf.keySet()){
			Integer tempTF = tf.get(tmpToken);
			Integer tempDF = df_computation.getDF(tmpToken);
			if(tempTF == null || tempDF == null || tempTF == 0 || tempDF == 0) continue;
			result.add(new TFIDFResult(tmpToken, df_computation.no_documents, tempTF, tempDF));
		}
		Collections.sort(result);
		Collections.reverse(result);
		
		Writer.write(id+":");
		
		for (TFIDFResult entry: result)
		{
			Writer.write(entry.toString() + "\n");
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
