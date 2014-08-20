import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.TreeSet;

import datatypes.TinyEdge;
import graphAccess.InMemoryGraphAccess;


public class DBSandbox {

	public static void main(String[] args) {
		long start, end;
		InMemoryGraphAccess graphAccess = new InMemoryGraphAccess();
		MPFileParser parser = new MPFileParser(graphAccess);
		HashMap<String, TreeSet<TinyEdge>> map;
		
		try {
			start = System.nanoTime();
			//parser.parse("../../data/Mappingbased Properties/test/mappingbased_smallish.nt");
			parser.parse("../../data/Mappingbased Properties/test/mappingbased_properties_cleaned_en.nt");
			map = graphAccess.getMap();
			System.out.println(map.entrySet().size() + " map entries. Perform that many random walks.");
			
			WeightCalculator.setUniformWeights(graphAccess);
			
			System.out.println("weights set");
			
//			for(Entry<String, TreeSet<TinyEdge>> e: map.entrySet()){
//				System.out.println(e.getKey() + ":");
//				for(TinyEdge s: e.getValue()){
//					System.out.println("	" + s);
//				}
//			}
			
			HashMap<String, HashMap<String, Integer>> signatures = SemanticSignatureCalculator.calcSignature(graphAccess);
			
			for(Entry<String, HashMap<String, Integer>> e: signatures.entrySet()){
				System.out.println(e.getKey() + ":");
				for(Entry<String, Integer> e2: e.getValue().entrySet()){
					System.out.println("	" + e2.getKey() + ": " + e2.getValue());
				}
			}
			end = System.nanoTime();
			double passedTime = (end - start) / 1000000.0;
			System.out.println("Calculation time: " + passedTime + " ms");
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println("Done!");
	}

}
