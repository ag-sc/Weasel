package datatypes;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;


public class TitleEncoder {
	private TitleEncoder(){
		
	}

	public static String encodeTitle(String title){
		title = title.trim().replace(" ", "_").toLowerCase();
		
		try {
			title = URLDecoder.decode(title, "UTF-8");
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
