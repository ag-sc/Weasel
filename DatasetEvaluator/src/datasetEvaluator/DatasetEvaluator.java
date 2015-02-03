package datasetEvaluator;

import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;

import annotatedSentence.AnnotatedSentence;
import annotatedSentence.Fragment;
import annotatedSentence.Word;
import databaseConnectors.DatabaseConnector;
import datasetParser.DatasetParser;
import datatypes.AnnotatedSentenceDeprecated;
import datatypes.SimpleFileWriter;
import entityLinker.EntityLinker;


public class DatasetEvaluator {

	private DatasetParser parser;
	private EntityLinker linker;
	private DatabaseConnector checkupConnector;
	private int numberOfEntities = 0;
	private int numberOfPossiblyKnownEntities = 0;
	private int numberOfCorrectCandidates = 0;
	private int correctEntities = 0;
	
	public DatasetEvaluator(EntityLinker linker, DatabaseConnector entityDBconnector) {
		this.parser = DatasetParser.getInstance();
		this.linker = linker;
		this.checkupConnector = entityDBconnector;
	}
	// TODO: fix the counting so that double entries are not counted twice
	public String evaluate() throws IOException{	
		//SimpleFileWriter fw = new SimpleFileWriter("../../data/assignments.txt");
		
		AnnotatedSentenceDeprecated parserSentence = new AnnotatedSentenceDeprecated();
		int sentenceCounter = 0;
		//HashSet<Integer> allEntities = parser.getEntitiesInDocument(checkupConnector);
		while((parserSentence = parser.parse()).length() > 0){
			System.out.println("Sentence " + (sentenceCounter++) + ":");
			AnnotatedSentence as = linker.link(parserSentence.getSentence(), null);
			LinkedList<Word> result = as.getWordList();
			
			for(int i = 0; i < result.size(); i++){
				String entity = parserSentence.getEntity(i);
				Fragment tmp = result.get(i).getDominantFragment();
				if (tmp == null) continue;
				String candidate = tmp.getEntity();

				if (entity.length() != 0) {
					Fragment f = result.get(i).getDominantFragment();
					if (f != null) {
						if (f.containsEntity(entity))
							numberOfCorrectCandidates++;
					}

//					if (checkupConnector.fragmentExists(entity)) {
//		// System.out.println(" - In DB: " + entity);
//						numberOfPossiblyKnownEntities++;
//					} else {
//						System.out.println("not in db: " + entity);
//					}

					numberOfEntities++;
					if (entity.equalsIgnoreCase(candidate)) {
						correctEntities++;
						System.out.println(correctEntities + ": " + f.originWord + " -> " + candidate);
					}else{
						System.out.println("##:" + f.originWord + " !-> " + entity + " - picked instead: " + candidate);
					}
				}
				
			}
			
//			System.out.println("Assigned:");
//			System.out.println(result + "\n");
			
//			fw.writeln(result.toString());
//			fw.flush();
			
		}
		System.out.println(numberOfEntities + " entities in evaluation set.");
		System.out.println(numberOfPossiblyKnownEntities + " entities are in our database ("+ ((double)numberOfPossiblyKnownEntities / (double)numberOfEntities * 100.00)+"%)");
		System.out.println(numberOfCorrectCandidates + " fragments have the correct entity in their candidate list ("+ ((double)numberOfCorrectCandidates / (double)numberOfEntities * 100.00)+"%)");
		System.out.println(correctEntities + " entities were correctly assigned ("+ ((double)correctEntities / (double)numberOfEntities * 100.00)+"%)");
	
		checkupConnector.close();
		linker.closeConnectors();
		
		return Double.toString((double)correctEntities / (double)numberOfEntities * 100.00);
	}
	
}
