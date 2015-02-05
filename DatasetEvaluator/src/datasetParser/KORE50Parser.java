package datasetParser;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import annotatedSentence.AnnotatedSentence;
import annotatedSentence.Fragment;
import configuration.Config;
import databaseConnectors.DatabaseConnector;
import datatypes.AnnotatedSentenceDeprecated;

public class KORE50Parser extends DatasetParser {

	private BufferedReader br = null;
	private String line;
	private boolean readFullDocument = false;
	private boolean allLowerCase = false;

	// public KORE50Parser() throws IOException {
	// this(false);
	// //br = new BufferedReader(new InputStreamReader(new FileInputStream(br),
	// "UTF8"));
	// }

	public KORE50Parser() {
		setBufferedReader();
		Config config = Config.getInstance();
		this.readFullDocument = Boolean.parseBoolean(config.getParameter("readFullDocument"));
		this.allLowerCase = Boolean.parseBoolean(config.getParameter("treatAllAsLowerCase"));
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

//	public String parseString() throws NumberFormatException, IOException {
//		String sentence = "";
//
//		while ((line = br.readLine()) != null) {
//			if (line.split(" ")[0].equals("-DOCSTART-")) {
//				if (readFullDocument)
//					return sentence;
//				else
//					continue;
//			} else if (line.equals(".")) {
//				if (readFullDocument)
//					continue;
//				else
//					return sentence;
//			} else if (line.equals(",") || line.equals("\n")) {
//				continue;
//			} else {
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
//
//				String[] splitLine = line.split("\\t");
//				for (String s : splitLine[0].split(" ")) {
//					sentence += s + " ";
//				}
//
//			}
//		}
//
//		return sentence;
//	}

	@Override
	public AnnotatedSentence parse() throws IOException {
		AnnotatedSentence annotatedSentence = new AnnotatedSentence();

		while ((line = br.readLine()) != null) {
			if(allLowerCase) line = line.toLowerCase();
			if (line.split(" ")[0].equals("-DOCSTART-")) {
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
				// encode to be compatible with database
				splitLine[0] = URLEncoder.encode(splitLine[0], "UTF-8");
				
				if (splitLine.length >= 4) {
					if(splitLine[1].equals("I")){
						continue;
					}
					splitLine[2] = URLEncoder.encode(splitLine[2], "UTF-8");
					if (splitLine[3].equals("--NME--")) {
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
