package iniloader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map.Entry;
import java.util.Properties;

import datatypes.configuration.Config;

public class IniLoader {

	private Config config;
	
	public IniLoader() {
		config = Config.getInstance();
	}

	
	public void parse(String iniFilePath){
		InputStream input = null;
		try {
			input = new FileInputStream(iniFilePath);
			Properties p = new Properties();
			p.load(input);
			for(Entry<Object, Object> e: p.entrySet()){
				config.addParameter((String) e.getKey(), (String) e.getValue());
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
			System.exit(-1);
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException ioe) {
					ioe.printStackTrace();
				}
			}
		}
	}
}
