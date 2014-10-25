import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;


public class AbstractSandbox {

	public AbstractSandbox() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader("../../data/Wikipedia Abstracts/abstracts_cleaned.txt"));
		String line;
		int counter = 0;
		
		HashMap<String, Integer> documentFrequency = new HashMap<String, Integer>();
		double wordLength = 0;
		
		while((line = br.readLine()) != null){
			counter++;
			String title = br.readLine().replace(" ", "_");
			String textArray[] = br.readLine().split(" ");
			for(String _word: textArray){
				String word = _word.toLowerCase();
				Integer i = documentFrequency.get(word);
			    if(i == null){
			    	documentFrequency.put(word, 1);
			    	wordLength += word.length();
			    }
			    else documentFrequency.put(word, i + 1);
			}
			if(counter % 100000 == 0){
				double tmpLength = wordLength / (double)documentFrequency.keySet().size();
				System.out.println(counter + "\tMap size: " + documentFrequency.keySet().size() + " - average string length: " + tmpLength);
			}
		}
		br.close();

		wordLength = wordLength / (double)documentFrequency.keySet().size();
		System.out.println("Map size: " + documentFrequency.keySet().size() + " - average string length: " + wordLength);
		
		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("../../data/documentFrequency"));
		out.writeObject(documentFrequency);
		out.close();
		
		System.out.println("All done.");
	}

}
