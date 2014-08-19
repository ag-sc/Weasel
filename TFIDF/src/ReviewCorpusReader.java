import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.xml.sax.SAXException;


public class ReviewCorpusReader {

	
	List<Review> Corpus;
	
	public ReviewCorpusReader()
	{
		Corpus = new ArrayList<Review>();
	}
	
	public void read(String file) throws XPathExpressionException,
			ParserConfigurationException, SAXException, IOException {
		
		
		Review review;
		
	  // Open the file that is the first 
	  // command line parameter
	  FileInputStream fstream = new FileInputStream(file);
	  // Get the object of DataInputStream
	  DataInputStream in = new DataInputStream(fstream);
	  BufferedReader br = new BufferedReader(new InputStreamReader(in));
	  String strLine;
	  
	  // read corpus line by line
	  
	  Pattern p = Pattern.compile("^(.*?)\\t(.*?)\\t(.*?)\\t(.*?)\\t(.*?)\\t(.*?)\\t(.*?)\\t(.*?)\\t(.*?)$");
	  			  
	  Matcher matcher;
	  
	  review = new Review();

	  int no_reviews = 0;
	  
	  while ((strLine = br.readLine()) != null)   {
		  // Print the content on the console
		  matcher = p.matcher(strLine);
		  
		  if (matcher.find())
		  {
			  // call setters for review
			 
			  no_reviews ++;
			  
			  review.setID(no_reviews);
			  
			  // System.out.print("Author: "+matcher.group(1)+"\n");
			  review.setAuthor(matcher.group(1));
			  
			  // System.out.print("Date: "+matcher.group(2)+"\n");
			  review.setDate(matcher.group(2));
			  
			  // System.out.print("ProductID: "+matcher.group(3)+"\n");
			  review.setProductID(matcher.group(3));
			 
			  // System.out.print("Running Number: "+matcher.group(4)+"\n");
			  review.setRunningNumber(matcher.group(4));
			   
			  // System.out.print("Product Name: "+matcher.group(5)+"\n");
			  review.setProductName(matcher.group(5));
			  
			  // System.out.print("Stars: "+matcher.group(6)+"\n");
			  review.setStars(matcher.group(6));
			  
			  // System.out.print("Ratio: "+matcher.group(7)+"\n");
			  review.setRatio(matcher.group(7));
			  
			  // System.out.print("Title: "+matcher.group(8)+"\n");
			  review.setTitle(matcher.group(9));
			  
			  // System.out.print("Content: "+matcher.group(9)+"\n");
			  review.setContent(matcher.group(9));
			  
			 
			  Corpus.add(review);
			  // System.out.print("Adding sentence: "+sentence+"\n");
			  review = new Review();
		  }

		  
	  }

		  
	  //Close the input stream
	  in.close();

	}
	
	public Iterator<Review> getIterator() {
		return Corpus.iterator();
	}

	public List<Review> getReviews(int i, int j) {
		return Corpus.subList(i, j-1);
	}

	public int size() {
		return Corpus.size();
	}

	public List<Review> getReview() {
		return Corpus;
	}

}
