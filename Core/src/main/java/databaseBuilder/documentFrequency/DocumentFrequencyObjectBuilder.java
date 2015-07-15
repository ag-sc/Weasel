package databaseBuilder.documentFrequency;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.nustaq.serialization.FSTObjectOutput;

import datatypes.tfidf.DocumentFrequency;


public class DocumentFrequencyObjectBuilder {

	public static void run(String inputFile, String outputFile) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile), "UTF8"));
		String line;
		int counter = 0;
		
		DocumentFrequency df = new DocumentFrequency();
		
		while((line = br.readLine()) != null){
			counter++;
			br.readLine(); // skip title line
			//String title = br.readLine().replace(" ", "_");
			//if(title.contains("?"))System.out.println(title);
			line = br.readLine().toLowerCase();
			
			df.addDocument(line);

			if(counter % 100000 == 0){
				System.out.println(counter);
			}
		}
		br.close();
	
//		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("documentFrequency"));
//		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("../../data/Wikipedia Abstracts/documentFrequency"));
//		out.writeObject(df);
//		out.close();
		FSTObjectOutput out_fst = new FSTObjectOutput(new FileOutputStream(outputFile));
		out_fst.writeObject( df );
		out_fst.close(); // required !
		
		System.out.println("All done. #Abstracts: " + counter);
	}

}
