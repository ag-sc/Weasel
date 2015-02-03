package datasetParser;

import java.io.IOException;
import java.util.HashSet;

import configuration.Config;
import databaseConnectors.DatabaseConnector;
import datatypes.AnnotatedSentenceDeprecated;

public abstract class DatasetParser {
	
	public abstract AnnotatedSentenceDeprecated parse() throws IOException;
	public abstract HashSet<Integer> getEntitiesInDocument(DatabaseConnector entityDBconnector);
	
	public static DatasetParser getInstance(){
		Config config = Config.getInstance();
		String datasetType = config.getParameter("datasetType");
		switch(datasetType){
			case "KORE50":
				return new KORE50Parser();
		}
		throw new IllegalArgumentException();
	}
	
	
}
