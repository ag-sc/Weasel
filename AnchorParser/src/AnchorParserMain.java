import fileparser.AbstractParser;

public class AnchorParserMain {

	public static void main(String[] args) {
		System.out.println("Hello World!");

		try{
			AbstractParser parser = new AbstractParser("../../data/Wikipedia Abstracts/enwiki-latest-abstract.xml");
			String tuple[];
			for(int i = 0; i < 20; i++){
				tuple = parser.parseTuple();
				System.out.println(tuple[0] + " - " + tuple[1]);
			}
			
		}catch (Exception e){
			e.printStackTrace();
		}
		
		
		System.out.println("All done :)");
	}

}
