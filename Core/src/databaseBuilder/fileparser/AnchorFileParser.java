package databaseBuilder.fileparser;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import datatypes.StringEncoder;
import datatypes.configuration.Config;


public class AnchorFileParser extends FileParser{
	private BufferedReader br = null;
	private boolean useURLEncoding = false;
	private HashMap<String, Integer> totalReferencesPerEntity = new HashMap<String, Integer>();
	
	public AnchorFileParser(String filePath) throws FileNotFoundException, UnsupportedEncodingException{
		useURLEncoding = Boolean.parseBoolean(Config.getInstance().getParameter("useURLEncoding"));
		// create totalReferencesPerEntity map
		br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), "UTF8"));
		String line;
		String[] triplet;
		try {
			while((line = br.readLine()) != null){
				triplet = getLineTriplet(line);
				if(triplet == null) continue;
				String entity = triplet[1];
				Integer count = Integer.parseInt(triplet[2]);
				if(totalReferencesPerEntity.containsKey(entity)){
					totalReferencesPerEntity.put(entity, totalReferencesPerEntity.get(entity) + count);
				}else{
					totalReferencesPerEntity.put(entity, count);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(-1);
		}
		
		br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), "UTF8"));
	}
	
	public String[] parseTuple() throws IOException {
		String[] triplet = parse();
		if(triplet == null) return null;
		String[] tuple = new String[2];
		tuple[0] = triplet[0];
		tuple[1] = triplet[1];
		return tuple;
	}
	
	private String[] getLineTriplet(String line){
		if(line == null) return null;
		String triplet[];
		triplet = line.split("\\t");
		if (triplet.length != 3) {
			return null;
		}

		String stringPattern = ".*?resource/(.+)";
		Pattern resourcePattern = Pattern.compile(stringPattern);
		Matcher matcher = resourcePattern.matcher(triplet[1]);
		if (matcher.find())
			triplet[1] = matcher.group(1);

		if(useURLEncoding){
			triplet[0] = StringEncoder.encodeString(triplet[0]); // anchor
			triplet[1] = StringEncoder.encodeString(triplet[1]); // entity
		}
		return triplet;
	}

	public String[] parse() throws IOException {
		String line;
		String[] triplet;
		String[] quadruple = new String[4];

		do {
			line = br.readLine();
			if(line == null) return null;
			triplet = getLineTriplet(line);		
		} while (triplet == null);

		quadruple[0] = triplet[0];
		quadruple[1] = triplet[1];
		quadruple[2] = triplet[2];
		quadruple[3] = totalReferencesPerEntity.get(triplet[1]).toString();
		
		return quadruple;
	}
	
//	public String[] getLine() {
//		try{
//			String line;
//			String[] splitLine;
//			
//			line = br.readLine();
//			if(line == null) return null;
//				
//			if(lineCounter % 10000 == 0) System.out.println("Processing line: " + lineCounter);
//			splitLine = line.split("\\t");	
//			lineCounter++;
//			return splitLine;
//			
//		}catch(IOException e){
//			e.printStackTrace();
//		}
//		return null;
//	}



	@Override
	public void close() {
		try {
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
