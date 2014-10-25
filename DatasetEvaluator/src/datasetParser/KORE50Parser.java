package datasetParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import datatypes.AnnotatedSentenceDeprecated;

public class KORE50Parser implements DatasetParser{

	private BufferedReader br = null;
	private String line;
	private boolean readFullDocument = false;
	
	public KORE50Parser(BufferedReader br) throws IOException {
		this(br, false);
		//br = new BufferedReader(new InputStreamReader(new FileInputStream(br), "UTF8"));
	}
	
	public KORE50Parser(BufferedReader br, boolean readFullDocument) throws IOException {
		this.br = br;
		this.readFullDocument = readFullDocument;
		line = br.readLine(); // read first line to start with first sentence when parse() is called
	}
	
	public String parseString() throws NumberFormatException, IOException{
		String sentence = "";
		
		while ((line = br.readLine()) != null) {
			if(line.split(" ")[0].equals("-DOCSTART-")){
				if(readFullDocument) return sentence;
				else continue;
			}else if (line.equals(".")) {
				if(readFullDocument) continue;
				else return sentence;
			}else if(line.equals(",") || line.equals("\n")){
				continue;
			}else {
				String tmp;
				String unicodeString = "\\\\u(\\w\\w\\w\\w)";
				Pattern unicodePattern = Pattern.compile(unicodeString);
				Matcher matcher = unicodePattern.matcher(line);
				while (matcher.find()) {
					tmp = matcher.group(1);
					// System.out.println("found unicode: " + tmp);
					// System.out.println("translates to: " +
					// (char)Integer.parseInt(tmp, 16));
					String tmp3 = "\\\\u" + tmp;
					String tmp4 = Character.toString((char) Integer.parseInt(tmp, 16));
					line = line.replaceAll(tmp3, tmp4);
				}

				String[] splitLine = line.split("\\t");
				for (String s : splitLine[0].split(" ")) {
					sentence += s + " ";
				}
				
			}
		}
		
		return sentence;
	}

	@Override
	public AnnotatedSentenceDeprecated parse() throws IOException {
		AnnotatedSentenceDeprecated annotatedSentence = new AnnotatedSentenceDeprecated();

		while ((line = br.readLine()) != null) {
			if(line.split(" ")[0].equals("-DOCSTART-")){
				if(readFullDocument) return annotatedSentence;
				else continue;
			}else if (line.equals(".")) {
				if(readFullDocument) continue;
				else return annotatedSentence;
			}else if(line.equals(",") || line.equals("\n")){
				continue;
			}else {
				String tmp;
				String unicodeString = "\\\\u(\\w\\w\\w\\w)";
				Pattern unicodePattern = Pattern.compile(unicodeString);
				Matcher matcher = unicodePattern.matcher(line);
				while (matcher.find()) {
					tmp = matcher.group(1);
					// System.out.println("found unicode: " + tmp);
					// System.out.println("translates to: " +
					// (char)Integer.parseInt(tmp, 16));
					String tmp3 = "\\\\u" + tmp;
					String tmp4 = Character.toString((char) Integer.parseInt(tmp, 16));
					line = line.replaceAll(tmp3, tmp4);
				}

				String[] splitLine = line.split("\\t");
				for (String s : splitLine[0].split(" ")) {
					
					int index = annotatedSentence.addToken(s);
					if (splitLine.length >= 4 && !splitLine[3].equals("--NME--")) {
						//int index = annotatedSentence.addToken(s);
						annotatedSentence.setEntity(index, splitLine[3]);
					}
				}
				
			}
		}

		return annotatedSentence;
	}

}
