package main.java.datatypes.configuration;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

import weka.classifiers.Classifier;


public class Config {
	
		private Map<String, String> parameters;
		public static Property datasetEntityProp = ResourceFactory.createProperty("V-SEL:", "datasetEntity");
		
        // Private constructor. Prevents instantiation from other classes.
        private Config() {
        	parameters = new HashMap<String, String>();
        }
 
        private static class SingletonHolder {
                private static final Config INSTANCE = new Config();
        }
 
        public static Config getInstance() {
                return SingletonHolder.INSTANCE;
        }
        
        public void addParameter(String key, String value){
        	parameters.put(key, value);
        }
        
        public String getParameter(String parameter){
        	String s = parameters.get(parameter);
        	if(s != null) return s;
        	else{
        		System.err.println("No entry found for parameter '" + parameter + "'");
        		throw new IllegalArgumentException();
        	}
        }
        
        public void setParameter(String key, String value){
        	parameters.put(key, value);
        }
}