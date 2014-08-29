package neo4j;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.TreeSet;

public class BuildDBSandbox {

	public static void main(String[] args) {
//		System.out.println("Build Treeset");
//		TreeSet<String> treeSet = new TreeSet<String>();
//		try {
//			BufferedReader br = new BufferedReader(new FileReader("../../data/Wikipedia Anchor/1_URIKeyMap.txt"));
//			String line;
//			while((line = br.readLine()) != null){
//				String[] splitLine = line.split("\\t");
//				if(splitLine.length > 1) treeSet.add(splitLine[0]);
//			}
//			
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			return;
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			return;
//		}
		
		long start = System.nanoTime();
		
		System.out.println("System start");
		Neo4jDatabaseBuilder dbBuilder = new Neo4jDatabaseBuilder("../../data/DBs/InfoboxPlusCategories");
		String stringArray[] = new String[2];
		stringArray[0] = "../../data/Wikipedia/Raw Infobox Properties/test/raw_infobox_properties_en.nt";
		stringArray[1] = "../../data/Wikipedia/Article Categories/test/article_categories_en.nt";	
		dbBuilder.run(stringArray);
		
		System.out.println("GraphDB build, build Semantic Signature DB.");
		Neo4jSemSigBuilder semSig = new Neo4jSemSigBuilder("../../data/DBs/InfoboxPlusCategories");
		semSig.run(null);
		
//		Neo4jDatabaseBuilder.run("InfoboxPlusCategories",
//				"raw_infobox_properties_en.nt",
//				"article_categories_en.nt",
//				treeSet);
//		System.out.println("GraphDB build, build Semantic Signature DB.");
//		Neo4jSemSigBuilder.build("InfoboxPlusCategories", "SemanticSignature");

		long end = System.nanoTime();
		double passedTime = (end - start) / 60000000000.0;
		System.out.println("All done! Total time: " + passedTime + " minutes");
	}

}
