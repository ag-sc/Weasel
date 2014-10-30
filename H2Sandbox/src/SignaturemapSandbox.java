import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.TreeSet;

import stopwatch.Stopwatch;
import custom.DocumentFrequency;
import custom.TermFrequency;
import databaseConnectors.H2Connector;
import datatypes.TFIDFResult;
import datatypes.Tuple;


public class SignaturemapSandbox {

	public SignaturemapSandbox() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) throws IOException, ClassNotFoundException, SQLException {
		Stopwatch sw2 = new Stopwatch(Stopwatch.UNIT.HOURS);
		
		// get df object
		FileInputStream fileInputStream = new FileInputStream("../../data/Wikipedia Abstracts/documentFrequency");
		ObjectInputStream objectReader = new ObjectInputStream(fileInputStream);
		DocumentFrequency df = (DocumentFrequency) objectReader.readObject(); 
		objectReader.close();
		
		// get abstract reader
		BufferedReader br = new BufferedReader(new FileReader("../../data/Wikipedia Abstracts/abstracts_cleaned.txt"));
		String line;
		int counter = 0;
		
		// create fingerprint map
		HashMap<Integer, Tuple<ArrayList<Integer>, HashMap<Integer, Float>>> map = new HashMap<Integer, Tuple<ArrayList<Integer>, HashMap<Integer, Float>>>(1000000);
		
		// Set up H2 Connector
		String dbPathH2 = "E:/Master Project/data/H2/AnchorsPlusPagelinks/h2_anchors_pagelinks";
		String sql = "select entitySinkIDList from EntityToEntity where EntitySourceID is (?)";
		H2Connector entityDB = new H2Connector(dbPathH2, "sa", "", sql);
		
		System.out.println("Starting Loop");
		int partCounter = 1;
		Stopwatch sw = new Stopwatch(Stopwatch.UNIT.SECONDS);
		while((line = br.readLine()) != null){
			counter++;
			// Get EntityID and Semantic Signature
			String title = StringConverter.convert(br.readLine().replace(" ", "_"), "UTF-8");
			line = br.readLine().toLowerCase();
			
			Integer id = entityDB.resolveName(title);
			if(id == null){
				System.err.println(title + " --> Not in DB");
				continue;
			}
			LinkedList<String> semSigTemp = entityDB.getFragmentTargets(id.toString());
			if(semSigTemp.size() < 2) continue;
			ArrayList<Integer> semSig = new ArrayList<Integer>();
			for(String s: semSigTemp) semSig.add(Integer.parseInt(s));
			
			//System.out.println("Start on TFIDF");
			// calculate TF/IDF
			String wordArray[] = line.split(" ");
			TreeSet<String> wordSet = new TreeSet<String>();
			for(String w: wordArray) wordSet.add(w);
			TermFrequency tf = new TermFrequency();
			for(String word: wordArray){
				tf.addTerm(word);
			}
			
			LinkedList<TFIDFResult> resultList = new LinkedList<TFIDFResult>();
			for(String word: wordSet){
				resultList.add(new TFIDFResult(word, df.numberOfDocuments, tf.getFrequency(word), df.getFrequency(word)));
			}
			Collections.sort(resultList);
			Collections.reverse(resultList);
			HashMap<Integer, Float> top100TFIDF = new HashMap<Integer, Float>();
			
			for(int i = 0; i < 100 && i < resultList.size(); i++){
				top100TFIDF.put(df.getWordID(resultList.get(i).token), resultList.get(i).tfidf);
			}
			
			map.put(id, new Tuple<ArrayList<Integer>, HashMap<Integer, Float>>(semSig, top100TFIDF));

			if(counter % 1000 == 0){
				System.out.println(counter + " - " + sw.stop() + " s");
				sw.start();
			}
			
			if(counter % 500000 == 0){
				ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("bigMap_"+partCounter));
				out.writeObject(map);
				out.close();
				partCounter++;
				map = new HashMap<Integer, Tuple<ArrayList<Integer>, HashMap<Integer, Float>>>(1000000);
			}
		}
		br.close();
		entityDB.close();
		
		System.out.println("All done. #Abstracts: " + counter + " - time taken: " + sw2.stop() + " hours");

	}

}
