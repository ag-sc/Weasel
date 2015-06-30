package executable.testPrograms;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.fluent.Content;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.client.utils.URLEncodedUtils;

public class TestHTTPClient {

	public static void main(String[] args) throws ClientProtocolException, IOException, URISyntaxException {
		// TODO Auto-generated method stub
		String string = "In this musical, Madonna played the role of the First Lady.";
		//string = URLEncoder.encode(string, "UTF-8");
		
		URI uri = new URIBuilder()
		.setScheme("http")
        .setHost("fox-demo.aksw.org")
        //.setPath("/call/ner/entities")
        .setPath("/api")
        .setParameter("input", string)
        .setParameter("type", "text")
        .setParameter("task", "NER")
        .setParameter("output", "N-Triples")
        //.setParameter("returnHtml", "true")
        .build();
		Content content = Request.Post(uri).addHeader("Content-Type", "application/x-www-form-urlencodeds").execute().returnContent();
		System.out.println(URLDecoder.decode(content.toString(), "UTF-8"));
	}

}
