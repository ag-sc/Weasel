package main.java.databaseBuilder.wikipediaAbstracts;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.regex.Pattern;

import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.parser.ParserDelegator;

import org.eclipse.mylyn.wikitext.core.parser.MarkupParser;
import org.eclipse.mylyn.wikitext.core.parser.builder.HtmlDocumentBuilder;
import org.eclipse.mylyn.wikitext.core.parser.markup.MarkupLanguage;
import org.eclipse.mylyn.wikitext.core.util.ServiceLocator;

import main.java.utility.Stopwatch;
import edu.jhu.nlp.wikipedia.PageCallbackHandler;
import edu.jhu.nlp.wikipedia.WikiPage;
import edu.jhu.nlp.wikipedia.WikiXMLParser;
import edu.jhu.nlp.wikipedia.WikiXMLParserFactory;


/**
 * @author Felix Tristram
 * Parse the wikipedia dump in order to extract tf-idf values from all the abstracts.
 */
public class WikiDumpProcessor {

	static BufferedWriter fw;
	static int counter = 0;
	static Stopwatch sw;
	static boolean useURLEncoding = false;
	
//	static BufferedWriter debugFW;
	
	public static void run(String outputFilePath, String inputFilePath) throws IOException {
//		fw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("../../data/Wikipedia Abstracts/test-abstracts_cleaned_correct.txt"), "ISO-8859-15"));
//		WikiXMLParser wxsp = WikiXMLParserFactory.getSAXParser("enwiki-latest-pages-articles.xml");
		
//		useURLEncoding = Boolean.parseBoolean(Config.getInstance().getParameter("useURLEncoding"));
		
//		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(inputFilePath), "UTF8"));
//		InputSource inSource = new InputSource(br);
//		WikiXMLParser wxsp = new WikiXMLSAXParser(inSource.getByteStream());
		
		WikiXMLParser wxsp = WikiXMLParserFactory.getSAXParser(inputFilePath);
		fw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFilePath), "UTF8"));
		
//		debugFW = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("debugFile.txt"), "UTF8"));
		
		sw = new Stopwatch(Stopwatch.UNIT.SECONDS);
		Stopwatch sw2 = new Stopwatch(Stopwatch.UNIT.MINUTES);
		// TODO Auto-generated method stub
		
		// Patterns
		final Pattern pattern1 = Pattern.compile("<ref.*?>.*?>");
		final Pattern pattern2 = Pattern.compile("\\|.*?\\s?=");
		final Pattern pattern3 = Pattern.compile("<.*?>");
		final Pattern pattern4 = Pattern.compile("[\\p{Punct}&&[^-_]]");
		final Pattern pattern5 = Pattern.compile("\\s+");
		final Pattern pattern6 = Pattern.compile("\\p{Pd}");

		try {
			wxsp.setPageCallback(new PageCallbackHandler() {
				MarkupLanguage language = ServiceLocator.getInstance().getMarkupLanguage("MediaWiki");
				
		        
				public void process(WikiPage page) {

//					if(page.isRedirect() || page.isSpecialPage() || page.isDisambiguationPage()) return;
					
					counter++;
					if(counter % 100000 == 0){
						System.out.println(counter + "\t- time: " + sw.stop() + " s");
						sw.start();
					}
					//if(counter < 4400000) return;
					
					String textAbstract = page.getWikiText();
					//System.out.println(textAbstract);
					String tmpArray[] = textAbstract.split("==");
					textAbstract = tmpArray[0];
					StringWriter writer = new StringWriter();
					HtmlDocumentBuilder builder = new HtmlDocumentBuilder(writer);
		            builder.setEmitAsDocument(false);
		            //builder.setEncoding("ISO-8859-15");
		            //System.out.println(builder.getEncoding());
		            
		            MarkupParser parser = new MarkupParser(language, builder);		            
		            
		            String titleTmp = page.getTitle().trim().toLowerCase();
		            if(titleTmp.contains("wikiproject")){
		            	System.out.println("Potentially dangerous abstract: '" + titleTmp + "'. Skip.");
		            	return;
		            }
//		            try {
//						debugFW.write("Current Abstract: " + titleTmp + " (number: " + counter + ")\n");
//						debugFW.flush();
//					} catch (IOException e2) {
//						// TODO Auto-generated catch block
//						e2.printStackTrace();
//					}
		            
		            try{
		            	parser.parse(textAbstract);
		            }catch(OutOfMemoryError oome){
		            	System.out.println("Abstract crashed Mylyn. Skipping. id: " + counter + " - size: " + textAbstract.length() + " - name: " + page.getTitle().trim());
		            	return;
		            }
		            
		            
		            final String html = writer.toString();
		            //System.out.println(html);
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
		            
		            String tmp =  pattern1.matcher(cleaned.toString()).replaceAll("");
		            tmp =  pattern2.matcher(tmp).replaceAll("");
		            tmp =  pattern3.matcher(tmp).replaceAll("");
		            tmp =  pattern4.matcher(tmp).replaceAll("");
		            tmp =  pattern5.matcher(tmp).replaceAll(" ");
		            tmp =  pattern6.matcher(tmp).replaceAll("-");
		            
//		            String tmp = cleaned.toString().replaceAll("<ref.*?>.*?>", "");
//		            tmp = tmp.toString().replaceAll("\\|.*?\\s?=", "");
//		            tmp = tmp.toString().replaceAll("<.*?>", "");
//		            tmp = tmp.toString().replaceAll("\\p{Punct}", "");
//		            tmp = tmp.toString().replaceAll("\\s+", " ");
		            
		            String title = page.getTitle().trim();
		            title =  pattern6.matcher(title).replaceAll("-");
		            
//					if (useURLEncoding) {
//						title = TitleEncoder.encodeTitle(title);
//					}
		            
		            //if(counter > 370 && counter < 380) System.out.println(title);
//		            System.out.println(tmp);
//		            try {
//						//tmp = new String(tmp.getBytes("ISO-8859-15"), "UTF-8");
//						title = new String(page.getTitle().trim().getBytes("ISO-8859-15"), "UTF-8");
//					} catch (UnsupportedEncodingException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
		            
		            try {
						fw.write("# " + counter);
						fw.newLine();
						fw.write(title);
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
