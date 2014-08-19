import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.xml.sax.SAXException;


public class Run {

	public static void main(String[] args) throws XPathExpressionException, ParserConfigurationException, SAXException, IOException {
		

		ReviewCorpusReader reader = new ReviewCorpusReader();
		
		Review review;
		
		DF_Computation df_computation = new DF_Computation();
		
		reader.read("/Users/cimiano/Data/english");
		
		Iterator<Review> it = reader.getIterator();
		
		while (it.hasNext())
		{
			review = it.next();
			
			df_computation.addDocument(review.getContent());
		}
		
		FileWriter writer = new FileWriter("/Users/cimiano/Data/english.tfidf");
		
		TFIDF_Computation tfidf_computation = new TFIDF_Computation(writer);
		
		tfidf_computation.SetDF_Computation(df_computation);
		
		it = reader.getIterator();
		
		while (it.hasNext())
		{
			review = it.next();
			
			tfidf_computation.addDocument(""+ review.getID(),review.getContent());
		}
		
		writer.close();
		
		
	}

}
