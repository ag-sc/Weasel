package datatypes;

import java.io.Serializable;
import java.util.Map;

public class InMemoryDataContainer implements Serializable{
	private static final long serialVersionUID = 1L;
	
	// Entities
	public String[] idToEntity;
	public Map<String, Integer> entityToID;

	// Anchors
	public Map<String, Integer> anchorID;
	public int[][] anchorToCandidates;
	public int[][] anchorToCandidatesCount;

	public InMemoryDataContainer() {
		// TODO Auto-generated constructor stub
	}

}
