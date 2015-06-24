package nif;

import java.io.InputStream;
import java.util.ArrayList;
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
import datatypes.annotatedSentence.AnnotatedSentence;

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
		while (iter.hasNext()) {
			Statement stmt = iter.nextStatement(); // get next statement
			Resource originResource = stmt.getSubject();
			
			StmtIterator tmpIter = model.listStatements(new SimpleSelector(originResource, NIF_SchemaGen.isString,(RDFNode) null));
			String originSentence = tmpIter.nextStatement().getObject().asLiteral().toString().split("\\^\\^")[0];
			
			System.out.println("Origin Sentence: " + originSentence);
			AnnotatedSentence as = new AnnotatedSentence();
			
			List<Resource> wordList = new ArrayList<Resource>();
			tmpIter = model.listStatements(new SimpleSelector(null, NIF_SchemaGen.referenceContext,(RDFNode) originResource));
			while (tmpIter.hasNext()) {
				System.out.println(tmpIter.nextStatement());
			}
		}
	}
	
}


















