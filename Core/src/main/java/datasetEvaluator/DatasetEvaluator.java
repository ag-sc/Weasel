package datasetEvaluator;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

import nif.ITSRDF_SchemaGen;
import nif.NIF_SchemaGen;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

import datasetEvaluator.datasetParser.DatasetParser;
import datatypes.StringEncoder;
import datatypes.annotatedSentence.AnnotatedSentence;
import datatypes.annotatedSentence.Fragment;
import datatypes.configuration.Config;
import datatypes.databaseConnectors.DatabaseConnector;
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

	public static double[] evaluateModel(Model model) {
		double score = 0.0, precision, recall;
		int goldStdEntities = 0;
		int correctAssignments = 0;
		int totalAssignments = 0;
		
		// Count total assignments
		StmtIterator goldEntitiesIter = model.listStatements(new SimpleSelector(null, Config.datasetEntityProp, (RDFNode) null));
		while(goldEntitiesIter.hasNext()){
			Statement stmt = goldEntitiesIter.next();
			RDFNode subject = stmt.getObject();
			if(!subject.toString().equalsIgnoreCase("--NME--")){
				goldStdEntities++;
			}
		}
		
		StmtIterator assignmentIter = model.listStatements(new SimpleSelector(null, ITSRDF_SchemaGen.taIdentRef, (RDFNode) null));
		// For all resources with a dataset-assigned entity
		while (assignmentIter.hasNext()) {
			totalAssignments++;
			Statement stmt = assignmentIter.nextStatement(); // get next statement
			Resource resource = stmt.getSubject();
			Statement correctEntityStmt = resource.getProperty(Config.datasetEntityProp);
			Statement assignedEntityStmt = resource.getProperty(ITSRDF_SchemaGen.taIdentRef);
			RDFNode assignedEntityNode = assignedEntityStmt.getObject();
			String assignedEntity = assignedEntityNode.toString();
			
			if(correctEntityStmt == null){
				System.out.println("Assigned: " + assignedEntityNode.toString());
				continue;
			}
			RDFNode correctEntityNode = correctEntityStmt.getObject();
			
			String correctEntity = correctEntityNode.toString();
			
//			if(correctEntity != null && assignedEntity != null) continue;
			if(correctEntity.equals(assignedEntity)){
				correctAssignments++;
				System.out.println("Correctly Assigned: " + assignedEntity);
			}else{
				if(correctEntity.equalsIgnoreCase("--NME--")){
					totalAssignments--;
					continue;
				}
				System.out.println("Assigned: " + assignedEntity + "\t| Correct: " + correctEntity);
			}
		}
		
		precision = (double) correctAssignments / (double) totalAssignments;
		recall = (double) correctAssignments / (double) goldStdEntities;
		double fMeasure = (2 * precision * recall) / (precision + recall);
		if(Double.isNaN(fMeasure)) fMeasure = 0;
		
		System.out.println("Precision:\t" + (precision * 100) + " %\t(" + correctAssignments + "/" + totalAssignments + ")"); 
		System.out.println("Recall:\t" + (recall * 100) + " %\t(" + correctAssignments + "/" + goldStdEntities + ")");
		System.out.println("F-Measure:\t" + fMeasure);
		
		double[] results = new double[3];
		results[0] = precision;
		results[1] = recall;
		results[2] = fMeasure;
		return results;
	}

	// TODO: fix the counting so that double entries are not counted twice
	public double evaluate() throws IOException {
		// SimpleFileWriter fw = new
		// SimpleFileWriter("../../data/assignments.txt");

		boolean countRedirectsAsCorrect = Boolean.parseBoolean(Config.getInstance().getParameter("countRedirectsAsCorrect"));

		AnnotatedSentence parserSentence;
		int sentenceCounter = 0;
		// HashSet<Integer> allEntities =
		// parser.getEntitiesInDocument(checkupConnector);
		while ((parserSentence = parser.parse()).length() != 0) {
			System.out.println("Sentence " + (sentenceCounter++) + ":");
			AnnotatedSentence as = linker.link(parserSentence);
			List<Fragment> result = as.getFragmentList();

			for (Fragment f : result) {
				String entity = f.getOriginEntity();
				String candidate = f.getEntity();

				if (entity == null) {
					System.err.println("Entity is null for originword: " + f.originWord);
					System.out.println("Entity is null for originword: " + f.originWord);
					continue;
				}

				if (entity.length() != 0) {
					if (f != null) {
						Integer id = checkupConnector.resolveName(entity);
						if (id != null && f.containsEntity(id.toString())) {
							numberOfCorrectCandidates++;
						} else {
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

					if (countRedirectsAsCorrect) {
						Integer redirectCandidate = checkupConnector.getRedirect(checkupConnector.resolveName(candidate));
						if (redirectCandidate >= 0) {
							String tmp = checkupConnector.resolveID(redirectCandidate.toString());
							if (tmp != null)
								candidate = tmp;
						}

						// Integer redirectEntity =
						// checkupConnector.getRedirect(checkupConnector.resolveName(TitleEncoder.encodeTitle(entity)));
						Integer redirectEntity = checkupConnector.getRedirect(checkupConnector.resolveName(entity));
						if (redirectEntity >= 0) {
							String tmp = checkupConnector.resolveID(redirectEntity.toString());
							if (tmp != null)
								entity = tmp;
						}
						// if(redirectCandidate >= 0 || redirectEntity >= 0)
						// System.out.println("Redirects found for next line.");
					}

					if (entity == null || candidate == null) {
						System.err.println("Candidate or entity null: " + candidate + " - " + entity);
					} else if (entity.equalsIgnoreCase(candidate)) {
						correctEntities++;
						System.out.println(correctEntities + ": " + f.originWord + " -> " + candidate);
					} else {
						System.out.println("##:" + f.originWord + " !-> " + entity + " - picked instead: " + candidate);
					}
				}

			}

			// System.out.println("Assigned:");
			// System.out.println(result + "\n");

			// fw.writeln(result.toString());
			// fw.flush();

		}
		System.out.println(numberOfEntities + " entities in evaluation set.");
		System.out.println(numberOfPossiblyKnownEntities + " entities are in our database ("
				+ ((double) numberOfPossiblyKnownEntities / (double) numberOfEntities * 100.00) + "%)");
		System.out.println(numberOfCorrectCandidates + " fragments have the correct entity in their candidate list ("
				+ ((double) numberOfCorrectCandidates / (double) numberOfEntities * 100.00) + "%)");
		System.out.println(correctEntities + " entities were correctly assigned (" + ((double) correctEntities / (double) numberOfEntities * 100.00) + "%, "
				+ ((double) correctEntities / (double) numberOfCorrectCandidates * 100.00) + "% if we count only the possibly correct ones)");

		checkupConnector.close();
		linker.closeConnectors();

		return ((double) correctEntities / (double) numberOfEntities * 100.00);
	}

}
