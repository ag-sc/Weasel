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

	DatasetEvaluatorSandbox evaluator;

	public NIFAdapter(DatasetEvaluatorSandbox evaluator){
		this.evaluator = evaluator;
	}
	
	public void linkModel(Model model){
		StmtIterator iter = model.listStatements(new SimpleSelector(null, null, (RDFNode) NIF_SchemaGen.Context));
		
		// For all resources labled "context"
		while (iter.hasNext()) {
			Statement stmt = iter.nextStatement(); // get next statement
			Resource originResource = stmt.getSubject();
			
			String originSentence = originResource.getProperty(NIF_SchemaGen.isString).getLiteral().toString().split("\\^\\^")[0];
			if(originSentence == null) continue;
			System.out.println("Origin Sentence: " + originSentence);
			
			// Find all tokens
			ArrayList<Resource> tokenList = new ArrayList<Resource>();
			StmtIterator tokenIter = model.listStatements(new SimpleSelector(null, NIF_SchemaGen.referenceContext, (RDFNode) originResource));
			while(tokenIter.hasNext()){
				Statement tokenStmt = tokenIter.next();
				tokenList.add(tokenStmt.getSubject());
			}
			
			// Use token placeholders to create correct annotatedSentence object.
			String tmpSentence = originSentence.replaceAll("\\p{Punct}", " ");
			for(int i = 0; i < tokenList.size(); i++){
				Resource r = tokenList.get(i);
				String token = r.getProperty(NIF_SchemaGen.anchorOf).getLiteral().toString();
				tmpSentence = tmpSentence.replaceAll("\\b" + token + "\\b", "resourceIndex:" + i);
			}
			
			// Create AnnotatedSentence
			AnnotatedSentence as = new AnnotatedSentence();
			String[] wordArray = tmpSentence.split(" ");
			for(String word: wordArray){
				String tmp = word;
				Fragment fragment = null;
				if(tmp.contains("resourceIndex:")){
					Resource tmpResource = tokenList.get(Integer.parseInt(tmp.replace("resourceIndex:", "")));
					tmp = tmpResource.getProperty(NIF_SchemaGen.anchorOf).getLiteral().toString();
					fragment = new Fragment(StringEncoder.encodeString(tmp), tmpResource);
				}else{
					fragment = new Fragment(StringEncoder.encodeString(tmp));
				}
				as.appendFragment(fragment);
			}
			
			// Annotate
			evaluator.evaluateSentence(as);
//			for(Fragment f: as.getFragmentList()){
//				System.out.println(f.originWord + " -> " + f.getEntity());
//			}
			
			// Assign annotation results to model
			for(Fragment fragment: as.getFragmentList()){
				if(fragment.getEntity() != null && fragment.getOriginResource() != null){
					model.add(fragment.getOriginResource(), ITSRDF_SchemaGen.taIdentRef, fragment.getEntity());
					System.out.println(fragment.getEntity() + " - " + fragment.getOriginResource());
				}
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


















