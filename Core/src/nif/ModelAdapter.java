package nif;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

import datatypes.configuration.Config;

public abstract class ModelAdapter {
	
	protected boolean useURLEncoding = true;
	
	public ModelAdapter(){
		Config config = Config.getInstance();
		this.useURLEncoding = Boolean.parseBoolean(Config.getInstance().getParameter("useURLEncoding"));
	}

	public void linkModel(Model model){
		StmtIterator iter = model.listStatements(new SimpleSelector(null, null, (RDFNode) NIF_SchemaGen.Context));

		// For all resources labled "context"
		while (iter.hasNext()) {
			Statement stmt = iter.nextStatement(); // get next statement
			Resource originResource = stmt.getSubject();

			String originSentence = originResource.getProperty(NIF_SchemaGen.isString).getLiteral().toString().split("\\^\\^")[0];
			if (originSentence == null)
				continue;
			System.out.println("Origin Sentence: " + originSentence);
			innerLoop(model, stmt, originSentence, originResource);
		}
	}
	
	protected abstract void innerLoop(Model model, Statement stmt, String originSentence, Resource originResource);
}
