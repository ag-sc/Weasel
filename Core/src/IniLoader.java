import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map.Entry;

import org.ini4j.Ini;
import org.ini4j.Profile.Section;

import configuration.Config;

public class IniLoader {

	private Ini ini = new Ini();
	private Config config;
	
	public IniLoader() {
		InputStream input = null;
		try {
			input = new FileInputStream("../config.ini");
			this.ini.load(input);
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
		config = Config.getInstance();
	}

	
	public void parse(){
		for(Entry<String, Section> section: ini.entrySet()){
			for(Entry<String, String> parameter: section.getValue().entrySet()){
				config.addParameter(parameter.getKey(), parameter.getValue());
			}
		}
	}
}
