package main.java.datatypes;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;


public class StringEncoder {
	private StringEncoder(){
		
	}

	public static String encodeString(String string){
		try {
			try{
				string = URLDecoder.decode(string, "UTF-8");
			}catch (IllegalArgumentException e){
				//System.err.println("URL decode error for: " + string);
				//e.printStackTrace();
				// The title probably contained a '%' sign that didn't specify a URL character code. Oh well, just ignore it then.
			}
			
			string = string.trim().replace(" ", "_");
			string = URLEncoder.encode(string, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			System.exit(-1);
		} catch (Exception e){
			System.err.println("Url encoding exception for title: " + string);
			e.printStackTrace();
		}
		return string;
	}
}
