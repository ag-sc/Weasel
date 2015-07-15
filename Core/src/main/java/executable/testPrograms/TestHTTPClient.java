package executable.testPrograms;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.fluent.Content;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.client.utils.URLEncodedUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class TestHTTPClient {

	public static void main(String[] args) throws ClientProtocolException, IOException, URISyntaxException, ParserConfigurationException, SAXException {
		// TODO Auto-generated method stub
		String string = "In this musical, Madonna played the role of the First Lady.";
		//string = URLEncoder.encode(string, "UTF-8");
		
		// Spotlight
		URI uri = new URIBuilder()
		.setScheme("http")
        .setHost("spotlight.dbpedia.org")
        .setPath("/rest/annotate")
        .setParameter("text", string)
        .setParameter("confidence", "0.2")
        .setParameter("support", "20")
        //.setParameter("output", "N-Triples")
        //.setParameter("returnHtml", "true")
        .build();
		Content content = Request.Get(uri).addHeader("Accept", "text/xml").execute().returnContent();
		//System.out.println(content.toString());
//		System.out.println(URLDecoder.decode(content.toString(), "UTF-8"));
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document dom = db.parse(content.asStream());
		Element docEle = dom.getDocumentElement();
		NodeList nl = docEle.getElementsByTagName("Resource");
		if(nl != null && nl.getLength() > 0) {
			for(int i = 0 ; i < nl.getLength();i++) {

				//get the employee element
				Element el = (Element)nl.item(i);
				String surfaceForm = el.getAttribute("surfaceForm");
				int offset = Integer.parseInt(el.getAttribute("offset"));
				String entityURI = el.getAttribute("URI");
				System.out.println(surfaceForm + offset + entityURI);
			}
		}
		
		// Fox
//		URI uri = new URIBuilder()
//		.setScheme("http")
//        .setHost("fox-demo.aksw.org")
//        //.setPath("/call/ner/entities")
//        .setPath("/api")
//        .setParameter("input", string)
//        .setParameter("type", "text")
//        .setParameter("task", "NER")
//        .setParameter("output", "N-Triples")
//        //.setParameter("returnHtml", "true")
//        .build();
//		Content content = Request.Post(uri).addHeader("Content-Type", "application/x-www-form-urlencodeds").execute().returnContent();
//		System.out.println(URLDecoder.decode(content.toString(), "UTF-8"));
	}

}
