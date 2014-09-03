package neo4j;

import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.RelationshipType;

public class Neo4jCore {
	
	Label entityLabel = DynamicLabel.label( "Entity" );
	Label anchorLabel = DynamicLabel.label( "Anchor" );
	Label partialAnchorLabel = DynamicLabel.label( "PartialAnchor" );
	
	public static enum RelTypes implements RelationshipType {
		SEMANTIC_SIGNATURE_OF,
		ANCHOR_OF,
		PARTIALMATCH_OF
	}
}
