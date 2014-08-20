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
}
