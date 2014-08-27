package evaluation;

import graph.Graph;
import graph.GraphEdge;
import graph.Node;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.TreeSet;

import databaseConnectors.DatabaseConnector;
import datatypes.EntityOccurance;
import datatypes.FragmentCandidateTuple;
import datatypes.FragmentPlusCandidates;

public class BabelfyEvaluation extends EvaluationEngine{

	private DatabaseConnector semanticSignatureDB;
	private Graph<FragmentCandidateTuple> graph;
	private double numberOfFragments = 0.0;
	private double minimumScore = 0.0;
	private int ambiguityLevel = 1;
	
	public BabelfyEvaluation(DatabaseConnector semanticSignatureDB, double minimumScore, int ambiguityLevel){
		this.semanticSignatureDB = semanticSignatureDB;
		this.minimumScore = minimumScore;
		this.ambiguityLevel = ambiguityLevel;
	}
	
	private Graph<FragmentCandidateTuple> trimToDenseSubgraph(Graph<FragmentCandidateTuple> graph){
		Graph<FragmentCandidateTuple> trimmedGraph = graph.deepcopy();
		double avrgDegree = 0.0;
		
		for (int sanityCounter = 0; sanityCounter < 100000; sanityCounter++) {
			// Get mous ambigous fragment
			EntityOccurance mostAmbigousFragment = null;
			HashMap<EntityOccurance, Integer> tmpMap = new HashMap<EntityOccurance, Integer>();
			for (FragmentCandidateTuple fct : graph.nodeMap.keySet()) {
				if (tmpMap.get(fct.entityOccurance) == null)
					tmpMap.put(fct.entityOccurance, 1);
				else
					tmpMap.put(fct.entityOccurance, tmpMap.get(fct.entityOccurance) + 1);
			}
			int max = 0;
			for (Entry<EntityOccurance, Integer> e : tmpMap.entrySet()) {
				if (e.getValue() > max) {
					max = e.getValue();
					mostAmbigousFragment = e.getKey();
				}
			}
			
			if(max <= ambiguityLevel) return trimmedGraph; // end algorithm if ambiguity low
			
			//TODO: only update nodes that are changed
			scoreAllFragments();
			
			double score = 1000000;
			FragmentCandidateTuple weakestCandidate = null;
			for (FragmentCandidateTuple fct : graph.nodeMap.keySet()) {
				if(fct.entityOccurance == mostAmbigousFragment && fct.score < score){
					score = fct.score;
					weakestCandidate = fct;
				}
			}
			graph.removeNode(weakestCandidate);
			
			if(graph.avrgDegree() > avrgDegree){
				avrgDegree = graph.avrgDegree();
				trimmedGraph = graph.deepcopy();
			}
		}
		
		System.out.println("		Graph trimming cancelled! Loop too long!");
		return trimmedGraph;
	}
	
	private void scoreAllFragments(){
		for(Node<FragmentCandidateTuple> node: graph.nodeMap.values()){
			node.content.weight = weight(node.content);
		}
		
		for(Node<FragmentCandidateTuple> nodeOrigin: graph.nodeMap.values()){
			double tmp = 0.0;
			for(Node<FragmentCandidateTuple> otherNode: graph.nodeMap.values()){
				if(nodeOrigin != otherNode) tmp += otherNode.degree() * otherNode.content.weight;
			}
			
			double part1 = (nodeOrigin.degree() * nodeOrigin.content.weight);
			double score = part1 / tmp;
			nodeOrigin.content.score = score;
		}
	}
	
	private double weight(FragmentCandidateTuple fct){
		Node<FragmentCandidateTuple> node = graph.getNode(fct);
		TreeSet<String> connectingFragments = new TreeSet<String>();
		for(GraphEdge<FragmentCandidateTuple> edge: node.incomingEdges){
			connectingFragments.add(edge.source.content.entityOccurance.getFragment());
		}
		for(GraphEdge<FragmentCandidateTuple> edge: node.outgoingEdges){
			connectingFragments.add(edge.sink.content.entityOccurance.getFragment());
		}
		//TODO: check division by 0?
		double weight = (double)connectingFragments.size() / (double)(numberOfFragments - 1);
		return weight;
	}
	
	@Override
	public LinkedList<EntityOccurance> evaluate(LinkedList<FragmentPlusCandidates> fragments) {
		System.out.println("Starting evaluation... ");
		numberOfFragments = fragments.size();
		// add fragments/candidates to graph
		graph = new Graph<FragmentCandidateTuple>();
		for(FragmentPlusCandidates fpc: fragments){
			for(String candidate: fpc.candidates){
				graph.addNode(new FragmentCandidateTuple(candidate, fpc.fragment));
			}
		}
		
		System.out.println("	Graph nodes added.");
		
		// build edges
		for(Node<FragmentCandidateTuple> nodeSource: graph.nodeMap.values()){
			TreeSet<String> semSig = new TreeSet<String>();
			LinkedList<String> tmp = semanticSignatureDB.lookUpFragment(nodeSource.content.candidate);
			for (String s : tmp) {
				semSig.add(s);
			}

			for(Node<FragmentCandidateTuple> nodeSink: graph.nodeMap.values()){
				// if fragment is the same, skip
				if(nodeSource.content.entityOccurance.getFragment().equals(nodeSink.content.entityOccurance.getFragment())) continue;
				
				if(semSig.contains(nodeSink.content.candidate)){ // conditions fullfilled, build edge
					System.out.println("		Adding edge: " + nodeSource.content.candidate + " --> " + nodeSink.content.candidate);
					graph.addEdge(nodeSource, nodeSink);
				}
			}
		}
		System.out.println("	Graph edges added.");
		
		// Trim Graph
		trimToDenseSubgraph(graph);
		System.out.println("	Graph trimmed.");
		scoreAllFragments();
		
		HashMap<EntityOccurance, Double> scoreMap = new HashMap<EntityOccurance, Double>();
		for(Node<FragmentCandidateTuple> node: graph.nodeMap.values()){
			Double tmp = scoreMap.get(node.content.entityOccurance);
			if(tmp == null || tmp < node.content.score){
				node.content.entityOccurance.setName(node.content.candidate);
				scoreMap.put(node.content.entityOccurance, node.content.score);
			}
		}
		
		LinkedList<EntityOccurance> tmp = new LinkedList<EntityOccurance>();
		for(Entry<EntityOccurance, Double> e: scoreMap.entrySet()){
			if(e.getValue() > minimumScore) tmp.add(e.getKey());
		}
		
		System.out.println("	Evaluation complete.");
		return tmp;
	}

}
