package entityLinker;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import annotatedSentence.AnnotatedSentence;
import annotatedSentence.Fragment;
import annotatedSentence.Word;

public class EntityLinkerFragmentedSentenceTest extends EntityLinkerBaseTest{

	String sentence = "a b c d e f";
	
	@Before
	public void setup(){
		EasyMock.expect(anchors.getFragmentTargets("a")).andReturn(new LinkedList<String>(Arrays.asList("A")));
		EasyMock.expect(anchors.getFragmentTargets("b")).andReturn(new LinkedList<String>(Arrays.asList("B")));
		EasyMock.expect(anchors.getFragmentTargets("c")).andReturn(new LinkedList<String>(Arrays.asList("C")));
		EasyMock.expect(anchors.getFragmentTargets("d")).andReturn(new LinkedList<String>(Arrays.asList("D")));
		EasyMock.expect(anchors.getFragmentTargets("e")).andReturn(new LinkedList<String>(Arrays.asList("E")));
		EasyMock.expect(anchors.getFragmentTargets("f")).andReturn(new LinkedList<String>(Arrays.asList("F")));
		EasyMock.expect(anchors.getFragmentTargets("a b")).andReturn(new LinkedList<String>(Arrays.asList("AB")));
		EasyMock.expect(anchors.getFragmentTargets("a b")).andReturn(new LinkedList<String>(Arrays.asList("AB")));
		EasyMock.expect(anchors.getFragmentTargets("b c")).andReturn(new LinkedList<String>(Arrays.asList("BC")));
		EasyMock.expect(anchors.getFragmentTargets("b c")).andReturn(new LinkedList<String>(Arrays.asList("BC")));
		EasyMock.expect(anchors.getFragmentTargets("c d e")).andReturn(new LinkedList<String>(Arrays.asList("CDE")));
		EasyMock.expect(anchors.getFragmentTargets("c d e")).andReturn(new LinkedList<String>(Arrays.asList("CDE")));
		EasyMock.expect(anchors.getFragmentTargets("c d e")).andReturn(new LinkedList<String>(Arrays.asList("CDE")));
		EasyMock.replay(anchors);
		
		EasyMock.expect(partialAnchors.getFragmentTargets("a")).andReturn(new LinkedList<String>(Arrays.asList("a b", "c a b", "a b f")));
		EasyMock.expect(partialAnchors.getFragmentTargets("b")).andReturn(new LinkedList<String>(Arrays.asList("a b", "b c")));
		EasyMock.expect(partialAnchors.getFragmentTargets("c")).andReturn(new LinkedList<String>(Arrays.asList("b c", "c d e")));
		EasyMock.expect(partialAnchors.getFragmentTargets("d")).andReturn(new LinkedList<String>(Arrays.asList("c d e")));
		EasyMock.expect(partialAnchors.getFragmentTargets("e")).andReturn(new LinkedList<String>(Arrays.asList("c d e")));
		EasyMock.expect(partialAnchors.getFragmentTargets("f")).andReturn(new LinkedList<String>());
		EasyMock.replay(partialAnchors);
	}
	
	@Test
	public void getFragmentedSentenceTest() {
		AnnotatedSentence as = linker.getFragmentedSentence(sentence);
		HashMap<Integer, HashMap<Integer, Fragment>> map = as.getFragmentMap();
		
		Fragment tmp;
		tmp = map.get(0).get(1);
		tmp.probability = 0.5;
		tmp.setValue(tmp.candidates.first());
		tmp = map.get(1).get(2);
		tmp.probability = 0.6;
		tmp.setValue(tmp.candidates.first());
		tmp = map.get(2).get(4);
		tmp.probability = 0.7;
		tmp.setValue(tmp.candidates.first());
		
		as.assign();
		LinkedList<Word> list = as.getWordList();
		assertEquals("a -> AB", "a\tAB", list.get(0).toString());
		assertEquals("b -> AB", "b\tAB", list.get(1).toString());
		assertEquals("c -> CDE", "c\tCDE", list.get(2).toString());
		assertEquals("d -> CDE", "d\tCDE", list.get(3).toString());
		assertEquals("e -> CDE", "e\tCDE", list.get(4).toString());
		assertEquals("f -> null", "f", list.get(5).toString());
	}

}
