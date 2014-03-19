package processrestrictionselements;

import logicprocess.OntologyHolder;
import logicprocess.RestrictionSequence;

import org.w3c.dom.Node;

public abstract class ProcessRestrictionElements {
	
	OntologyHolder ontologyHolder;
	
	public abstract void proceed(Node node, RestrictionSequence restrictionSequence);
}
