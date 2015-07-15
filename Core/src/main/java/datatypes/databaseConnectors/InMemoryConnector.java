package main.java.datatypes.databaseConnectors;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import main.java.datatypes.InMemoryDataContainer;

public class InMemoryConnector extends DatabaseConnector {

	// Entities
	final String[] idToEntity;
	final Map<String, Integer> entityToID;
	final Map<Integer, Integer> redirects;
	final Set<Integer> disambiguation;
	
	// Anchors
	final Map<String, Integer> anchorID;
	final int[][] anchorToCandidates;
	final int[][] anchorToCandidatesCount;
	final int[][] anchorToCandidatesProb;
	int totalNumberOfCandidateReferences;
	
	InMemoryConnector(String inMemoryDataContainerFilePath) throws ClassNotFoundException, IOException {
		FileInputStream fileInputStream = new FileInputStream(inMemoryDataContainerFilePath);
		ObjectInputStream objectReader = new ObjectInputStream(fileInputStream);
		InMemoryDataContainer container = (InMemoryDataContainer) objectReader.readObject();
		objectReader.close();
		
		idToEntity = container.idToEntity;
		entityToID = container.entityToID;
		anchorID = container.anchorID;
		anchorToCandidates = container.anchorToCandidates;
		anchorToCandidatesCount = container.anchorToCandidatesCount;
		anchorToCandidatesProb = container.anchorToCandidatesProb;
		redirects = container.redirects;
		disambiguation = container.disambiguation;
		totalNumberOfCandidateReferences = container.totalNumberOfCandidateReferences;
	}

	@Override
	public String resolveID(String id) {
		int index = Integer.parseInt(id);
		return idToEntity[index];
	}

	@Override
	public Integer resolveName(String name) {
		return entityToID.get(name);
	}

	@Override
	public LinkedList<String> getFragmentTargets(String fragment) {
		//fragment = fragment.toLowerCase();
		LinkedList<String> result = new LinkedList<String>();
		Integer id = anchorID.get(fragment);
		if (id != null) {
			for (int i = 0; i < anchorToCandidates[id].length; i++) {
				result.add(anchorToCandidates[id][i] + "_" + anchorToCandidatesCount[id][i] + "_" + anchorToCandidatesProb[id][i]);
			}
		}else{
			//System.err.println("InMemoryConnector: Can't find fragment '" + fragment + "'");
		}

		return result;
	}

	@Override
	public boolean entityExists(String entity) {
		if(entityToID.containsKey(entity)) return true;
		return false;
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub

	}

	@Override
	public int getRedirect(Integer id) {
		if(id != null && redirects.containsKey(id)){
			return redirects.get(id);
		}else{
			return -1;
		}
	}

	@Override
	public boolean isDisambiguation(Integer id) {
		return disambiguation.contains(id);
	}

	@Override
	public int totalNumberOfEntities() {
		return entityToID.size();
	}

//	@Override
//	public int getTotalNumberOfReferences() {
//		return totalNumberOfCandidateReferences;
//	}

}
