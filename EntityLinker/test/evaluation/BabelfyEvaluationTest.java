package evaluation;

import static org.junit.Assert.*;

import java.util.LinkedList;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import databaseConnectors.DatabaseConnector;
import datatypes.EntityOccurance;
import datatypes.FragmentPlusCandidates;

public class BabelfyEvaluationTest {

	LinkedList<FragmentPlusCandidates> fpcList;
	DatabaseConnector connector;
	
	@Before
	public void setup(){
		fpcList = new LinkedList<FragmentPlusCandidates>();
		
		LinkedList<String> candidatesF1 = new LinkedList<String>();
		candidatesF1.add("C1");
		candidatesF1.add("C3");
		fpcList.add(new FragmentPlusCandidates(new EntityOccurance("F1", 0, 0), candidatesF1));
		
		LinkedList<String> candidatesF2 = new LinkedList<String>();
		candidatesF2.add("C2");
		candidatesF2.add("C4");
		fpcList.add(new FragmentPlusCandidates(new EntityOccurance("F2", 0, 0), candidatesF2));
		
		LinkedList<String> candidatesF3 = new LinkedList<String>();
		candidatesF3.add("C5");
		fpcList.add(new FragmentPlusCandidates(new EntityOccurance("F3", 0, 0), candidatesF3));
	
		/// Connector
		
		connector = EasyMock.createMock(DatabaseConnector.class);
		
		LinkedList<String> returnListC2 = new LinkedList<String>();
		returnListC2.add("C1");
		EasyMock.expect(connector.lookUpFragment("C2")).andReturn(returnListC2);
		
		LinkedList<String> returnListC1 = new LinkedList<String>();
		returnListC1.add("C2");
		EasyMock.expect(connector.lookUpFragment("C1")).andReturn(returnListC1);	
		
		LinkedList<String> returnListC3 = new LinkedList<String>();
		returnListC3.add("C2");
		returnListC3.add("C4");
		returnListC3.add("C5");
		EasyMock.expect(connector.lookUpFragment("C3")).andReturn(returnListC3);
		
		LinkedList<String> returnListC4 = new LinkedList<String>();
		returnListC4.add("C1");
		returnListC4.add("C3");
		EasyMock.expect(connector.lookUpFragment("C4")).andReturn(returnListC4);
		
		LinkedList<String> returnListC5 = new LinkedList<String>();
		returnListC5.add("C4");
		EasyMock.expect(connector.lookUpFragment("C5")).andReturn(returnListC5);
		EasyMock.replay(connector);
	}
	
	@Test
	public void evaluateTest() {
		
		BabelfyEvaluation bEval = new BabelfyEvaluation(connector, 0, 1);
		
		LinkedList<EntityOccurance> list = bEval.evaluate(fpcList);
		
		System.out.println(list);
		assertEquals("List has length 3", 3, list.size());
		for (EntityOccurance e : list) {
			if (e.getFragment().equals("F3")) {
				assertEquals("first entry candidate C5", "C5", e.getName());
			} else if (e.getFragment().equals("F2")) {
				assertEquals("first entry candidate C4", "C4", e.getName());
			} else if (e.getFragment().equals("F1")) {
				assertEquals("first entry candidate C3", "C3", e.getName());
			} else {
				fail("Incorrect fragment: " + e.getFragment());
			}
		}
		
	}

}
