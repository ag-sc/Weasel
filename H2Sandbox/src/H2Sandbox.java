import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.LinkedList;

import databaseConnectors.H2Connector;
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
		
		/*
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
        */
		LinkedList<String> result = new LinkedList<String>();
		String sql = "select ANCHOR  from ANCHORID where ID in (select PARTIALANCHORTOANCHOR.ANCHORID from PARTIALANCHORTOANCHOR, PARTIALANCHORID where PARTIALANCHORID.ID = PARTIALANCHORTOANCHOR.PARTIALANCHORID and PARTIALANCHOR is (?)) ";
		try {
			H2Connector connector = new H2Connector("../../data/H2/test/test1", "sa", "", sql);
			H2Connector connector2 = new H2Connector("../../data/H2/test/test1", "sa", "", sql);
			result = connector.getFragmentTargets("Megaplex");
			connector.close();
			connector2.close();
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for(String s: result) System.out.println(s);
		
        System.out.println("All done.");
	}

}
