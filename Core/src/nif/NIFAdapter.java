package nif;

import java.util.ArrayList;
import java.util.TreeSet;
import java.util.regex.Pattern;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

import datasetEvaluator.DatasetEvaluatorSandbox;
import datatypes.StringEncoder;
import datatypes.annotatedSentence.AnnotatedSentence;
import datatypes.annotatedSentence.Fragment;

public class NIFAdapter extends ModelAdapter {

	DatasetEvaluatorSandbox evaluator;

	public NIFAdapter(DatasetEvaluatorSandbox evaluator) {
		this.evaluator = evaluator;
	}

	@Override
	protected void innerLoop(Model model, Statement stmt, String originSentence, Resource originResource) {
		// Find all tokens
		ArrayList<Resource> tokenList = new ArrayList<Resource>();
		StmtIterator tokenIter = model.listStatements(new SimpleSelector(null, NIF_SchemaGen.referenceContext, (RDFNode) originResource));
		while (tokenIter.hasNext()) {
			Statement tokenStmt = tokenIter.next();
			tokenList.add(tokenStmt.getSubject());
		}
		
		System.out.println("TokenList " + tokenList.size());
		for(Resource r: tokenList){
			System.out.println(r.getProperty(NIF_SchemaGen.anchorOf).getLiteral().toString());
		}

		// new try
		AnnotatedSentence as = new AnnotatedSentence();
		TreeSet<String> stringSet = new TreeSet<String>();
		for(Resource res: tokenList){
			String token = res.getProperty(NIF_SchemaGen.anchorOf).getLiteral().toString();
			stringSet.add(token);
			Fragment fragment = new Fragment(StringEncoder.encodeString(token), res);
			System.out.println("New fragment: " + fragment.originWord + " - " + fragment.getOriginResource().toString());
			as.appendFragment(fragment);
		}
		String tmpSentence = originSentence.replaceAll("\\p{Punct}", " ");
		for(String word: tmpSentence.split(" ")){
			if(!stringSet.contains(word)){
				Fragment fragment = new Fragment(StringEncoder.encodeString(word));
				as.appendFragment(fragment);
			}
		}
		
//		// old try
//		// Use token placeholders to create correct annotatedSentence object.
//		String tmpSentence = originSentence.replaceAll("\\p{Punct}", " ");
//		for (int i = 0; i < tokenList.size(); i++) {
//			Resource r = tokenList.get(i);
//			String token = r.getProperty(NIF_SchemaGen.anchorOf).getLiteral().toString();
//			tmpSentence = tmpSentence.replaceAll("\\b" + Pattern.quote(token) + "\\b", "resourceIndex:" + i);
//		}
//		
//		System.out.println("Temp Sentence: ");
//		System.out.println(tmpSentence);
//
//		// Create AnnotatedSentence
//		AnnotatedSentence as = new AnnotatedSentence();
//		String[] wordArray = tmpSentence.split(" ");
//		for (String word : wordArray) {
//			String tmp = word;
//			Fragment fragment = null;
//			if (tmp.contains("resourceIndex:")) {
//				System.out.println("found resource index: "  + tmp);
//				Resource tmpResource = tokenList.get(Integer.parseInt(tmp.replace("resourceIndex:", "")));
//				tmp = tmpResource.getProperty(NIF_SchemaGen.anchorOf).getLiteral().toString();
//				fragment = new Fragment(StringEncoder.encodeString(tmp), tmpResource);
//				System.out.println("New fragment: " + fragment.originWord + " - " + fragment.getOriginResource().toString());
//			} else {
//				fragment = new Fragment(StringEncoder.encodeString(tmp));
//			}
//			as.appendFragment(fragment);
//		}

		// Annotate
		evaluator.evaluateSentence(as);
		// for(Fragment f: as.getFragmentList()){
		// System.out.println(f.originWord + " -> " + f.getEntity());
		// }

		// Assign annotation results to model
		for (Fragment fragment : as.getFragmentList()) {
			if (fragment.getEntity() != null && fragment.getOriginResource() != null) {
				model.add(fragment.getOriginResource(), ITSRDF_SchemaGen.taIdentRef, fragment.getEntity());
				//System.out.println(fragment.getEntity() + " - " + fragment.getOriginResource());
			}
		}
	}

	@Override
	protected void afterLoop() {
		evaluator.wekaFinalizeTraining();
	}

}
