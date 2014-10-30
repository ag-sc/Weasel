import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import custom.DocumentFrequency;


public class AbstractSandbox {

	public AbstractSandbox() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader("abstracts_cleaned.txt"));
//		BufferedReader br = new BufferedReader(new FileReader("../../data/Wikipedia Abstracts/test/abstracts_cleaned.txt"));
		String line;
		int counter = 0;
		
		DocumentFrequency df = new DocumentFrequency();
		
		while((line = br.readLine()) != null){
			counter++;
			String title = br.readLine().replace(" ", "_");
			line = br.readLine().toLowerCase();
			
			df.addDocument(line);

			if(counter % 100000 == 0){
				System.out.println(counter);
			}
		}
		br.close();
	
		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("documentFrequency"));
//		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("../../data/Wikipedia Abstracts/documentFrequency_test"));
		out.writeObject(df);
		out.close();
		
		System.out.println("All done. #Abstracts: " + counter);
	}

}
