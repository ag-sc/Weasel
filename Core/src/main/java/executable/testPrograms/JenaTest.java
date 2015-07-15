package main.java.executable.testPrograms;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

import main.java.nif.NIF_SchemaGen;

import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;

import jena.schemagen;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.vocabulary.VCARD;

public class JenaTest {

	public static void main(String[] args) {
		// create an empty model
		 Model model = ModelFactory.createDefaultModel();

		 // use the FileManager to find the input file
		 InputStream in = FileManager.get().open( "E:/Master Project/jena schemagen/kore50_example_short.ttl" );
		if (in == null) {
		    throw new IllegalArgumentException(
		                                 "File not found");
		}

		// read the RDF/XML file
		model.read(in, null, "TTL");
		Map<String, String> testMap = model.getNsPrefixMap();
		for(Entry<String, String> e: testMap.entrySet()){
			System.out.println("Prefix map: " + e.getKey() + " --> " + e.getValue());
		}
		
		Resource subject2 = ResourceFactory.createResource("http://www.mpi-inf.mpg.de/yago-naga/aida/download/KORE50.tar.gz/AIDA.tsv/CEL04#char=0,5");
		Property predicate2 = ResourceFactory.createProperty("http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#referenceContext");
		Resource object2 = ResourceFactory.createResource("http://dbpedia.org/resource/MyTestURL");
		Statement statement = ResourceFactory.createStatement(subject2, predicate2, object2);
		model.add(statement);
		
		subject2 = ResourceFactory.createResource("http://www.mpi-inf.mpg.de/yago-naga/aida/download/KORE50.tar.gz/AIDA.tsv/CEL04#char=0,5");
		predicate2 = NIF_SchemaGen.anchorOf;
		object2 = ResourceFactory.createResource("MyTestAnchor^^http://www.w3.org/2001/XMLSchema#string");
		statement = ResourceFactory.createStatement(subject2, predicate2, object2);
		model.add(statement);
		
		subject2 = ResourceFactory.createResource("http://www.mpi-inf.mpg.de/yago-naga/aida/download/KORE50.tar.gz/AIDA.tsv/CEL04#char=0,5");
		predicate2 = ResourceFactory.createProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type");
		object2 = NIF_SchemaGen.String;
		statement = ResourceFactory.createStatement(subject2, predicate2, object2);
		model.add(statement);
		
		subject2 = ResourceFactory.createResource("http://www.mpi-inf.mpg.de/yago-naga/aida/download/KORE50.tar.gz/AIDA.tsv/CEL04#char=0,5");
		predicate2 = ResourceFactory.createProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type");
		object2 = NIF_SchemaGen.RFC5147String;
		statement = ResourceFactory.createStatement(subject2, predicate2, object2);
		model.add(statement);

		// write it to standard out
		//model.write(System.out);
		
		
		// some definitions
//		String personURI = "http://somewhere/JohnSmith";
//		String givenName = "John";
//		String familyName = "Smith";
//		String fullName = givenName + " " + familyName;
//
//		// create an empty Model
//		Model model = ModelFactory.createDefaultModel();
//
//		// create the resource
//		// and add the properties cascading style
//		Resource johnSmith = model.createResource(personURI).addProperty(VCARD.FN, fullName)
//				.addProperty(VCARD.N, model.createResource().addProperty(VCARD.Given, givenName).addProperty(VCARD.Family, familyName));
//
//		// list the statements in the Model
		StmtIterator iter = model.listStatements();

		// print out the predicate, subject and object of each statement
		while (iter.hasNext()) {
			Statement stmt = iter.nextStatement(); // get next statement
			Resource subject = stmt.getSubject(); // get the subject
			Property predicate = stmt.getPredicate(); // get the predicate
			RDFNode object = stmt.getObject(); // get the object

			System.out.print(subject.toString());
			System.out.print(" " + predicate.toString() + " ");
			if (object instanceof Resource) {
				System.out.print(object.toString());
			} else {
				// object is a literal
				System.out.print(" \"" + object.toString() + "\"");
			}	

			System.out.println(" .");
		}
		System.out.println("\n----------------------------\n");
		//model.write(System.out, "TURTLE");
		RDFDataMgr.write(System.out, model, Lang.TURTLE);
		
		
//		ArrayList<String> argsArray = new ArrayList<String>();
//		argsArray.add("-i");
//		argsArray.add("E:/Master Project/jena schemagen/nif-core_2.owl");
//		
//		argsArray.add("-n");
//		argsArray.add("TestClassNIF");
//		
//		argsArray.add("-o");
//		argsArray.add("E:/Master Project/jena schemagen/");
//		
//		argsArray.add("--owl");
//		
//		String[] args2 = argsArray.toArray(new String[argsArray.size()]);
//		schemagen.main(args2);
	}

}
