package nif;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.util.FileManager;

import datasetEvaluator.DatasetEvaluatorSandbox;
import datatypes.SortableAssociate;
import datatypes.StringEncoder;
import datatypes.annotatedSentence.AnnotatedSentence;
import datatypes.annotatedSentence.Fragment;

public class NIFAdapter {

	Model model;
	DatasetEvaluatorSandbox evaluator;

	public NIFAdapter(String filePath, DatasetEvaluatorSandbox evaluator) {
		model = ModelFactory.createDefaultModel();
		loadModelFromFile(filePath);
		this.evaluator = evaluator;
	}

	private void loadModelFromFile(String filePath) {
		// create an empty model
		model = ModelFactory.createDefaultModel();
		// use the FileManager to find the input file
		InputStream in = FileManager.get().open(filePath);
		if (in == null) {
			throw new IllegalArgumentException("File not found");
		}
		// read the RDF/XML file
		model.read(in, null, "TTL");
	}
	
	public void linkModel(){
		StmtIterator iter = model.listStatements(new SimpleSelector(null, null, (RDFNode) NIF_SchemaGen.Context));
		model.write(System.out, "N-TRIPLES");
		if(true) return;
		
		while (iter.hasNext()) {
			Statement stmt = iter.nextStatement(); // get next statement
			Resource originResource = stmt.getSubject();
			
			StmtIterator tmpIter = model.listStatements(new SimpleSelector(originResource, NIF_SchemaGen.isString,(RDFNode) null));
			String originSentence = null;
			while (tmpIter.hasNext()) {
				originSentence = tmpIter.nextStatement().getObject().asLiteral().toString().split("\\^\\^")[0];
			}
			if(originSentence == null) continue;
			
			System.out.println("Origin Sentence: " + originSentence);
			AnnotatedSentence as = new AnnotatedSentence();
			originSentence = originSentence.replaceAll("\\p{Punct}", "");
			String[] wordArray = originSentence.split(" ");
			for(String word: wordArray){
				Fragment fragment = new Fragment(StringEncoder.encodeString(word));
				as.appendFragment(fragment);
			}
			evaluator.evaluateSentence(as);
			for(Fragment f: as.getFragmentList()){
				System.out.println(f.originWord + " -> " + f.getEntity());
			}
			
//			List<SortableAssociate<Integer, Resource>> resourceList = new ArrayList<SortableAssociate<Integer, Resource>>();
//			tmpIter = model.listStatements(new SimpleSelector(null, NIF_SchemaGen.referenceContext,(RDFNode) originResource));
//			while (tmpIter.hasNext()) {
//				stmt = tmpIter.nextStatement();
//				Resource referenceResource = stmt.getSubject();
//				StmtIterator indexIter = model.listStatements(new SimpleSelector(referenceResource, NIF_SchemaGen.beginIndex,(RDFNode) null));
//				while (indexIter.hasNext()) {
//					stmt = indexIter.nextStatement();
//					Integer index = Integer.parseInt(stmt.getObject().toString().split("\\^\\^")[0]);
//					resourceList.add(new SortableAssociate<Integer, Resource>(index, referenceResource));
//				}
//				
//				//System.out.println(tmpIter.nextStatement());
//			}
//			
//			Collections.sort(resourceList);
//			for(SortableAssociate<Integer, Resource> sa: resourceList){
//				String word = "<empty>";
//				StmtIterator wordIter = model.listStatements(new SimpleSelector(sa.getObjectPart(), NIF_SchemaGen.anchorOf,(RDFNode) null));
//				while (wordIter.hasNext()) {
//					word = wordIter.next().getObject().toString().split("\\^\\^")[0];
//				}
//				System.out.println(sa.getComparablePart() + ": " + word);
//			}
		}
	}
	
}


















