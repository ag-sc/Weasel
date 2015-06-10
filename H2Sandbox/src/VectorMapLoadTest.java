import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.SQLException;
import java.util.HashMap;

import datatypes.VectorEntry;
import datatypes.databaseConnectors.H2Connector;
import utility.Stopwatch;


public class VectorMapLoadTest {

	public VectorMapLoadTest() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) throws IOException, ClassNotFoundException, SQLException {
		System.out.println("Hello world");
		Stopwatch sw = new Stopwatch(Stopwatch.UNIT.SECONDS);
		FileInputStream fileInputStream = new FileInputStream("../vectormap/vectorMap");
		ObjectInputStream objectReader = new ObjectInputStream(fileInputStream);
		HashMap<Integer, VectorEntry> vectorMap = (HashMap<Integer, VectorEntry>) objectReader.readObject();
		System.out.println("Vectormap load time: " + sw.stop() + " seconds - size: "+ vectorMap.size());
		
//		fileInputStream = new FileInputStream("candidateCount.map");
//		objectReader = new ObjectInputStream(fileInputStream);
//		HashMap<String, Integer> candidateMap = (HashMap<String, Integer>) objectReader.readObject();
//		
//		HashMap<Integer, VectorEntry> smallVectorMap = new HashMap<Integer, VectorEntry>();
//		for(String candidate: candidateMap.keySet()){
//			Integer id = Integer.parseInt(candidate);
//			VectorEntry ve = vectorMap.get(id);
//			if(ve == null){
//				System.out.println("No entry for: " + candidate);
//				continue;
//			}else{
//				smallVectorMap.put(id, ve);
//			}
//		}
//		
//		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("smallVector.map"));
//		out.writeObject(smallVectorMap);
//		out.close();
//		System.out.println("Small map written. Size: " + smallVectorMap.size());
		String entityToEntitySQL = "select entitySinkIDList from EntityToEntity where EntitySourceID is (?)";
		H2Connector h2 = new H2Connector("../vectormap/data/h2_anchors_pagelinks", "sa", "", entityToEntitySQL);
			
		VectorEntry entry = vectorMap.get(12863); // United_States
		System.out.println("Semsig for: " + h2.resolveID("12863"));
		for(int i = 0; i < entry.semSigVector.length; i++){
			System.out.println(h2.resolveID(Integer.toString(entry.semSigVector[i])) + "\t-\t" + entry.semSigCount[i]);
		}
		
		System.out.println("");
		entry = vectorMap.get(122931); // United_Kingdom
		System.out.println("Semsig for: " + h2.resolveID("122931"));
		for(int i = 0; i < entry.semSigVector.length; i++){
			System.out.println(h2.resolveID(Integer.toString(entry.semSigVector[i])) + "\t-\t" + entry.semSigCount[i]);
		}
	}

}
