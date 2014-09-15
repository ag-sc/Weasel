package datasetEvaluator;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;

import databaseConnectors.DatabaseConnector;
import datasetParser.DatasetParser;
import datatypes.AnnotatedSentence;
import datatypes.EntityOccurance;
import datatypes.FragmentPlusCandidates;
import entityLinker.EntityLinker;


public class DatasetEvaluator {

	private DatasetParser parser;
	private EntityLinker linker;
	private DatabaseConnector entityDBconnector;
	private int numberOfEntities = 0;
	private int numberOfPossiblyKnownEntities = 0;
	private int numberOfCorrectCandidates = 0;
	private int correctEntities = 0;
	
	public DatasetEvaluator(DatasetParser parser, EntityLinker linker, DatabaseConnector entityDBconnector) {
		this.parser = parser;
		this.linker = linker;
		this.entityDBconnector = entityDBconnector;
	}
	// TODO: fix the counting so that double entries are not counted twice
	public void evaluate() throws IOException{	
		
		AnnotatedSentence parserSentence = new AnnotatedSentence();
		while((parserSentence = parser.parse()).length() > 0){
			HashMap<String, LinkedList<String>> fragmentPlusCandidates = linker.getFragmentPlusCandidates(parserSentence.getSentence());
			AnnotatedSentence result = new AnnotatedSentence(parserSentence.getSentence());
			linker.link(fragmentPlusCandidates, result);
			
			for(int i = 0; i < result.length(); i++){
				String entity = parserSentence.getEntity(i);
				String candidate = result.getEntity(i);

				if (entity.length() != 0) {
					String tmp = parserSentence.getToken(i);
					LinkedList<String> tmplist = fragmentPlusCandidates.get(tmp);
					if (tmplist != null && tmplist.contains(entity)) {
						// correct candidate is available for token i
						numberOfCorrectCandidates++;
					}

					if (entityDBconnector.fragmentExists(entity)) {
						// System.out.println(" - In DB: " + entity);
						numberOfPossiblyKnownEntities++;
					} else {
						System.out.println("not in db: " + entity);
					}

					numberOfEntities++;
					if (entity.equals(candidate)) {
						correctEntities++;
					}
				}
				
			}
			
			System.out.println("Assigned:");
			System.out.println(result);
		}
		System.out.println(numberOfEntities + " entities in evaluation set.");
		System.out.println(numberOfPossiblyKnownEntities + " entities are in our database ("+ ((double)numberOfPossiblyKnownEntities / (double)numberOfEntities * 100.00)+"%)");
		System.out.println(numberOfCorrectCandidates + " fragments have the correct entity in their candidate list ("+ ((double)numberOfCorrectCandidates / (double)numberOfEntities * 100.00)+"%)");
		System.out.println(correctEntities + " entities were correctly assigned ("+ ((double)correctEntities / (double)numberOfEntities * 100.00)+"%)");
	}
	
}
