package entityLinker.evaluation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import datatypes.StringEncoder;
import datatypes.annotatedSentence.AnnotatedSentence;
import datatypes.annotatedSentence.Fragment;
import datatypes.configuration.Config;

public class DBpediaSpotlightEvaluation extends EvaluationEngine {

//	static String stem = "http://spotlight.dbpedia.org/rest/annotate?text=";
	static String stem = "http://spotlight.dbpedia.org/rest/disambiguate?text=";
	static String tail = "&confidence=0.0&support=0";
	
	protected String stringPattern1 = "<a.*?resource/(.*?)\".*?>(.*?)</a>";
	protected Pattern resourcePattern1;
	protected Matcher matcher1;
	
	boolean useURLEncoding = false;
	BufferedWriter bw;
	int counter = 0;
	
	public DBpediaSpotlightEvaluation(){
		resourcePattern1 = Pattern.compile(stringPattern1);
		try {
			bw = new BufferedWriter(new FileWriter("workingSentences.txt"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.useURLEncoding = Boolean.parseBoolean(Config.getInstance().getParameter("useURLEncoding"));
	}
	
	@Override
	public void evaluate(AnnotatedSentence annotatedSentence) {	
		// generate lookup string
		StringBuilder sb = new StringBuilder();
		sb.append("<annotation text=\"");
		for(Fragment fragment: annotatedSentence.getFragmentList()){
			//if(!fragment.getOriginEntity().isEmpty()) 
			if(!fragment.originWord.equals("\"")) sb.append(fragment.originWord + " ");
		}
		sb.append("\">\n");
		int accumulatedLength = 0;
		for(Fragment fragment: annotatedSentence.getFragmentList()){
			if(!fragment.getOriginEntity().isEmpty()) {
				sb.append("  <surfaceForm name=\"" + fragment.originWord + "\" offset=\"" + accumulatedLength + "\"/>\n");
				accumulatedLength += fragment.originWord.length() + 1;
			}else if(!fragment.originWord.equals("\"")){
				accumulatedLength += fragment.originWord.length() + 1;
			}
			
		}
		sb.append("</annotation>");
		
		
		
		Map<String, String> entityMap = new HashMap<String, String>();
		try{ 
			//System.out.println(URLEncoder.encode(sb.toString(), "UTF-8"));
			String encodedSentence = URLEncoder.encode(sb.toString(), "UTF-8");
//			String encodedSentence = URLEncoder.encode(annotatedSentence.getSentence(), "UTF-8");
			String urlString = stem + encodedSentence + tail;
			URL url = new URL(urlString);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		    connection.setRequestMethod("GET");
		    BufferedReader  rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		    
		    String line;
		    while ((line = rd.readLine()) != null && !line.equals("<div>"));
	        line = rd.readLine();
	        
	        //System.out.println(line);
	        
	        
	        matcher1 = resourcePattern1.matcher(line);
	        while(matcher1.find()){
	        	String originWord = matcher1.group(2);
	        	String entity = matcher1.group(1);
	        	if(useURLEncoding){
	        		originWord = StringEncoder.encodeString(originWord);
	        		entity = StringEncoder.encodeString(entity);
	        	}
	        	entityMap.put(originWord, entity);
//	        	System.out.println(matcher1.group(2) + " -> " +matcher1.group(1));
	        }
		}catch (IOException e){
			e.printStackTrace();
			try {
				bw.write(counter + "\n");
				bw.flush();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
//			System.exit(-1);
		}
		
		for(Fragment fragment: annotatedSentence.getFragmentList()){
			fragment.setEntity(entityMap.get(fragment.originWord));
		}
	counter++;
	}

}
