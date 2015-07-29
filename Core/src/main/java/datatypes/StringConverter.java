package main.java.datatypes;
import java.io.UnsupportedEncodingException;


public class StringConverter {

	public StringConverter() {
		// TODO Auto-generated constructor stub
	}
	
	public static String convert(String input, String charset) throws UnsupportedEncodingException{
		StringBuilder sb = new StringBuilder();
		byte[] array = input.getBytes(charset);
		for(byte b: array){
			if((b & 0xFF) > 0x7f){ // is special character
				sb.append("%" + Integer.toHexString(b & 0xFF).toUpperCase());
			}else{
				sb.append((char) b);
			}
			
		}
		
		return sb.toString();
	}

	public static void main(String[] args) throws UnsupportedEncodingException{
		String tmp = StringConverter.convert("Hello World", "UTF-8");
		System.out.println(tmp);
	}
}
