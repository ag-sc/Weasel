package datatypes;

import java.io.Serializable;
import java.util.Collection;

public class PageRankNode implements Serializable {
	private static final long serialVersionUID = 1L;
	public final double[] pagerankBuffer = new double[2];
	public final int[] outgoing;
	
	public PageRankNode(double initialRank, Collection<String> outgoingLinks) {
		pagerankBuffer[0] = initialRank;
		pagerankBuffer[1] = initialRank;
		
		outgoing = new int[outgoingLinks.size()];
		int index = 0;
		for(String s: outgoingLinks) outgoing[index++] = Integer.parseInt(s);
	}

}
