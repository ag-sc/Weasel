package datatypes;

import java.util.LinkedList;

public class AnnotatedSentenceDeprecated {
	LinkedList<String> tokens;
	LinkedList<String> entities;

	public AnnotatedSentenceDeprecated(String sentence){
		this();
		for(String word: sentence.split(" ")){
			addToken(word);
		}
	}
	
	public AnnotatedSentenceDeprecated() {
		tokens = new LinkedList<String>();
		entities = new LinkedList<String>();
	}
	
	public int addToken(String token){
		tokens.add(token);
		entities.add("");
		return tokens.lastIndexOf(token);
	}
	
	public void setEntity(int index, String entity){
		entities.set(index, entity);
	}
	
	public String getToken(int index){
		return tokens.get(index);
	}
	
	public String getEntity(int index){
		return entities.get(index);
	}
	
	public String getSentence(){
		String s = "";
		for(String word: tokens) s += word + " ";
		return s.trim();
	}
	
	public int length(){
		return tokens.size();
	}
	
	public String toString(){
		String s = "";
		for(int i = 0; i < length(); i++){
			s += tokens.get(i) + "\t" + entities.get(i) + "\n";
		}
		return s;
	}

}
