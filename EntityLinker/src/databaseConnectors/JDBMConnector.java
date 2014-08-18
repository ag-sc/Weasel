package databaseConnectors;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Map.Entry;

import datatypes.TermFrequency;
import jdbm.PrimaryHashMap;
import jdbm.RecordManager;
import jdbm.RecordManagerFactory;

public class JDBMConnector extends DatabaseConnector{
	private RecordManager recman;
	PrimaryHashMap<String, LinkedList<TermFrequency>> dbMap;
	
	public JDBMConnector(String dbName, String tableName) throws IOException{
		recman = RecordManagerFactory.createRecordManager(dbName);
		dbMap = recman.hashMap(tableName);
	}
	
	public void close(){
		try {
			recman.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public LinkedList<String> lookUpFragment(String fragment) {
		LinkedList<String> list = new LinkedList<String>();
		
		LinkedList<TermFrequency> tmp = dbMap.get(fragment);
		if(tmp != null){
			for(TermFrequency t: tmp){
				list.add(new String(t.term));
			}
		}
		
		for(Entry<String, LinkedList<TermFrequency>> e: dbMap.entrySet()){
			System.out.println(e.getKey());
		}
		
		return list;
	}

}
