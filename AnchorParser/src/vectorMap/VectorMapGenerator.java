package vectorMap;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.sql.SQLException;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.nustaq.serialization.FSTObjectInput;

import configuration.Config;
import stopwatch.Stopwatch;
import tfidf.DocumentFrequency;
import tfidf.TFIDF;
import databaseConnectors.ConnectorFactory;
import databaseConnectors.DatabaseConnector;
import datatypes.TFIDFResult;
import datatypes.TitleEncoder;
import datatypes.VectorEntry;


public class VectorMapGenerator {
	
	final static int initialMapSize = 15000000;
	
	public VectorMapGenerator() {
		// TODO Auto-generated constructor stub
	}

	public static void run(String vectorMapOutputPath, String dfPath, String abstractPath, String semsigPath, String inMemoryDataContainerPath) throws IOException, ClassNotFoundException, SQLException {
		
		boolean urlEncoding = Boolean.parseBoolean(Config.getInstance().getParameter("useURLEncoding"));
		
		Stopwatch sw2 = new Stopwatch(Stopwatch.UNIT.HOURS);
		
		// get df object
		FileInputStream fileInputStream = new FileInputStream(dfPath);
		
//		ObjectInputStream objectReader = new ObjectInputStream(fileInputStream);
//		DocumentFrequency df = (DocumentFrequency) objectReader.readObject(); 
//		objectReader.close();
		
		FSTObjectInput in = new FSTObjectInput(fileInputStream);	// read object using FST library
		DocumentFrequency df = (DocumentFrequency)in.readObject();
		in.close();
		
		// get abstract reader
		
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(abstractPath), "UTF8"));
		String line;
		int counter = 0;
		
		// create vector map
		HashMap<Integer, VectorEntry> vectorMap = new HashMap<Integer, VectorEntry>(initialMapSize);
		
		// Set up H2 Connector
		String sql = "select entitySinkIDList from EntityToEntity where EntitySourceID is (?)";
//		DatabaseConnector entityDB = new H2Connector(dbPathH, "sa", "", sql, false);
		DatabaseConnector entityDB = ConnectorFactory.getInMemoryConnector(inMemoryDataContainerPath);
		
		System.out.println("Starting Loop");
		Stopwatch sw = new Stopwatch(Stopwatch.UNIT.SECONDS);
		while((line = br.readLine()) != null){
			counter++;
			
			if(counter % 100000 == 0){
				System.out.println(counter + " - " + sw.stop() + " s");
				sw.start();
			}
			
			// Get EntityID and Semantic Signature
			//String title = StringConverter.convert(br.readLine().replace(" ", "_"), "UTF-8");
			String title = br.readLine();
			if(urlEncoding){
				title = TitleEncoder.encodeTitle(title);
			}else{
				title = title.replace(" ", "_");
			}
			
			line = br.readLine();
			Integer id = entityDB.resolveName(title);
			if(id == null && !urlEncoding){
				String normalized = Normalizer.normalize(title, Normalizer.Form.NFD);
				normalized = normalized.replaceAll("[^\\p{ASCII}]", "");
				id = entityDB.resolveName(normalized);
			}
			if(id == null){
				System.err.println(title + " --> Not in DB");
				continue;
			}
			
			//System.out.println("Start on TFIDF");
			// calculate TF/IDF
			
			List<TFIDFResult> resultList = new ArrayList<TFIDFResult>();			
			resultList = TFIDF.compute(line, df);
			Collections.sort(resultList);
			Collections.reverse(resultList);
			
			VectorEntry vEntry = new VectorEntry();
			for(int i = 0; i < 100 && i < resultList.size(); i++){
				vEntry.tfVector[i] = df.getWordID(resultList.get(i).token);
				vEntry.tfScore[i]  = resultList.get(i).tfidf;
			}
			vectorMap.put(id, vEntry);

			
		}
		br.close();

		
		System.out.println("Done with TF/IDF. #Abstracts: " + counter);
		
		System.out.println("Start on Semantic Signature");
		System.err.println("Start on Semantic Signature");
		counter = 0;
		sw.start();
		br = new BufferedReader(new InputStreamReader(new FileInputStream(semsigPath)));
		while((line = br.readLine()) != null){
			//line = line.toLowerCase();
			counter++;
			if(counter % 100000 == 0){
				System.out.println(counter + " semsig entries:\t" + sw.stop() + " s");
				sw.start();
			}
			Integer id = entityDB.resolveName(line);
			if(!vectorMap.containsKey(id)){
				System.err.println(line + " not in vectorMap");
				while(line != null && !line.equals("")) line = br.readLine(); // skip entry
				continue;
			}else{
				VectorEntry entry = vectorMap.get(id);
				for(int i = 0; i < 100; i++){
					line = br.readLine();
					if (line == null || line.equals(""))
						break;
					String lineArray[] = line.split("\t");
					Integer tmpID = entityDB.resolveName(lineArray[0]);
					if (tmpID != null) {
						entry.semSigVector[i] = tmpID;
						entry.semSigCount[i] = Integer.parseInt(lineArray[1]);
					} else {
						i--; // skip this line
					}
				}

				// include entity in its own signature
//				entry.semSigVector[0] = id;
//				if(entry.semSigCount[1] > 0) entry.semSigCount[0] = (int)Math.floor(entry.semSigCount[1] * 1.5);
//				else entry.semSigCount[0] = 1;
				
				while(line != null && !line.equals("")) line = br.readLine();
			}
		}
		
		br.close();
		entityDB.close();
		System.out.println("Done with semsig."  + " - time taken: " + sw2.stop() + " hours");
		
		System.out.println("Write map to binary file");
		sw.start();
		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(vectorMapOutputPath));
		out.writeObject(vectorMap);
		out.close();
		
		System.out.println(("Write to binary file: Done! Write map to text file."));
		BufferedWriter fw = new BufferedWriter(new FileWriter(vectorMapOutputPath + ".txt"));
		for(Entry<Integer, VectorEntry> entry: vectorMap.entrySet()){
			VectorEntry vEntry = entry.getValue();
			
			fw.write(entry.getKey() + "\t" + vEntry.pagerank + "\n");
			for(Integer i: vEntry.semSigVector) fw.write(i + "\t");
			fw.write("\n");
			for(Integer i: vEntry.semSigCount) fw.write(i + "\t");
			fw.write("\n");
			for(Integer i: vEntry.tfVector) fw.write(i + "\t");
			fw.write("\n");
			for(Float f: vEntry.tfScore) fw.write(f + "\t");
			fw.write("\n");
			fw.write("\n");
		}
		fw.close();
		
//		System.out.println("Write map to fst binary file");
//		FSTObjectOutput out_fst = new FSTObjectOutput(new FileOutputStream(vectorMapOutputPath + "_fst"));
//		out_fst.writeObject( vectorMap );
//		out_fst.close(); // required !
		System.out.println("All done! Time taken for file writing: " + sw.stop() + " s");
	}

}
