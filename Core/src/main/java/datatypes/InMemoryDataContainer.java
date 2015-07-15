package datatypes;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

public class InMemoryDataContainer implements Serializable{
	private static final long serialVersionUID = 4L;
	
	// Entities
	public String[] idToEntity;
	public Map<String, Integer> entityToID;
	public Map<Integer, Integer> redirects;
	public Set<Integer> disambiguation;

	// Anchors
	public Map<String, Integer> anchorID;
	public int[][] anchorToCandidates;
	public int[][] anchorToCandidatesCount;
	public int[][] anchorToCandidatesProb;
	public int totalNumberOfCandidateReferences;

	public InMemoryDataContainer() {
		// TODO Auto-generated constructor stub
	}

}
