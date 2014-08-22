package datasetEvaluator;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;

import databaseConnectors.DatabaseConnector;
import datasetParser.DatasetParser;
import datasetParser.KORE50Parser;
import datatypes.EntityOccurance;
import entityLinker.EntityLinker;


public class DatasetEvaluator {

	private DatasetParser parser;
	private EntityLinker linker;
	private DatabaseConnector entityDBconnector;
	private int numberOfEntities = 0;
	private int numberOfPossiblyKnownEntities = 0;
	private int correctEntities = 0;
	
	public DatasetEvaluator(DatasetParser parser, EntityLinker linker, DatabaseConnector entityDBconnector) {
		this.parser = parser;
		this.linker = linker;
		this.entityDBconnector = entityDBconnector;
	}

	public void evaluate() throws IOException{	
		PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("missing URIs.txt")));
		
		while(parser.goToNext()){
			LinkedList<EntityOccurance> linkerList = linker.link(parser.getSentence());
			LinkedList<String> parserList = parser.getEntities();
			
			numberOfEntities += parserList.size();
			for(String entity: parserList){
				//System.out.print(entity);
				if(entityDBconnector.lookUpFragment(entity).size() > 0){
					//System.out.println(" - In DB: " + entity);
					numberOfPossiblyKnownEntities++;
				}else{
					System.out.println("not: " + entity);
					out.println(entity);
				}
				//System.out.println();
			}
			
			for(EntityOccurance eo: linkerList){
				for(String entity: parserList){
					if(entity.equals(eo.getName())){
						System.out.println("correct: " + eo);
						correctEntities++;
						break;
					}
				}
			}
			
		}
		out.close();
		System.out.println(numberOfEntities + " entities in evaluation set.");
		System.out.println(numberOfPossiblyKnownEntities + " entities are in our database ("+ ((double)numberOfPossiblyKnownEntities / (double)numberOfEntities * 100.00)+"%)");
		System.out.println(correctEntities + " entities were correctly assigned ("+ ((double)correctEntities / (double)numberOfEntities * 100.00)+"%)");
	}
	
}
