package configuration;

import java.util.HashMap;
import java.util.Map;


public class Config {
	
		private Map<String, String> parameters;
		public HashMap<Integer, Integer> hackMap; //hack
		
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
}