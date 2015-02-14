import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.LinkedList;


public class NFoldDataSplitter {

	static int folds = 10;
	static int nrOfDataEntries = 1393;
	static String inputPath = "E:/Master Project/data/aida-yago2-dataset/AIDA-YAGO2-dataset.tsv";
	static String outputPath = "E:/Master Project/data/aida-yago2-dataset/aida_fold_";
	
	public static void main(String[] args) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(inputPath)));
		LinkedList<String> dataList = new LinkedList<String>();
		
		int entriesPerFold = 1393 / 10;
		
		String line ="";
		StringBuilder sb = new StringBuilder();
		while((line = br.readLine()) != null){
			if(line.contains("-DOCSTART-") && !line.equals("-DOCSTART- (1 EU)")){
				dataList.add(sb.toString());
				sb = new StringBuilder();
			}
			
			sb.append(line + "\n");
		}
		br.close();
		dataList.add(sb.toString());
		
		System.out.println("size: " + dataList.size());
		Collections.shuffle(dataList);
		for(int i = 0; i < folds; i++){
			
			BufferedWriter fw = new BufferedWriter(new FileWriter(outputPath + "_" + i + ".tsv"));
			
			for(int j = 0; j < entriesPerFold; j++){
				fw.write(dataList.pop());
			}
			
			fw.close();
		}
	}

}
