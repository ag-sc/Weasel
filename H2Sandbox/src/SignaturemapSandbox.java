import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.SQLException;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.TreeSet;

import stopwatch.Stopwatch;
import tfidf.DocumentFrequency;
import tfidf.TFIDF;
import tfidf.TermFrequency;
import databaseConnectors.H2Connector;
import datatypes.TFIDFResult;
import datatypes.Tuple;


public class SignaturemapSandbox {

	final static String dfPath = "../../data/Wikipedia Abstracts/documentFrequency";
	final static String abstractPath = "../../data/Wikipedia Abstracts/test_abstracts_cleaned_correct.txt";
	final static String dbPathH2 = "E:/Master Project/data/H2/AnchorsPlusPagelinks/h2_anchors_pagelinks";
	final static String outputPath = "../../data/Wikipedia Abstracts/bigmap/bigmap_";
	
//	final static String dfPath = "documentFrequency";
//	final static String abstractPath = "abstracts_cleaned.txt";
//	final static String dbPathH2 = "E:/Master Project/data/H2/AnchorsPlusPagelinks/h2_anchors_pagelinks";
//	final static String outputPath = "../../data/Wikipedia Abstracts/bigmap/bigmap_";
	
	public SignaturemapSandbox() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) throws IOException, ClassNotFoundException, SQLException {
		Stopwatch sw2 = new Stopwatch(Stopwatch.UNIT.HOURS);
		
		// get df object
		FileInputStream fileInputStream = new FileInputStream(dfPath);
		ObjectInputStream objectReader = new ObjectInputStream(fileInputStream);
		//DocumentFrequency df = (DocumentFrequency) objectReader.readObject(); 
		objectReader.close();
		
		// get abstract reader
		
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(abstractPath), "UTF8"));
//		BufferedReader br = new BufferedReader(new FileReader("abstracts_cleaned.txt"));
		String line;
		int counter = 0;
		
		// create fingerprint map
		HashMap<Integer, Tuple<ArrayList<Integer>, HashMap<Integer, Float>>> map = new HashMap<Integer, Tuple<ArrayList<Integer>, HashMap<Integer, Float>>>(1000000);
		
		// Set up H2 Connector
		String sql = "select entitySinkIDList from EntityToEntity where EntitySourceID is (?)";
		H2Connector entityDB = new H2Connector(dbPathH2, "sa", "", sql);
		
		System.out.println("Starting Loop");
		int partCounter = 1;
		Stopwatch sw = new Stopwatch(Stopwatch.UNIT.SECONDS);
		while((line = br.readLine()) != null){
			counter++;
			// Get EntityID and Semantic Signature
			//String title = StringConverter.convert(br.readLine().replace(" ", "_"), "UTF-8");
			String title = br.readLine().replace(" ", "_");
			line = br.readLine().toLowerCase();
			
			//if(counter > 370 && counter < 380) System.out.println(title);
			
			Integer id = entityDB.resolveName(title);
			if(id == null){
				String normalized = Normalizer.normalize(title, Normalizer.Form.NFD);
				normalized = normalized.replaceAll("[^\\p{ASCII}]", "");
				id = entityDB.resolveName(normalized);
				if(id == null){
					System.err.println(title + "/" + normalized + " --> Not in DB");
					continue;
				}
			}
			LinkedList<String> semSigTemp = entityDB.getFragmentTargets(id.toString());
			if(semSigTemp.size() < 2) continue;
			ArrayList<Integer> semSig = new ArrayList<Integer>();
			for(String s: semSigTemp) semSig.add(Integer.parseInt(s));
			
			//System.out.println("Start on TFIDF");
			// calculate TF/IDF
			LinkedList<TFIDFResult> resultList = new LinkedList<TFIDFResult>();			
			//resultList = TFIDF.compute(line, df);
			
			HashMap<Integer, Float> top100TFIDF = new HashMap<Integer, Float>();
			
			for(int i = 0; i < 100 && i < resultList.size(); i++){
				//top100TFIDF.put(df.getWordID(resultList.get(i).token), resultList.get(i).tfidf);
			}
			
			map.put(id, new Tuple<ArrayList<Integer>, HashMap<Integer, Float>>(semSig, top100TFIDF));

			if(counter % 1000 == 0){
				System.out.println(counter + " - " + sw.stop() + " s");
				sw.start();
			}
			
			if(counter % 3000 == 0){
				ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(outputPath + partCounter));
				out.writeObject(map);
				out.close();
				System.out.println("Wrote 'bigmap_"+partCounter + "' to file - " + sw.stop() + " s");
				sw.start();
				partCounter++;
				map = new HashMap<Integer, Tuple<ArrayList<Integer>, HashMap<Integer, Float>>>(1000000);
			}
		}
		br.close();
		entityDB.close();
		
		System.out.println("All done. #Abstracts: " + counter + " - time taken: " + sw2.stop() + " hours");

	}

}
