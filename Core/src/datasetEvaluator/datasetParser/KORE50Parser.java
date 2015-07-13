package datasetEvaluator.datasetParser;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nif.NIF_SchemaGen;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

import datatypes.StringEncoder;
import datatypes.annotatedSentence.AnnotatedSentence;
import datatypes.annotatedSentence.Fragment;
import datatypes.configuration.Config;
import datatypes.databaseConnectors.DatabaseConnector;
import entityLinker.InputStringHandler;

public class KORE50Parser extends DatasetParser {

	private BufferedReader br = null;
	private String line;
	private boolean readFullDocument = false;
	private boolean useURLEncoding = false;
	static String lastLine = "";

	// public KORE50Parser() throws IOException {
	// this(false);
	// //br = new BufferedReader(new InputStreamReader(new FileInputStream(br),
	// "UTF8"));
	// }

	public KORE50Parser() {
		setBufferedReader();
		Config config = Config.getInstance();
		this.readFullDocument = Boolean.parseBoolean(config.getParameter("readFullDocument"));
		this.useURLEncoding = Boolean.parseBoolean(Config.getInstance().getParameter("useURLEncoding"));
	}
	
	private void setBufferedReader(){
		Config config = Config.getInstance();
		try {
			this.br = new BufferedReader(new InputStreamReader(new FileInputStream(config.getParameter("datasetPath")), "UTF8"));
			line = br.readLine(); // read first line to start with first
									// sentence when parse() is called
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void close() {
		if (br != null)
			try {
				br.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}

	private String decodeString(String string){
		String tmp;
		String unicodeString = "\\\\u(\\w\\w\\w\\w)";
		Pattern unicodePattern = Pattern.compile(unicodeString);
		Matcher matcher = unicodePattern.matcher(string);
		while (matcher.find()) {
			tmp = matcher.group(1);
			// System.out.println("found unicode: " + tmp);
			// System.out.println("translates to: " +
			// (char)Integer.parseInt(tmp, 16));
			String tmp3 = "\\\\u" + tmp;
			String tmp4 = Character.toString((char) Integer.parseInt(tmp, 16));
			string = string.replaceAll(tmp3, tmp4);
		}
		return string;
	}
	
	@Override
	public AnnotatedSentence parse() throws IOException {
		AnnotatedSentence annotatedSentence = new AnnotatedSentence();

		while ((line = br.readLine()) != null) {
			String tmpLine = line.toLowerCase();
			
			if (tmpLine.contains("-docstart-")) {
				if (readFullDocument)
					return annotatedSentence;
				else {
					continue;
				}

			} else if (line.equals(".")) {
				if (readFullDocument) {
					continue;
				}

				else
					return annotatedSentence;
			} else if (line.equals(",") || line.equals("\n")) {
				continue;
			} else {
//				String tmp;
//				String unicodeString = "\\\\u(\\w\\w\\w\\w)";
//				Pattern unicodePattern = Pattern.compile(unicodeString);
//				Matcher matcher = unicodePattern.matcher(line);
//				while (matcher.find()) {
//					tmp = matcher.group(1);
//					// System.out.println("found unicode: " + tmp);
//					// System.out.println("translates to: " +
//					// (char)Integer.parseInt(tmp, 16));
//					String tmp3 = "\\\\u" + tmp;
//					String tmp4 = Character.toString((char) Integer.parseInt(tmp, 16));
//					line = line.replaceAll(tmp3, tmp4);
//				}
				line = decodeString(line);
				
				
				String[] splitLine = line.split("\\t");
				// encode to be compatible with database
				if(useURLEncoding){
					splitLine[0] = StringEncoder.encodeString(splitLine[0].toLowerCase());
					if (splitLine.length >= 4) {
						splitLine[2] = StringEncoder.encodeString(splitLine[2].toLowerCase());
						splitLine[3] = StringEncoder.encodeString(splitLine[3]);
					}
				}
				
				if (splitLine.length >= 4) {
					if(splitLine[1].equals("I")){
						continue;
					}
					if (splitLine[3].equalsIgnoreCase("--NME--")) {
						annotatedSentence.appendFragment(new Fragment(splitLine[2]));
					}else{
						Fragment f = new Fragment(splitLine[2]);
						f.setOriginEntity(splitLine[3]);
						annotatedSentence.appendFragment(f);
					}
				}else{
					annotatedSentence.appendFragment(new Fragment(splitLine[0]));
				}

			}
			
		}

		return annotatedSentence;
	}
	
	@Override
	public void parseIntoStringHandler(InputStringHandler handler) throws IOException {
		Model model = handler.getModel();
		StringBuilder sb = new StringBuilder();
		while ((line = br.readLine()) != null) {
			if (line.contains("-DOCSTART-")) {
				handler.handleString(sb.toString().trim());
				sb = new StringBuilder();
			}else{
				line = decodeString(line);
				String[] splitLine = line.split("\\t");
				String word = "";
				
				// If entity is mentioned, add to model
				if(splitLine.length > 1){
					if(splitLine[1].equals("I")){
						continue;
					}
					
					if(useURLEncoding){
//						splitLine[0] = StringEncoder.encodeString(splitLine[0]);
						if (splitLine.length >= 4) {
//							splitLine[2] = StringEncoder.encodeString(splitLine[2]);
							splitLine[3] = StringEncoder.encodeString(splitLine[3]);
						}
					}
					
					sb.append(" ");
					int offset = sb.toString().length() - 1;
					Resource tmp = model.createResource("Sentence_" + handler.getStringCounter() + "#char=" + offset + "," + (offset +  splitLine[2].length()))
							.addProperty(Config.datasetEntityProp, splitLine[3]);
					word = splitLine[2];
				}else{
					word = splitLine[0];
					if(!word.matches("\\p{Punct}+.*") && !word.isEmpty()){
						sb.append(" ");
					}
				}
				
				sb.append(word);
			}
		}
		
		// Refine result
		String sentence = sb.toString();
		sentence = sentence.trim();
		//sentence = sentence.replaceAll("\\s+", " ");
		
		handler.handleString(sentence);
	}

	@Override
	public HashSet<Integer> getEntitiesInDocument(DatabaseConnector entityDBconnector) {
		HashSet<Integer> set = new HashSet<Integer>();

		try {
			AnnotatedSentence parserSentence = new AnnotatedSentence();
			while ((parserSentence = parse()).length() > 0) {
				for (Fragment f : parserSentence.getFragmentList()) {
					if (f.getEntity() != null) {
						Integer id = entityDBconnector.resolveName(f.getEntity());
						if (id != null) {
							set.add(id);
						}
					}
				}
			}
		} catch (IOException e) {

		}
		setBufferedReader();
		return set;
	}

}
