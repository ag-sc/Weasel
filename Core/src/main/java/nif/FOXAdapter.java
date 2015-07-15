package nif;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.fluent.Content;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.utils.URIBuilder;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

import datatypes.StringEncoder;
import datatypes.configuration.Config;

public class FOXAdapter extends ModelAdapter{
	
	public FOXAdapter(){
		super();
	}
	
	protected void innerLoop(Model model, Statement stmt, String originSentence, Resource originResource){
		String foxResult = getContentStringFor(originSentence);
		Model foxModel = ModelFactory.createDefaultModel();
		for(String line: foxResult.split("\n")){
			String[] splitLine = line.split(" ");
			if(splitLine.length != 4){
				//System.out.println("weird line: " + line);
				continue;
			}
			Resource tmpSubject = ResourceFactory.createResource(splitLine[0]);
			String tmp = splitLine[1].replace("<", "").replace(">", "");
			Property tmpProperty = ResourceFactory.createProperty(tmp);
			Resource tmpObject = ResourceFactory.createResource(splitLine[2].replace("<", "").replace(">", ""));
			foxModel.add(tmpSubject, tmpProperty, tmpObject);
		}
		
//		foxModel.write(System.out, "Turtle");
		
		Property means = ResourceFactory.createProperty("http://ns.aksw.org/scms/means");
		Property beginIndexProp = ResourceFactory.createProperty("http://ns.aksw.org/scms/beginIndex");
		Property endIndexProp = ResourceFactory.createProperty("http://ns.aksw.org/scms/endIndex");
		StmtIterator resultIter = foxModel.listStatements(new SimpleSelector(null, means, (RDFNode) null));
		while (resultIter.hasNext()) {
			Resource resultResource = resultIter.nextStatement().getSubject();
			String entity = resultResource.getProperty(means).getObject().toString().replace("http://dbpedia.org/resource/", "");
			String beginIndex = resultResource.getProperty(beginIndexProp).getObject().toString().split("\"")[1];
			String endIndex = resultResource.getProperty(endIndexProp).getObject().toString().split("\"")[1];
			//System.out.println(entity + " - " + beginIndex + "," + endIndex);
			
			if(useURLEncoding) entity = StringEncoder.encodeString(entity);
			
			model.createResource(originResource.toString() + "#char=" + beginIndex + "," + endIndex).addProperty(ITSRDF_SchemaGen.taIdentRef, entity);
		}
	}
	
	
	private String getContentStringFor(String sentence){
		URI uri = buildURI(sentence);
		Content content;
		String page = "";
		try {
			content = Request.Post(uri).addHeader("Content-Type", "application/x-www-form-urlencodeds").execute().returnContent();
			page = URLDecoder.decode(content.toString(), "UTF-8");
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String stringPattern = "\"output\":\"(.+)\",\"input\":";
		Pattern resourcePattern = Pattern.compile(stringPattern, Pattern.DOTALL);
		Matcher matcher = resourcePattern.matcher(page);
		if (matcher.find())
			return matcher.group(1).trim();
		
		return "";
	}
	
	private URI buildURI(String sentence){
		URI uri = null;
		try {
			uri = new URIBuilder()
			.setScheme("http")
			.setHost("fox-demo.aksw.org")
			.setPath("/api")
			.setParameter("input", sentence)
			.setParameter("type", "text")
			.setParameter("task", "NER")
			.setParameter("output", "N-Triples")
			.build();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return uri;
	}
}













