package neo4j;

import java.io.IOException;
import java.util.TreeSet;

import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.RelationshipType;

import fileparser.AnchorFileParser;
import fileparser.StopWordParser;

public class Neo4jAnchorBuilder extends Neo4jDatabaseBuilder{

	Label anchorLabel = DynamicLabel.label( "Anchor" );
	Label partialAnchorLabel = DynamicLabel.label( "PartialAnchor" );
	
	public Neo4jAnchorBuilder(String dbPath) {
		super(dbPath);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void _run(String[] args) {
		int nrAnchor = 0, nrAdditional = 0;
		
		long localstart = System.nanoTime();
		TreeSet<String> stopWords = new TreeSet<String>();
		try {
			stopWords = StopWordParser.parseStopwords("../../data/stopwords.txt");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		try{
			int lineCounter = 0;
			AnchorFileParser anchorParser = new AnchorFileParser(args[0]);
			while((tuple = anchorParser.parseTuple()) != null){
				lineCounter++;
				//addRelation(tuple, anchorLabel, entityLabel, RelTypes.ANCHOR);
				nrAnchor++;
				
				String[] splitAnchor = tuple[0].split(" ");
				if(splitAnchor.length > 1){
					String[] newTuple = new String[2];
					newTuple[1] = tuple[0];
					for(String partialAnchor: splitAnchor){
						newTuple[0] = partialAnchor;
						if(!stopWords.contains(partialAnchor.toLowerCase())) {
							nrAdditional++;
							//addRelation(newTuple, partialAnchorLabel, anchorLabel, RelTypes.PARTIALMATCH);
						}
					}
				}
				
				if(lineCounter % 100000 == 0) System.out.println("	processed lines: "+ lineCounter);
			}
		}catch (IOException e){
			e.printStackTrace();
			return;
		}
		
		long localend = System.nanoTime();
		double passedTime = (localend - localstart) / 60000000000.0;
		System.out.println("Total time: " + passedTime + " min");
		System.out.println("nr of nodes: " + nrOfNodes + " - nrOfEdges: " + nrOfEdges);
		System.out.println("nrAnchor: " + nrAnchor + " - nrAdditional: " + nrAdditional);
	}
	
	protected void addRelation(String[] tuple, Label labelSource, Label labelSink, RelationshipType relType){
		//if(!treeSet.contains(tuple[0]) && !treeSet.contains(tuple[1])) return;
		nrOfEdges++;
		
		Node sourceNode = null;
		for ( Node node : graphDB.findNodesByLabelAndProperty( labelSource, "name", tuple[0] ) ){
			sourceNode = node;
		}
		if(sourceNode == null){
			sourceNode = graphDB.createNode(labelSource);
			sourceNode.setProperty("name", tuple[0]);
			nrOfNodes++;
		}
		
		Node sinkNode = null;
		for ( Node node : graphDB.findNodesByLabelAndProperty( labelSink, "name", tuple[1] ) ){
			sinkNode = node;
		}
		if(sinkNode == null){
			sinkNode = graphDB.createNode(labelSink);
			sinkNode.setProperty("name", tuple[1]);
			nrOfNodes++;
		}
		
		if(!sourceNode.equals(sinkNode)) sourceNode.createRelationshipTo(sinkNode, relType);
		
		changeCounter++;
		checkTransactions();
	}
	
	public static void main(String[] args) {
		Neo4jAnchorBuilder builder = new Neo4jAnchorBuilder("../../data/DBs/AnchorsTest");
		String[] tmp = new String[1];
		tmp[0] = "../../data/Wikipedia Anchor/anchors.txt";
		builder.run(tmp);
	}
}






