package datatypes;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;


public class StringEncoder {
	private StringEncoder(){
		
	}

	public static String encodeString(String title){
		try {
			try{
				title = URLDecoder.decode(title, "UTF-8");
			}catch (IllegalArgumentException e){
				// The title probably contained a '%' sign that didn't specify a URL character code. Oh well, just ignore it then.
			}
			
			title = title.trim().replace(" ", "_").toLowerCase();
			title = URLEncoder.encode(title, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			System.exit(-1);
		} catch (Exception e){
			System.err.println("Url encoding exception for title: " + title);
			e.printStackTrace();
		}
		return title;
	}
}
