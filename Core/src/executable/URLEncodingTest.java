package executable;
import iniloader.IniLoader;

import java.io.IOException;

import databaseBuilder.fileparser.AnchorFileParser;
import datatypes.configuration.Config;


public class URLEncodingTest {

	public static void main(String[] args) throws IOException {
		String filepath = "../config.ini";
		if(args.length == 1) filepath = args[0];
		System.out.println("Using config file: " + filepath);
		IniLoader iniLoader = new IniLoader();
		iniLoader.parse(filepath);
		
		Config config = Config.getInstance();
		config.setParameter("treatAllAsLowerCase", "true");
		
		AnchorFileParser anchorParser = new AnchorFileParser("E:/Master Project/data/Wikipedia Anchor/anchors.txt");
		
		String[] triplet;
		int counter = 0;
		while ((triplet = anchorParser.parseTriplet()) != null && counter < 20) {
			System.out.println(triplet[0] + " - " + triplet[1]);
			counter++;
		}
	}

}
