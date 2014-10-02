package datatypes;

import java.util.TreeSet;

public class H2List {

	private static String delimiter = " ";
	
	public static TreeSet<String> stringToSet(String sqlList){
		TreeSet<String> set = new TreeSet<String>();
		for(String s: sqlList.split(delimiter)) set.add(s);
		return set;
	}
	
	public static String setToString(TreeSet<String> set){
		StringBuilder builder = new StringBuilder();
		for(String s: set){
			builder.append(s);
			builder.append(delimiter);
		}
		return builder.toString();
	}

}
