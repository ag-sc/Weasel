package executable.tools;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.LinkedList;


public class NFoldDataSplitter {

//	static int folds = 10;
//	static int nrOfDataEntries = 1360;
//	static String inputPath = "E:/Master Project/data/aida-yago2-dataset/spotlight/aida_picked_setences.tsv";
//	static String outputPath = "E:/Master Project/data/aida-yago2-dataset/spotlight/aida_spotlight_10fold";
	
//	static int folds = 2;
//	static int nrOfDataEntries = 1393;
//	static String inputPath = "E:/Master Project/data/aida-yago2-dataset/AIDA-YAGO2-dataset.tsv";
//	static String outputPath = "E:/Master Project/data/aida-yago2-dataset/aida_2fold";
	
	static int folds = 10;
	static int nrOfDataEntries = 50;
	static String inputPath = "E:/Master Project/data/kore50/kore50.tsv";
	static String outputPath = "E:/Master Project/data/kore50/kore50_fold";
	
	public static void main(String[] args) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(inputPath)));
		LinkedList<String> dataList = new LinkedList<String>();
		
		int entriesPerFold = nrOfDataEntries / folds;
		
		String line ="";
		StringBuilder sb = new StringBuilder();
		while((line = br.readLine()) != null){
			if(line.contains("-DOCSTART-") && (!line.equals("-DOCSTART- (1 EU)") ||  !line.equals("-DOCSTART- (1 CEL01)"))){
				if(!sb.toString().isEmpty()) dataList.add(sb.toString());
				sb = new StringBuilder();
			}
			
			if(!line.isEmpty()) sb.append(line + "\n");
		}
		br.close();
		dataList.add(sb.toString().trim());
		
		System.out.println("size: " + dataList.size());
		Collections.shuffle(dataList);
		
		for(int i = 0; i < folds; i++){
			
			BufferedWriter fw = new BufferedWriter(new FileWriter(outputPath + "_" + i + ".tsv"));
			
			for(int j = 0; j < entriesPerFold; j++){
				if(j < entriesPerFold - 1){
					fw.write(dataList.pop() + "\n");
				}else{
					fw.write(dataList.pop());
				}
			}
			
			fw.close();
		}
		
		if(folds > 2){
			for(int i = 0; i < folds; i++){
				BufferedWriter fw = new BufferedWriter(new FileWriter(outputPath + "_" + i + "_trainset.tsv"));
				for(int j = 0; j < folds; j++){
					if(i == j) continue;
					br = new BufferedReader(new InputStreamReader(new FileInputStream(outputPath + "_" + j + ".tsv")));
					while((line = br.readLine()) != null){
						fw.write(line + "\n");
					}
					br.close();
				}
				fw.close();
			}
		}
	}

}
