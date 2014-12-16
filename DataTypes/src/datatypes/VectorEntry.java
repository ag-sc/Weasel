package datatypes;

import java.io.Serializable;

public final class VectorEntry implements Serializable{
	private static final long serialVersionUID = -7333749930975596124L;

	public final double pagerank = 0.0;
	
	public final int semSigVector[] = new int[100];
	public final int semSigCount[] = new int[100];
	
	public final int tfVector[] = new int[100];
	public final float tfScore[] = new float[100];
	
	public VectorEntry(){
		for(int i = 0; i < semSigVector.length; i++) semSigVector[i] = -1; // valid entries are >= 0
		for(int i = 0; i < tfVector.length; i++) tfVector[i] = -1;
	}
}
