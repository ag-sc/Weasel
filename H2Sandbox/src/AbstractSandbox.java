import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;

import tfidf.DocumentFrequency;


public class AbstractSandbox {

	public AbstractSandbox() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("abstracts_cleaned_correct.txt"), "UTF8"));
//		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("../../data/Wikipedia Abstracts/abstracts_cleaned_correct.txt"), "UTF8"));
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
//		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("../../data/Wikipedia Abstracts/documentFrequency"));
		out.writeObject(df);
		out.close();
		
		System.out.println("All done. #Abstracts: " + counter);
	}

}
