package datatypes;

import java.io.Serializable;
import java.util.TreeSet;

public class DBEntry<T> implements Serializable, Comparable<DBEntry<T>>{

	private static final long serialVersionUID = -7579575003883525000L;
	String key;
	TreeSet<T> values;
	
	public DBEntry(String key, TreeSet<T> values){
		this.key = key;
		this.values = values;
	}

	@Override
	public int compareTo(DBEntry<T> otherEntry) {
		return key.compareToIgnoreCase(otherEntry.key);
	}
}
