import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;

import h2.H2AnchorBuilder;
import h2.H2DBCreator;



public class H2Sandbox {

	public H2Sandbox() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args){
//		String dbPath = "../../data/H2/test/test1";
		String dbPath = "/media/data/shared/ftristram/anchors/H2 Anchors/h2Anchors";
//		String filePath = "../../data/Wikipedia Anchor/anchors.txt";		
		String filePath = "anchors.txt";
		
        H2DBCreator dbCreator = new H2DBCreator(dbPath);
        try {
			dbCreator.create();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        try {
//			H2AnchorBuilder builder = new H2AnchorBuilder(dbPath, filePath, "sa", "", "../../data/stopwords.txt");
			H2AnchorBuilder builder = new H2AnchorBuilder(dbPath, filePath, "sa", "", "stopwords.txt");
			builder.run();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        System.out.println("All done.");
	}

}
