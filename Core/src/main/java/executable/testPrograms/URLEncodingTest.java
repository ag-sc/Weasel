package main.java.executable.testPrograms;
import main.java.iniloader.IniLoader;

import java.io.IOException;

import main.java.databaseBuilder.fileparser.AnchorFileParser;
import main.java.datatypes.configuration.Config;


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
		while ((triplet = anchorParser.parse()) != null && counter < 20) {
			System.out.println(triplet[0] + " - " + triplet[1]);
			counter++;
		}
	}

}
