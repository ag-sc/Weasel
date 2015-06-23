package nif;

import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.util.FileManager;

import datatypes.annotatedSentence.AnnotatedSentence;

public class NIFAdapter {

	Model model;

	public NIFAdapter() {
		model = ModelFactory.createDefaultModel();
	}

	public void loadModelFromFile(String filePath) {
		// create an empty model
		Model model = ModelFactory.createDefaultModel();
		// use the FileManager to find the input file
		InputStream in = FileManager.get().open(filePath);
		if (in == null) {
			throw new IllegalArgumentException("File not found");
		}
		// read the RDF/XML file
		model.read(in, null, "TTL");
	}
	
	public List<AnnotatedSentence> convertModelToAS(){
		List<AnnotatedSentence> sentenceList = new LinkedList<AnnotatedSentence>();
		
		StmtIterator iter = model.listStatements(new IsStringSelector());
		while (iter.hasNext()) {
			Statement stmt = iter.nextStatement(); // get next statement
		}
		
		return sentenceList;
	}
}


















