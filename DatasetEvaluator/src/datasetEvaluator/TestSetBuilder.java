package datasetEvaluator;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.TreeSet;

import fileparser.AnchorFileParser;
import fileparser.WikiParser;

public class TestSetBuilder {
	
	static PrintWriter writer;
	
	private static void anchorDavid() throws IOException{
		String filePath = "../../data/Wikipedia Anchor/anchors.txt";
		String line;
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), "UTF8"));
		while((line = br.readLine()) != null){
			if(line.contains("David ")){
				System.out.println(line);
			}
		}
		
	}
	
	private static void wikiLinksSet(String seedPage) throws IOException{
		int lineCounter = 0;
		String filePath = "../../data/Wikipedia/Pagelinks/page_links_en.nt";
		WikiParser parser = new WikiParser(filePath);
		String tuple[];
		TreeSet<String> set = new TreeSet<String>();
		set.add(seedPage);
		while ((tuple = parser.parseTuple()) != null) {
			if (tuple.length == 2) {
				lineCounter++;
				if (tuple[0].equals(seedPage)) {
					set.add(tuple[1]);
				} else if (tuple[1].equals(seedPage)) {
					set.add(tuple[0]);
				}
				if (lineCounter % 1000000 == 0)
					System.out.println(lineCounter);
			}
		}
		parser.close();
		
		System.out.println("Done with treeset");
		
		lineCounter = 0;
		parser = new WikiParser(filePath);
		while ((tuple = parser.parseTuple()) != null) {
			if (tuple.length == 2) {
				lineCounter++;
				if (set.contains(tuple[0]) || set.contains(tuple[1])) {
					writer.println("<resource/" + tuple[0] + "> <> <resource/" + tuple[1] + ">");
				}
				if (lineCounter % 1000000 == 0)
					System.out.println(lineCounter);
			}
		}
		parser.close();
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			writer = new PrintWriter("../../data/Wikipedia/Pagelinks/test/merkel_wiki.txt", "UTF-8");
			wikiLinksSet("Angela_Merkel");
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
