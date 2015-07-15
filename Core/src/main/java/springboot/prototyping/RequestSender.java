package main.java.springboot.prototyping;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.fluent.Content;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.utils.URIBuilder;

public class RequestSender {

	static Content content;
	static URI uri = null;
	static String sentence = "David and Victoria added spice to their marriage.";
	
	public static void main(String[] args) throws URISyntaxException {
		
		for(int i = 0; i < 10; i++){
			sendRequest();
		}
		
		testProperRequest();
	}
	
	public static void testProperRequest(){
		try {
			uri = new URIBuilder()
			.setScheme("http")
			.setHost("localhost:8080")
			.setPath("/veasel-english")
			.setParameter("input", sentence)
			.build();
		} catch (URISyntaxException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		String page = "";
		try {
			content = Request.Post(uri).execute().returnContent();
			page = URLDecoder.decode(content.toString(), "UTF-8");
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println(page);
	}
	
	public static void sendRequest(){
		try {
			uri = new URIBuilder()
			.setScheme("http")
			.setHost("localhost:8080")
			.setPath("/veasel-english")
			.setParameter("input", sentence)
			.build();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String page = "";
		try {
			Request.Post(uri).execute();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
