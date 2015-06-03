package datasetEvaluator;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

import configuration.Config;
import annotatedSentence.AnnotatedSentence;
import annotatedSentence.Fragment;
import databaseConnectors.DatabaseConnector;
import datasetParser.DatasetParser;
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
	public double evaluate() throws IOException{	
		//SimpleFileWriter fw = new SimpleFileWriter("../../data/assignments.txt");
		
		boolean countRedirectsAsCorrect = Boolean.parseBoolean(Config.getInstance().getParameter("countRedirectsAsCorrect"));
		
		AnnotatedSentence parserSentence;
		int sentenceCounter = 0;
		//HashSet<Integer> allEntities = parser.getEntitiesInDocument(checkupConnector);
		while((parserSentence = parser.parse()).length() != 0){
			System.out.println("Sentence " + (sentenceCounter++) + ":");
			AnnotatedSentence as = linker.link(parserSentence, null);
			List<Fragment> result = as.getFragmentList();
			
			for(Fragment f: result){
				String entity = f.getOriginEntity();
				String candidate = f.getEntity();

				if(entity == null){
					System.err.println("Entity is null for originword: " + f.originWord);
					System.out.println("Entity is null for originword: " + f.originWord);
					continue;
				}
				
				if (entity.length() != 0) {
					if (f != null) {
						Integer id = checkupConnector.resolveName(entity);
						if (id != null && f.containsEntity(id.toString())){
							numberOfCorrectCandidates++;	
						}else {
							System.err.println("No correct candidate for '" + f.originWord + " -> " + entity + "'");
						}
					}

					if (checkupConnector.entityExists(entity)) {
						// System.out.println(" - In DB: " + entity);
						numberOfPossiblyKnownEntities++;
					} else {
						System.err.println("not in db: " + entity + " - " + URLEncoder.encode(entity, "UTF-8"));
					}

					numberOfEntities++;
					
					if(countRedirectsAsCorrect){
						Integer redirectCandidate = checkupConnector.getRedirect(checkupConnector.resolveName(candidate));
						if(redirectCandidate >= 0){
							String tmp = checkupConnector.resolveID(redirectCandidate.toString());
							if(tmp != null) candidate = tmp;
						}
						
//						Integer redirectEntity = checkupConnector.getRedirect(checkupConnector.resolveName(TitleEncoder.encodeTitle(entity)));
						Integer redirectEntity = checkupConnector.getRedirect(checkupConnector.resolveName(entity));
						if(redirectEntity >= 0){
							String tmp = checkupConnector.resolveID(redirectEntity.toString());
							if(tmp != null) entity = tmp;
						}
//						if(redirectCandidate >= 0 || redirectEntity >= 0)
//							System.out.println("Redirects found for next line.");
					}
					
					if(entity == null || candidate == null){
						System.err.println("Candidate or entity null: " + candidate + " - " + entity);
					}else if (entity.equalsIgnoreCase(candidate)) {
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
		System.out.println(correctEntities + " entities were correctly assigned ("+ ((double)correctEntities / (double)numberOfEntities * 100.00)+"%, " + ((double)correctEntities / (double)numberOfCorrectCandidates * 100.00) + "% if we count only the possibly correct ones)");
	
		checkupConnector.close();
		linker.closeConnectors();
		
		return ((double)correctEntities / (double)numberOfEntities * 100.00);
	}
	
}
