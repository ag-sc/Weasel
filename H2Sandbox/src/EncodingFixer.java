import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;


public class EncodingFixer {

	public static void main(String[] args) throws IOException {
		String sourceFile = "../../data/Wikipedia Abstracts/enwiki-latest-pages-articles.xml";
		BufferedWriter fw = new BufferedWriter(new FileWriter("../../data/Wikipedia Abstracts/enwiki-latest-pages-articles_UTF8.xml"));

		BufferedReader br  = new BufferedReader(new InputStreamReader(new FileInputStream(sourceFile), "UTF8"));
		String line;
		int lineCounter = 0;
		while((line = br.readLine()) != null){
			if(lineCounter % 1000000 == 0){
				System.out.println(lineCounter);
			}
			fw.write(line + "\n");
			lineCounter++;
		}
		
		br.close();
		fw.close();
	}

}
