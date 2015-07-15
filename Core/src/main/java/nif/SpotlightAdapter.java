package main.java.nif;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;

import main.java.datatypes.StringEncoder;
import main.java.datatypes.configuration.Config;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.fluent.Content;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.utils.URIBuilder;

public class SpotlightAdapter extends ModelAdapter{
	DocumentBuilderFactory dbf;
	double confidence = 0.2;
	int support = 10;
	
	public SpotlightAdapter(){
		super();
		dbf = DocumentBuilderFactory.newInstance();
		Config config = Config.getInstance();
		confidence = Double.parseDouble(config.getParameter("SpotlightAdapterConfidence"));
		support = Integer.parseInt(config.getParameter("SpotlightAdapterSupport"));
	}
	
	@Override
	protected void innerLoop(Model model, Statement stmt, String originSentence, Resource originResource) {
		Content content = spotlightContentRequest(originSentence);
		DocumentBuilder db;
		try {
			db = dbf.newDocumentBuilder();
			Document dom = db.parse(content.asStream());
			Element docEle = dom.getDocumentElement();
			NodeList nl = docEle.getElementsByTagName("Resource");
			if(nl != null && nl.getLength() > 0) {
				for(int i = 0 ; i < nl.getLength();i++) {
					//get the data for every entity
					Element el = (Element)nl.item(i);
					String surfaceForm = el.getAttribute("surfaceForm");
					int beginIndex = Integer.parseInt(el.getAttribute("offset"));
					String entityURI = el.getAttribute("URI");
					
					// Add to the nif model
					int endIndex = beginIndex + surfaceForm.length();
					entityURI = entityURI.replace("http://dbpedia.org/resource/", "");
					if(useURLEncoding) entityURI = StringEncoder.encodeString(entityURI);
					model.createResource(originResource.toString() + "#char=" + beginIndex + "," + endIndex).addProperty(ITSRDF_SchemaGen.taIdentRef, entityURI);
				}
			}
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	protected Content spotlightContentRequest(String input){
		URI uri;
		try {
			uri = new URIBuilder()
			.setScheme("http")
			.setHost("spotlight.dbpedia.org")
			.setPath("/rest/annotate")
			.setParameter("text", input)
			.setParameter("confidence", Double.toString(confidence))
			.setParameter("support", Integer.toString(support))
			.build();
			
			return Request.Get(uri).addHeader("Accept", "text/xml").execute().returnContent();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
