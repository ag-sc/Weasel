package datasetEvaluator;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import fileparser.AnchorFileParser;

public class TestSetBuilder {
	
	static String filePath = "../../data/Wikipedia Anchor/anchors.txt";

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			String line;
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), "UTF8"));
			while((line = br.readLine()) != null){
				if(line.contains("David ")){
					System.out.println(line);
				}
			}
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
