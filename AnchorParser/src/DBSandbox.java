import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.TreeSet;

import datatypes.TinyEdge;
import graphAccess.InMemoryGraphAccess;


public class DBSandbox {

	public static void main(String[] args) {
		long start, end;
		double passedTime;
		InMemoryGraphAccess graphAccess = new InMemoryGraphAccess();
		MPFileParser parser = new MPFileParser(graphAccess);
		HashMap<String, TreeSet<TinyEdge>> map;
		
		try {
			//parser.parse("../../data/Mappingbased Properties/mappingbased_properties_cleaned_en.nt");
			//parser.parse("../../data/Mappingbased Properties/test/mappingbased_smallish.nt");
			//parser.parse("../../data/Mappingbased Properties/test/weightsTest.nt");
			
			parser.parse("mappingbased_properties_cleaned_en.nt"); // for execution
			
			map = graphAccess.getMap();
			System.out.println(map.entrySet().size() + " map entries. Perform that many random walks.");
			
			start = System.nanoTime();
			WeightCalculator.setTriangleWeights(graphAccess);
			end = System.nanoTime();
			passedTime = (end - start) / 1000000000.0;
			System.out.println("weights set - Calculation time: " + passedTime + " s");
			
			System.out.println("Write weighted graph to disk.");
			ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("weighted graph.binary"));
			out.writeObject(map);
			out.close();
			System.out.println("Graph written to file 'weighted graph.binary'.");
			
//			for(Entry<String, TreeSet<TinyEdge>> e: map.entrySet()){
//				System.out.println(e.getKey() + ":");
//				for(TinyEdge s: e.getValue()){
//					System.out.println("	" + s);
//				}
//			}
			
			start = System.nanoTime();
			HashMap<String, HashMap<String, Integer>> signatures = SemanticSignatureCalculator.calcSignature(graphAccess);
			
//			for(Entry<String, HashMap<String, Integer>> e: signatures.entrySet()){
//				System.out.println(e.getKey() + ":");
//				for(Entry<String, Integer> e2: e.getValue().entrySet()){
//					System.out.println("	" + e2.getKey() + ": " + e2.getValue());
//				}
//			}
			end = System.nanoTime();
			passedTime = (end - start) / 60000000000.0;
			System.out.println("Calculation time: " + passedTime + " minutes");
			
			System.out.println("Write semantic signatures to disk.");
			out = new ObjectOutputStream(new FileOutputStream("semantic signature.binary"));
			out.writeObject(signatures);
			out.close();
			System.out.println("Graph written to file 'semantic signature.binary'.");
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println("Done! :D");
	}

}
