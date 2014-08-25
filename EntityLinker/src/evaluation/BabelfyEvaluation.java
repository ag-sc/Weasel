package evaluation;

import graph.Graph;
import graph.Node;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.TreeSet;

import datatypes.EntityOccurance;
import datatypes.FragmentCandidateTuple;
import datatypes.FragmentPlusCandidates;
import datatypes.TinyEdge;

public class BabelfyEvaluation extends EvaluationEngine{

	private String filePath = "../../data/Babelfy/semantic signature.binary";
	HashMap<String, TreeSet<String>> semanticSignature;
	Graph<FragmentCandidateTuple> graph;
	
	private void trimToDenseSubgraph(Graph<FragmentCandidateTuple> graph){
		
	}
	
	@SuppressWarnings("unchecked")
	private void loadSemanticSignature() throws IOException, ClassNotFoundException{
		FileInputStream fileInputStream = new FileInputStream(filePath);
		ObjectInputStream objectReader = new ObjectInputStream(fileInputStream);
		semanticSignature = (HashMap<String, TreeSet<String>>) objectReader.readObject(); 
		objectReader.close();
		fileInputStream.close();
	}
	
	@Override
	public LinkedList<EntityOccurance> evaluate(LinkedList<FragmentPlusCandidates> fragments) {
		try {
			loadSemanticSignature();
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
			return null;
		}
		
		// add fragments/candidates to graph
		graph = new Graph<FragmentCandidateTuple>();
		for(FragmentPlusCandidates fpc: fragments){
			for(String candidate: fpc.candidates){
				graph.addNode(new FragmentCandidateTuple(candidate, fpc.fragment));
			}
		}
		
		// build edges
		for(Node<FragmentCandidateTuple> nodeSource: graph.nodeMap.values()){
			for(Node<FragmentCandidateTuple> nodeSink: graph.nodeMap.values()){
				// if fragment is the same, skip
				if(nodeSource.content.entityOccurance.getFragment().equals(nodeSink.content.entityOccurance.getFragment())) continue;
				TreeSet<String> semSig = semanticSignature.get(nodeSource.content.candidate);
				if(semSig.contains(nodeSink.content.candidate)){ // conditions fullfilled, build edge
					graph.addEdge(nodeSource, nodeSink);
				}
			}
		}
		
		// Trim Graph
		trimToDenseSubgraph(graph);
		
		
		return null;
	}

}
