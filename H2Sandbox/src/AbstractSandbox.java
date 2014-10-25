import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.TreeSet;


public class AbstractSandbox {

	public AbstractSandbox() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader("../../data/Wikipedia Abstracts/abstracts_cleaned.txt"));
		String line;
		int counter = 0;
		
		DF_Computation dfComp = new DF_Computation();
		
		while((line = br.readLine()) != null){
			counter++;
			String title = br.readLine().replace(" ", "_");
			line = br.readLine().toLowerCase();

			TreeSet<String> set = new TreeSet<String>();
			for(String s: line.split(" ")) set.add(s);
			
			dfComp.addDocument(set);

			if(counter % 100000 == 0){
				System.out.println(counter);
			}
		}
		br.close();
	
		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("../../data/Wikipedia Abstracts/documentFrequency"));
		out.writeObject(dfComp);
		out.close();
		
		System.out.println("All done.");
	}

}
