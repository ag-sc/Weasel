package datatypes;

public class TFIDFResult implements Comparable<TFIDFResult> {

	public String token;
	public Integer nrDocuments;
	public Integer tf;
	public Integer df;
	public Float idf;
	public Float tfidf;
	
	
	public TFIDFResult(String token, int nrDocuments, Integer tf, Integer df) {
		if(tf == null) tf = 0;
		if(df == null) df = 0;
		
		this.token = token;
		this.tf = tf;
		this.df = df;
		this.nrDocuments = nrDocuments;
		
		idf = (float)Math.log( ((double) nrDocuments) / this.df.doubleValue());
		tfidf = tf * idf;
	}
	
	public String toString(){
		return token + "\t" + tf  + "\t" + df + "\t" + idf + "\t" + tfidf;
	}

	@Override
	public int compareTo(TFIDFResult o) {
		return tfidf.compareTo(o.tfidf);
	}

}
