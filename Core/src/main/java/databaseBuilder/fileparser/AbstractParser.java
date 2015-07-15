package databaseBuilder.fileparser;

import java.io.IOException;

public class AbstractParser extends WikiParser {

	public AbstractParser(String filePath) throws IOException {
		super(filePath);
		setPatters("/wiki/([^<]+)<", "<abstract>(.*)</abstract>");
	}

	@Override
	public String[] parseTuple() throws IOException {
		String tuple[] = new String[2];
		String line;

		while ((line = br.readLine()) != null) {

			matcher1 = resourcePattern1.matcher(line);
			matcher2 = resourcePattern2.matcher(line);

			if (matcher1.find())
				tuple[0] = matcher1.group(1);

			if (matcher2.find()){
				tuple[1] = matcher2.group(1);
				return tuple;
			}

		}

		return null;
	}

}
