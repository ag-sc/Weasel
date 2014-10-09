package annotatedSentence;

import static org.junit.Assert.*;

import java.util.LinkedList;

import org.junit.Test;

public class AnnotatedSentenceTest {

	@Test
	public void test() {
		String tmp = "1 2 3 4 5 6";
		AnnotatedSentence as = new AnnotatedSentence(tmp.split(" "));
		as.addFragment(new Fragment(0, 0, "1", 0.1));
		as.addFragment(new Fragment(1, 1, "2", 0.1));
		as.addFragment(new Fragment(2, 2, "3", 0.1));
		as.addFragment(new Fragment(3, 3, "4", 0.1));
		as.addFragment(new Fragment(4, 4, "5", 0.1));
		as.addFragment(new Fragment(5, 5, "6", 0.1));
		as.addFragment(new Fragment(0, 1, "12", 0.5));
		as.addFragment(new Fragment(1, 2, "23", 0.6));
		as.addFragment(new Fragment(2, 4, "345", 0.7));
		
		as.assign();
		
		LinkedList<Word> wordList = as.getWordList();
		assertEquals("1 is assigned to 12", "12", wordList.get(0).getDominantFragment().getID());
		assertEquals("2 is assigned to 12", "12", wordList.get(1).getDominantFragment().getID());
		assertEquals("3 is assigned to 345", "345", wordList.get(2).getDominantFragment().getID());
		assertEquals("4 is assigned to 345", "345", wordList.get(3).getDominantFragment().getID());
		assertEquals("5 is assigned to 345", "345", wordList.get(4).getDominantFragment().getID());
		assertEquals("6 is assigned to 6", "6", wordList.get(5).getDominantFragment().getID());
	}

}
