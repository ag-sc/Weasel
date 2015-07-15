package main.java.datatypes;

import java.io.Serializable;

public class Tuple<X extends Serializable, Y extends Serializable> implements Serializable { 
	private static final long serialVersionUID = 1210034881410143629L;
	public final X x; 
	  public final Y y; 
	  public Tuple(X x, Y y) { 
	    this.x = x; 
	    this.y = y; 
	  } 
	} 
