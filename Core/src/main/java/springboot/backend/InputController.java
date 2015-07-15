package main.java.springboot.backend;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import main.java.nif.ITSRDF_SchemaGen;
import main.java.nif.ModelAdapter;
import main.java.nif.NIFAdapter;
import main.java.nif.NIF_SchemaGen;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

import main.java.datasetEvaluator.DatasetEvaluatorSandbox;
import main.java.entityLinker.InputStringHandler;

@RestController
public class InputController {

	DatasetEvaluatorSandbox sandbox;
	InputStringHandler handler;
	ModelAdapter adapter;
	String wikiString = "https://en.wikipedia.org/wiki/";

	public InputController() throws ClassCastException, ClassNotFoundException, IOException {
		System.out.println("Inputcontroller created");
		sandbox = new DatasetEvaluatorSandbox();
		adapter = new NIFAdapter(sandbox);
		handler = new InputStringHandler();
	}
	
	@RequestMapping(method = RequestMethod.POST, value = "/veasel-english")
	public String handleInputEnglish(@RequestParam(value = "input") String input) {
		// error handling
		if (input == null) return "Empty input!";

		return handleSentence(input);
	}
	
	@RequestMapping(method = RequestMethod.POST, value = "/formatted-output")
	public String handleInputFormattedOutput(String input){
		// error handling
		if (input == null) return "Empty input!";
		Model model = handleSentenceGetModel(input);
		StringBuilder sb = new StringBuilder();
		
		StmtIterator resultIter = model.listStatements(new SimpleSelector(null, null, (RDFNode) NIF_SchemaGen.Context));
		while (resultIter.hasNext()) {
			Statement stmt = resultIter.nextStatement(); // get next statement
			Resource originResource = stmt.getSubject();
			
			List<Resource> resourceResults = new ArrayList<Resource>();
			
			StmtIterator tokenIter = model.listStatements(new SimpleSelector(null, NIF_SchemaGen.referenceContext, (RDFNode) originResource));
			while (tokenIter.hasNext()) {
				Resource tokenResource= tokenIter.next().getSubject();
				Statement entityStatement = tokenResource.getProperty(ITSRDF_SchemaGen.taIdentRef);
				if(entityStatement != null){
					resourceResults.add(tokenResource);
				}
			}
			
			// sort resources by beginIndex
			Collections.sort(resourceResults, new Comparator<Resource>() {
				@Override
				public int compare(Resource o1, Resource o2) {
					int beginIndex1 = o1.getProperty(NIF_SchemaGen.beginIndex).getInt();
					int beginIndex2 = o2.getProperty(NIF_SchemaGen.beginIndex).getInt();
					return beginIndex1 - beginIndex2;
				}
			});
			
			// replace entities in string by formatted result
			String sentence = originResource.getProperty(NIF_SchemaGen.isString).getObject().toString();
			int offset = 0;
			for(Resource resource: resourceResults){
				int beginIndex = resource.getProperty(NIF_SchemaGen.beginIndex).getInt();
				int endIndex = resource.getProperty(NIF_SchemaGen.endIndex).getInt();
				String entity = resource.getProperty(ITSRDF_SchemaGen.taIdentRef).getString();
				String anchorOf = resource.getProperty(NIF_SchemaGen.anchorOf).getString();
				String url = wikiString + entity;
				String formattedEntity = "<a href=\"" + url + "\">" + anchorOf + "</a>";
				
				int lengthBefore = sentence.length();
				sentence = sentence.substring(0, beginIndex + offset) + formattedEntity + sentence.substring(endIndex + offset);
				offset += sentence.length() - lengthBefore;
			}
			sb.append(sentence + "\n");
		}
		
		return sb.toString();
	}
	
	private Model handleSentenceGetModel(String inputSentence){
		handler.resetModel();
		handler.handleString(inputSentence);
		Model model = handler.getModel();
		adapter.linkModel(model);
		return model;
	}
	
	private String handleSentence(String inputSentence){
		Model model = handleSentenceGetModel(inputSentence);
		StringWriter stringWriter = new StringWriter();
		model.write(stringWriter, "Turtle");
		String output = stringWriter.toString();

		return output;
	}
}
