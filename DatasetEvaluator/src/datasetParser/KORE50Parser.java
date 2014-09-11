package datasetParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import datatypes.AnnotatedSentence;

public class KORE50Parser implements DatasetParser{

	private BufferedReader br = null;
	private String line;
	
	public KORE50Parser(BufferedReader br) throws IOException {
		this.br = br;
		//br = new BufferedReader(new InputStreamReader(new FileInputStream(br), "UTF8"));
	}

	@Override
	public AnnotatedSentence parse() throws IOException {
		AnnotatedSentence annotatedSentence = new AnnotatedSentence();
		
		while((line = br.readLine()) != null){
			if(line.split(" ")[0].equals("-DOCSTART-")){
				break;
			}
		}

		while ((line = br.readLine()) != null) {
			if (line.equals(".")) {
				return annotatedSentence;
			}else if(line.equals(",")){
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

					if (splitLine.length == 4 && !splitLine[3].equals("--NME--")) {
						annotatedSentence.setEntity(index, splitLine[3]);
					}
				}
				
			}
		}

		
		return annotatedSentence;
	}

}
