import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.parser.ParserDelegator;

import org.eclipse.mylyn.wikitext.core.parser.MarkupParser;
import org.eclipse.mylyn.wikitext.core.parser.builder.HtmlDocumentBuilder;
import org.eclipse.mylyn.wikitext.core.parser.markup.MarkupLanguage;
import org.eclipse.mylyn.wikitext.core.util.ServiceLocator;

import stopwatch.Stopwatch;
import edu.jhu.nlp.wikipedia.PageCallbackHandler;
import edu.jhu.nlp.wikipedia.WikiPage;
import edu.jhu.nlp.wikipedia.WikiXMLParser;
import edu.jhu.nlp.wikipedia.WikiXMLParserFactory;


public class WikixmljSandbox {

	static BufferedWriter fw;
	static int counter = 0;
	static Stopwatch sw;
	
	public static void main(String[] args) throws IOException {
		fw = new BufferedWriter(new FileWriter("abstracts_cleaned.txt"));
		WikiXMLParser wxsp = WikiXMLParserFactory.getSAXParser("enwiki-latest-pages-articles.xml");
		
//		fw = new BufferedWriter(new FileWriter("../../data/Wikipedia Abstracts/abstracts_cleaned.txt"));
//		WikiXMLParser wxsp = WikiXMLParserFactory.getSAXParser("../../data/Wikipedia Abstracts/enwiki-latest-pages-articles.xml");
		
		sw = new Stopwatch(Stopwatch.UNIT.SECONDS);
		Stopwatch sw2 = new Stopwatch(Stopwatch.UNIT.MINUTES);
		// TODO Auto-generated method stub
		

		try {
			wxsp.setPageCallback(new PageCallbackHandler() {
				public void process(WikiPage page) {
					if(page.isRedirect() || page.isSpecialPage() || page.isDisambiguationPage()) return;
					
					counter++;
					if(counter % 100000 == 0){
						System.out.println(counter + "\t- time: " + sw.stop() + " s");
						sw.start();
					}
					
					if(counter > -1) return;
					
					String textAbstract = page.getWikiText();
					String tmpArray[] = textAbstract.split("==");
					textAbstract = tmpArray[0];
					StringWriter writer = new StringWriter();
		            HtmlDocumentBuilder builder = new HtmlDocumentBuilder(writer);
		            builder.setEmitAsDocument(false);
		            MarkupLanguage language = ServiceLocator.getInstance().getMarkupLanguage("MediaWiki");
		            MarkupParser parser = new MarkupParser(language, builder);
		            parser.parse(textAbstract);
		            
		            final String html = writer.toString();
		            final StringBuilder cleaned = new StringBuilder();

		            HTMLEditorKit.ParserCallback callback = new HTMLEditorKit.ParserCallback() {
		                    public void handleText(char[] data, int pos) {
		                        cleaned.append(new String(data)).append(' ');
		                    }
		            };
		            try {
						new ParserDelegator().parse(new StringReader(html), callback, false);
					} catch (IOException e) {
						System.out.println("Exception in inner parse operations: ");
						e.printStackTrace();
					}	
		            
		            String tmp = cleaned.toString().replaceAll("<ref.*?>.*?>", "");
		            tmp = tmp.toString().replaceAll("\\|.*?\\s?=", "");
		            tmp = tmp.toString().replaceAll("<.*?>", "");
		            tmp = tmp.toString().replaceAll("\\p{Punct}", "");
		            tmp = tmp.toString().replaceAll("\\s+", " ");
		            
		            try {
						fw.write("#-# - " + counter);
						fw.newLine();
						fw.write(page.getTitle().trim());
						fw.newLine();
						fw.write(tmp);
						fw.newLine();
					} catch (IOException e1) {
						System.out.println("Exception in BufferedWriter operations: ");
						e1.printStackTrace();
					}
				}
			});
			
			wxsp.parse();
		} catch (Exception e) {
			System.out.println("Exception in Parse operations: ");
			e.printStackTrace();
		}

		fw.close();
		
		System.out.println("Total count: " + counter + " - total time: " + sw2.stop());
	}

}
