package nif;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Selector;
import com.hp.hpl.jena.rdf.model.Statement;

public class IsStringSelector implements Selector {

	@Override
	public boolean test(Statement s) {
		if(s.getPredicate().equals(NIF_SchemaGen.isString)) return true;
		return false;
	}

	@Override
	public boolean isSimple() {
		return true;
	}

	@Override
	public Resource getSubject() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Property getPredicate() {
		return NIF_SchemaGen.isString;
	}

	@Override
	public RDFNode getObject() {
		return null;
	}

}
