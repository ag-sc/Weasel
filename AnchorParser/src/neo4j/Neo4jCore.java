package neo4j;

import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.RelationshipType;

public class Neo4jCore {
	
	public static final Label entityLabel = DynamicLabel.label( "Entity" );
	public static final Label anchorLabel = DynamicLabel.label( "Anchor" );
	public static final Label partialAnchorLabel = DynamicLabel.label( "PartialAnchor" );
	public static final Label wikiLinkLabel = DynamicLabel.label( "WikiLink" );
	
	public static enum RelTypes implements RelationshipType {
		SEMANTIC_SIGNATURE_OF,
		ANCHOR_OF,
		PARTIALMATCH_OF,
		LINK_TO
	}
	
	public static void registerShutdownHook(final GraphDatabaseService graphDb) {
		// Registers a shutdown hook for the Neo4j instance so that it
		// shuts down nicely when the VM exits (even if you "Ctrl-C" the
		// running application).
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				graphDb.shutdown();
			}
		});
	}
}
