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

	public void parse(InputStream fileInputStream){
		Properties p = new Properties();
		try {
			p.load(fileInputStream);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		for(Entry<Object, Object> e: p.entrySet()){
			config.addParameter((String) e.getKey(), (String) e.getValue());
		}
	}
	
	public void parse(String iniFilePath){
		InputStream input = null;
		try {
			input = new FileInputStream(iniFilePath);
			parse(input);
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
