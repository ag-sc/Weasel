package main.java.databaseBuilder.fileparser;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import main.java.datatypes.StringEncoder;
import main.java.datatypes.configuration.Config;


/**
 * @author Felix Tristram
 * AnchorFileParser parses the file containing the wikipedia anchors, which are pairs of a
 * text token and a Wikipedia URL.
 */
public class AnchorFileParser extends FileParser{
	private BufferedReader br = null;
	private boolean useURLEncoding = false;
	private HashMap<String, Integer> totalReferencesPerEntity = new HashMap<String, Integer>();
	
	/**
	 * @param filePath Path to the Wikipedia Anchor file.
	 * @throws FileNotFoundException
	 * @throws UnsupportedEncodingException
	 */
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
			triplet[0] = StringEncoder.encodeString(triplet[0].toLowerCase()); // anchor
			triplet[1] = StringEncoder.encodeString(triplet[1]); // entity
		}
		return triplet;
	}

	/**
	 * Parse the next line of the anchors file.
	 * @return A String array size 4 with the following content
	 * Position 0: The anchor token.
	 * Position 1: The Wikipedia URL.
	 * Position 2: Number of occurrences of this pair in Wikipedia.
	 * Position 3: Total nuber of anchor references to the given URL.
	 * @throws IOException
	 */
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
