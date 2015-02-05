import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Scanner;

import stopwatch.Stopwatch;
import datatypes.InMemoryDataContainer;


public class InMemoryQuery {

	public InMemoryQuery() {
		// TODO Auto-generated constructor stub
	}
	
	public static void main(String[] args) throws IOException, ClassNotFoundException{
		System.out.println("Loading file...");
		Stopwatch sw = new Stopwatch(Stopwatch.UNIT.SECONDS);
		FileInputStream fileInputStream = new FileInputStream("inMemoryDataContainer_fromH2.bin");
		ObjectInputStream objectReader = new ObjectInputStream(fileInputStream);
		InMemoryDataContainer container = (InMemoryDataContainer) objectReader.readObject();
		objectReader.close();
		System.out.println("File loaded in " + sw.stop() + " seconds");
		
		String line = "x";
		Scanner sc = new Scanner(System.in);
		while(!line.equals("exit")){
			System.out.println("Query what: ");
			line = sc.nextLine();
			Integer entityID = -1;
			Integer anchorID = -1;
			try{
				anchorID = entityID = Integer.parseInt(line);
			}catch(Exception e){
				
			}
			
			if(entityID == -1){
				entityID = container.entityToID.get(line);
				if(entityID == null){
					System.out.println("No entity found");
					entityID = -1;
				}else {
					System.out.println("Found entity ID: " + entityID);
				}
				
				anchorID = container.anchorID.get(line);
				if(anchorID == null){
					System.out.println("No anchor found");
					anchorID = -1;
				}else {
					System.out.println("Found anchor ID: " + anchorID);
				}
			}
			
			if(entityID != -1){
				if(entityID < container.idToEntity.length) System.out.println("Entity: " + container.idToEntity[entityID]);
				else System.out.println("Entity ID larger than entity array lenght: "+ entityID + " >= " + container.idToEntity.length);
			}
			System.out.println("");
			
			if(anchorID != -1){
				if(anchorID < container.anchorToCandidates.length){
					System.out.println("candidates for anchor:");
					for(int j = 0; j < container.anchorToCandidates[anchorID].length; j++){
						int tmpEntityID = container.anchorToCandidates[anchorID][j];
						System.out.println(tmpEntityID + " == " + container.idToEntity[tmpEntityID] + " count: " + container.anchorToCandidatesCount[anchorID][j]);
					}
				}else System.out.println("Anchor ID larger than anchor array lenght: "+ anchorID + " >= " + container.anchorToCandidates.length);
			}
		}
	}

}
