package datatypes;

import java.io.Serializable;

public class Tuple<X, Y> implements Serializable {
	private static final long serialVersionUID = 8551637199843321885L;
	public X item1;
	public Y item2;
	
	public Tuple(X item1, Y item2){
		this.item1 = item1;
		this.item2 = item2;
	}
}
