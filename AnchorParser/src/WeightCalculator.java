import java.util.Iterator;
import java.util.TreeSet;
import java.util.Map.Entry;

import datatypes.TinyEdge;
import graphAccess.GraphAccess;


public class WeightCalculator {
	
	public static void setUniformWeights(GraphAccess graphAccess){
		Iterator<Entry<String, TreeSet<TinyEdge>>> iterator = graphAccess.getGraphIterator();
		while(iterator.hasNext()){
			for(TinyEdge edge: iterator.next().getValue()){
				edge.weight = 1.0;
			}
		}
	}
	
	public static void setTriangleWeights(GraphAccess graphAccess){
		long start, end;
		double passedTime;
		int counter = 0;
		
		start = System.nanoTime();
		Iterator<Entry<String, TreeSet<TinyEdge>>> iterator = graphAccess.getGraphIterator();
		while(iterator.hasNext()){
			counter++;
			Entry<String, TreeSet<TinyEdge>> entry = iterator.next();
			
			for(TinyEdge edge1: entry.getValue()){ // for every edge of the node in question
				if (edge1.weight == 0) {
					int newWeight = 1;
					TreeSet<TinyEdge> targetNeighborhood1 = graphAccess.query(edge1.target);
					if(targetNeighborhood1 != null)	for(TinyEdge edge2: targetNeighborhood1){ // for every node reachable by the target node
						TreeSet<TinyEdge> targetNeighborhood2 = graphAccess.query(edge2.target);
						if(targetNeighborhood2 != null)	for(TinyEdge edge3: targetNeighborhood2){ // check if that node leads back to origin
							if(edge3.target.equals(entry.getKey())){
								newWeight++;
								break;
							}
						}
					}
					edge1.weight = newWeight;
				}
			}
			
			if(counter % 100000 == 0){
				end = System.nanoTime();
				passedTime = (end - start) / 1000000.0;
				System.out.println(counter + " entries processed - time since last notice: " + passedTime + " ms");
				start = System.nanoTime();
			}
		}
	}
}
