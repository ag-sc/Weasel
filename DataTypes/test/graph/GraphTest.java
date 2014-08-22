package graph;

import static org.junit.Assert.*;
import graph.Graph;

import org.junit.Test;

public class GraphTest {

	@Test
	public void test() {
		Graph<String> graph = new Graph<String>();
		graph.addNode("V1");
		graph.addNode("V2");
		graph.addNode("V3");
		assertEquals("Graph has three nodes", 3, graph.size());
		assertEquals("Average degree is 0", 0.0, graph.avrgDegree(), 0.00001);
		assertTrue("Add edge" ,graph.addEdge("V1", "V2"));
		assertTrue("Add edge" ,graph.addEdge("V2", "V3"));
		assertTrue("Add edge" ,graph.addEdge("V3", "V1"));
		assertEquals("Average degree is 2", 2.0, graph.avrgDegree(), 0.00001);
		assertTrue("Add edge" ,graph.removeEdge("V3", "V1"));
		assertEquals("Average degree is 1.33333", 1.33333333, graph.avrgDegree(), 0.00001);
		assertTrue("Remove node successfully", graph.removeNode("V2"));
		assertEquals("Average degree is 0", 0.0, graph.avrgDegree(), 0.00001);
	}

}
