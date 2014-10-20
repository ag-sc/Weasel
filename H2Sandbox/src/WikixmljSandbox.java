import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.parser.ParserDelegator;

import org.eclipse.mylyn.wikitext.core.parser.MarkupParser;
import org.eclipse.mylyn.wikitext.core.parser.builder.HtmlDocumentBuilder;
import org.eclipse.mylyn.wikitext.core.parser.markup.MarkupLanguage;
import org.eclipse.mylyn.wikitext.core.util.ServiceLocator;

import datatypes.SimpleFileWriter;
import edu.jhu.nlp.wikipedia.PageCallbackHandler;
import edu.jhu.nlp.wikipedia.WikiPage;
import edu.jhu.nlp.wikipedia.WikiXMLParser;
import edu.jhu.nlp.wikipedia.WikiXMLParserFactory;


public class WikixmljSandbox {

	static SimpleFileWriter fw;
	static int counter = 0;
	
	public static void main(String[] args) {
		fw = new SimpleFileWriter("../../data/Wikipedia Abstracts/abstracts_cleaned.txt");
		
		// TODO Auto-generated method stub
		WikiXMLParser wxsp = WikiXMLParserFactory.getSAXParser("../../data/Wikipedia Abstracts/enwiki-latest-pages-articles.xml");

		try {
			wxsp.setPageCallback(new PageCallbackHandler() {
				public void process(WikiPage page) {
					if(page.isRedirect() || page.isSpecialPage() || page.isDisambiguationPage()) return;
					
					counter++;
					if(counter % 100000 == 0) System.out.println(counter + page.getTitle().trim());
					
					fw.writeln("-");
					fw.writeln(page.getTitle().trim());
					
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
						// TODO Auto-generated catch block
						e.printStackTrace();
					}	
		            
		            String tmp = cleaned.toString().replaceAll("(<ref.*?(</ref>|/>)|\\|.*? =)", "");
					fw.writeln(tmp);
					if(page.getTitle().trim().equals("Bear")) System.exit(0);
				}
			});
			
			wxsp.parse();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		fw.close();
		
		System.out.println("Total count: " + counter);
	}

}
