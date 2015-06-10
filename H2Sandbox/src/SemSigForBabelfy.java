import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import datatypes.databaseConnectors.ConnectorFactory;
import datatypes.databaseConnectors.DatabaseConnector;


public class SemSigForBabelfy {

	public static void main(String[] args) throws IOException, ClassNotFoundException {
		System.out.println("Load db...");
		DatabaseConnector entityDB = ConnectorFactory.getInMemoryConnector("../anchor_db/inMemoryDataContainer_fromH2.bin");
		System.out.println("Build semsig object");
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("semsig.txt")));

		HashMap<Integer, ArrayList<Integer>> semsig = new HashMap<Integer, ArrayList<Integer>>();
		
		int nrOfEntries = 0;
		String line;
		while((line = br.readLine()) != null){
			if(!line.isEmpty() && line.split("\t").length == 1){
				Integer root = entityDB.resolveName(line);
				if(root != null){
					ArrayList<Integer> signature = new ArrayList<Integer>();
					while((line = br.readLine()) != null && !line.isEmpty()){
						String[] splitLine = line.split("\t");
						Integer id = entityDB.resolveName(splitLine[0]);
						if(id != null) signature.add(id);
					}
					if(signature.size() > 0){
						semsig.put(root, signature);
						nrOfEntries++;
						if(nrOfEntries % 100000 == 0) System.out.println(nrOfEntries);
					}
				}else{
					System.err.println("No entity: " + line);
				}
			}
		}
		br.close();
		
		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("semsigObject.bin"));
		out.writeObject(semsig);
		out.close();
		
		System.out.println("All done. Nr of entries: " + nrOfEntries);
	}

}
