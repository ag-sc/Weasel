package main.java.executable;

import java.util.ArrayList;

import jena.schemagen;

public class GenerateSchemaGens {

	public static void main(String[] args) {
		// NIF
		ArrayList<String> argsArray = new ArrayList<String>();
		argsArray.add("-i");
		argsArray.add("E:/Master Project/jena schemagen/nif-core_2.owl");
		
		argsArray.add("-n");
		argsArray.add("NIF");
		
		argsArray.add("-o");
		argsArray.add("E:/Master Project/jena schemagen/");
		
		argsArray.add("--owl");
		
		String[] args2 = argsArray.toArray(new String[argsArray.size()]);
		schemagen.main(args2);

		// ITS
		argsArray = new ArrayList<String>();
		argsArray.add("-i");
		argsArray.add("E:/Master Project/jena schemagen/its-rdf.rdf");
		
		argsArray.add("-n");
		argsArray.add("ITSRDF");
		
		argsArray.add("-o");
		argsArray.add("E:/Master Project/jena schemagen/");
		
		argsArray.add("--owl");
		
		args2 = argsArray.toArray(new String[argsArray.size()]);
		schemagen.main(args2);
	}

}
