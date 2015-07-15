package main.java.datatypes;

public class SortableAssociate<C extends Comparable<C>, O> implements Comparable<SortableAssociate<C, O>>{

	C comparable;
	O object;
	
	public SortableAssociate(C comparable, O object){
		this.comparable = comparable;
		this.object = object;
	}
	
	public C getComparablePart(){
		return comparable;
	}
	
	public O getObjectPart(){
		return object;
	}
	
	@Override
	public int compareTo(SortableAssociate<C, O> o) {
		return comparable.compareTo(o.getComparablePart());
	}

}
