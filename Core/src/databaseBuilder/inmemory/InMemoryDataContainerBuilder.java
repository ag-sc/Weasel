package databaseBuilder.inmemory;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;

import utility.Stopwatch;
import databaseBuilder.fileparser.AnchorFileParser;
import datatypes.InMemoryDataContainer;

public class InMemoryDataContainerBuilder {

	public InMemoryDataContainerBuilder() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) throws IOException {
		System.out.println("Starting inMemoryDataContainer creation...");
		Stopwatch sw = new Stopwatch(Stopwatch.UNIT.SECONDS);
		Map<String, Integer> entityToID = new HashMap<String, Integer>();
		Map<String, Integer> anchorIDMap = new HashMap<String, Integer>();
		HashMap<Integer, LinkedList<String>>  anchorToCandidateMap = new HashMap<Integer, LinkedList<String>>();
		int currentEntityID = 0;
		int currentAnchorID = 0;
		
		String quadruple[];
		// find amount of entities and anchors
		System.out.println("Find amount of entities and anchors...");
		AnchorFileParser anchorParser = new AnchorFileParser("anchors.txt");
		int counter = 0;
		while ((quadruple = anchorParser.parse()) != null) {
			String anchor = quadruple[0].toLowerCase();
			Integer anchorID = anchorIDMap.get(anchor);
			if(anchorID == null){
				anchorID = currentAnchorID;
				anchorIDMap.put(anchor, currentAnchorID++);
			}
			
			String entity = quadruple[1].toLowerCase();
			Integer entityID = entityToID.get(entity);
			if(entityID == null){
				entityID = currentEntityID;
				entityToID.put(entity, currentEntityID++);
			}
			
			LinkedList<String> targetEntities = anchorToCandidateMap.get(anchorID);
			if(targetEntities == null){
				anchorToCandidateMap.put(anchorID, new LinkedList<String>());
				targetEntities = anchorToCandidateMap.get(anchorID);
			}
			
			targetEntities.add(entityID + "_" + quadruple[2] + "_" + quadruple[3]);
			
			if(counter % 100000 == 0) System.out.println("Quadruple nr.: " + counter);
			counter++;
		}
		anchorParser.close();
		
		System.out.println("Nr. of anchors: " + currentAnchorID + "\tNr. of Entities: " + currentEntityID);
		
		// assign missing values
		System.out.println("Assign missing values...");
		String[] idToEntity = new String[currentEntityID];
		for(Entry<String, Integer> e: entityToID.entrySet()){
			idToEntity[e.getValue()] = e.getKey();
		}
		
		int[][] anchorToCandidates = new int[currentAnchorID][];
		int[][] anchorToCandidatesCount = new int[currentAnchorID][];
		for(Entry<Integer, LinkedList<String>> e: anchorToCandidateMap.entrySet()){
			int id = e.getKey();
			LinkedList<String> list = e.getValue();
			anchorToCandidates[id] = new int[list.size()];
			anchorToCandidatesCount[id] = new int[list.size()];
			int index = 0;
			for(String s: list){
				String[] split = s.split("_");
				anchorToCandidates[id][index] = Integer.parseInt(split[0]);
				anchorToCandidatesCount[id][index] = Integer.parseInt(split[1]);
				index++;
			}
		}
		
		// Save data to file
		System.out.println("Write data to file...");
		InMemoryDataContainer data = new InMemoryDataContainer();
		data.anchorID = anchorIDMap;
		data.anchorToCandidates = anchorToCandidates;
		data.anchorToCandidatesCount = anchorToCandidatesCount;
		data.idToEntity = idToEntity;
		data.entityToID = entityToID;
		
		try {
			ObjectOutputStream out;
			out = new ObjectOutputStream(new FileOutputStream("inMemoryDataContainer.bin"));
			out.writeObject(data);
			out.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("All done! It took " + sw.stop() + " seconds.");
	}

}
