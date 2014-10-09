package evaluation;

import graph.Graph;
import graph.GraphEdge;
import graph.Node;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.TreeSet;

import stopwatch.Stopwatch;
import annotatedSentence.AnnotatedSentence;
import annotatedSentence.Fragment;
import annotatedSentence.Word;
import databaseConnectors.DatabaseConnector;
import databaseConnectors.H2Connector;
import datatypes.FragmentCandidateTuple;

public class BabelfyEvaluation extends EvaluationEngine{

	private DatabaseConnector semanticSignatureDB;
	private Graph<FragmentCandidateTuple> graph;
	private double numberOfFragments = 0.0;
	private double minimumScore = 0.0;
	private int ambiguityLevel = 1;
	
	public double lookUpTime = 0.0;
	public double searchSetTime = 0.0;
	
	public BabelfyEvaluation(DatabaseConnector semanticSignatureDB, double minimumScore, int ambiguityLevel){
		this.semanticSignatureDB = semanticSignatureDB;
		this.minimumScore = minimumScore;
		this.ambiguityLevel = ambiguityLevel;
	}
	
	private Graph<FragmentCandidateTuple> trimToDenseSubgraph(Graph<FragmentCandidateTuple> graph){
		Graph<FragmentCandidateTuple> trimmedGraph = graph.deepcopy();
		double avrgDegree = 0.0;
		
		for (int sanityCounter = 0; sanityCounter < 100000; sanityCounter++) {
			// Get most ambiguous fragment
			Fragment mostAmbigousFragment = null;
			HashMap<Fragment, Integer> tmpMap = new HashMap<Fragment, Integer>();
			for (FragmentCandidateTuple fct : graph.nodeMap.keySet()) {
				if (tmpMap.get(fct.fragment) == null)
					tmpMap.put(fct.fragment, 1);
				else
					tmpMap.put(fct.fragment, tmpMap.get(fct.fragment) + 1);
			}
			int max = 0;
			for (Entry<Fragment, Integer> e : tmpMap.entrySet()) {
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
				if(fct.fragment == mostAmbigousFragment && fct.score < score){
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
		HashMap<Fragment, Double> fragmentWeight = new HashMap<Fragment, Double>();
		
		for(Node<FragmentCandidateTuple> node: graph.nodeMap.values()){
			node.content.weight = weight(node.content);
			
			double tmpScore = node.degree() * node.content.weight;
			if(fragmentWeight.containsKey(node.content.fragment)){	
				fragmentWeight.put(node.content.fragment, fragmentWeight.get(node.content.fragment) + tmpScore);
			}else{
				fragmentWeight.put(node.content.fragment, tmpScore);
			}
		}
		
		// debug start
//		Node<FragmentCandidateTuple> david;
//		LinkedList<Node<FragmentCandidateTuple>> list = new LinkedList<Node<FragmentCandidateTuple>>();
//		for(Node<FragmentCandidateTuple> node: graph.nodeMap.values()){
//			list.add(node);
//			if(node.content.candidate.equals("David_Beckham")) david = node;
//		}
//		Collections.sort(list, new Comparator<Node<FragmentCandidateTuple>>(){
//			@Override
//			public int compare(Node<FragmentCandidateTuple> o1, Node<FragmentCandidateTuple> o2) {
//				if(o2.content.weight > o1.content.weight) return 1;
//				else if(o2.content.weight < o1.content.weight) return -1;
//				else return 0;
//			}
//			
//		});
		
		// debug end
		
		for(Node<FragmentCandidateTuple> nodeOrigin: graph.nodeMap.values()){
			double tmp = 0.0;
			tmp = fragmentWeight.get(nodeOrigin.content.fragment);
			
			double part1 = (nodeOrigin.degree() * nodeOrigin.content.weight);
			double score = part1 / tmp;
			nodeOrigin.content.score = score;
		}
	}
	
	private double weight(FragmentCandidateTuple fct) {
		Node<FragmentCandidateTuple> node = graph.getNode(fct);
		TreeSet<Fragment> connectingFragments = new TreeSet<Fragment>(new Comparator<Fragment>() {
			@Override
			public int compare(Fragment f1, Fragment f2) {
				if(f1 == f2) return 0;
				else if(f1.start - f2.start != 0){
					return f1.start - f2.start;
				}else return f1.stop - f2.stop;
			}
		});
		for (GraphEdge<FragmentCandidateTuple> edge : node.incomingEdges) {
			connectingFragments.add(edge.source.content.fragment);
		}
		for (GraphEdge<FragmentCandidateTuple> edge: node.outgoingEdges){
			connectingFragments.add(edge.sink.content.fragment);
		}
		//TODO: check division by 0?
		double weight = (double)connectingFragments.size() / (double)(numberOfFragments - 1);
		return weight;
	}
	
	@Override
	public void evaluate(AnnotatedSentence annotatedSentence) {
		System.out.println("Starting evaluation... ");
		Stopwatch stopwatch = new Stopwatch(Stopwatch.UNIT.SECONDS);
		LinkedList<Fragment> fragmentList = annotatedSentence.buildFragmentList();
		numberOfFragments = fragmentList.size();
		// add fragments/candidates to graph
		graph = new Graph<FragmentCandidateTuple>();
		for(Fragment fragment: fragmentList){
			for(String candidate: fragment.candidates){
				graph.addNode(new FragmentCandidateTuple(candidate, fragment));
			}
		}
		
		System.out.println(graph.nodeMap.size() + " graph nodes added. " + stopwatch.stop() + " s");
		stopwatch.start();
		// build edges
		Stopwatch sw = new Stopwatch(Stopwatch.UNIT.MILLISECONDS);
		for(Node<FragmentCandidateTuple> nodeSource: graph.nodeMap.values()){
			sw.start();
			TreeSet<String> semSig = new TreeSet<String>();
			//long start = System.nanoTime();
			LinkedList<String> tmp = semanticSignatureDB.getFragmentTargets(nodeSource.content.candidate);
			//long end = System.nanoTime();
			//double passedTime = (end - start) / 1000000000.0;
			//System.out.println("Passed time: " + passedTime + " s");
			
			for (String s : tmp) {
				semSig.add(s);
			}
			sw.stop();
			lookUpTime += sw.doubleTime;
			
			sw.start();
			for(Node<FragmentCandidateTuple> nodeSink: graph.nodeMap.values()){
				// if fragment is the same, skip
				if(nodeSource.content.fragment == nodeSink.content.fragment) continue;
				
				if(semSig.contains(nodeSink.content.candidate)){ // conditions fullfilled, build edge
					//System.out.println("	Adding edge: " + nodeSource.content.candidate + " --> " + nodeSink.content.candidate);
					graph.addEdge(nodeSource, nodeSink);
				}
			}
			sw.stop();
			searchSetTime += sw.doubleTime;
		}
		System.out.println("Graph edges added. Time: " + stopwatch.stop() + " s");
		
		// Trim Graph
		stopwatch.start();
		trimToDenseSubgraph(graph);
		scoreAllFragments();
		System.out.println("Graph trimmed. Time: " + stopwatch.stop() + " s");
		
		HashMap<Fragment, Double> scoreMap = new HashMap<Fragment, Double>();
		for(Node<FragmentCandidateTuple> node: graph.nodeMap.values()){
			Double tmp = scoreMap.get(node.content.fragment);
			if(tmp == null || tmp < node.content.score){
				node.content.fragment.probability = node.content.score;
				node.content.fragment.setID(node.content.candidate);
				scoreMap.put(node.content.fragment, node.content.score);
			}
		}
		
		annotatedSentence.assign(minimumScore);
		
		if(semanticSignatureDB instanceof H2Connector){
			for(Word w: annotatedSentence.getWordList()){
				Fragment f = w.getDominantFragment();
				if(f != null){
					String tmp = f.getID();
					tmp = ((H2Connector)semanticSignatureDB).resolveID(tmp);
					f.setEntity(tmp);
				}
			}
		}
		
		System.out.println("	Evaluation complete.");
	}

}
