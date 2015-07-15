package main.java.datatypes;

import java.util.LinkedList;
import java.util.TreeSet;

public class H2List {

	public static final String delimiter = " ";
	
	public static TreeSet<String> stringToSet(String sqlList){
		TreeSet<String> set = new TreeSet<String>();
		if(sqlList != null) for(String s: sqlList.split(delimiter)) set.add(s);
		return set;
	}
	
	public static LinkedList<String> stringToList(String sqlList){
		LinkedList<String> list = new LinkedList<String>();
		if(sqlList != null) for(String s: sqlList.split(delimiter)) list.add(s);
		return list;
	}
	
	public static String setToString(TreeSet<String> set){
		StringBuilder builder = new StringBuilder();
		for(String s: set){
			builder.append(s);
			builder.append(delimiter);
		}
		return builder.toString();
	}
	
	public static String listToString(LinkedList<String> list){
		StringBuilder builder = new StringBuilder();
		for(String s: list){
			builder.append(s);
			builder.append(delimiter);
		}
		return builder.toString();
	}

}
