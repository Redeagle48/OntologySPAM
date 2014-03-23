package semanticRestrictions;

import logicprocess.RestrictionSequence;

public abstract class RestrictionSemantic {
	
	RestrictionSequence restrictionSequence;
	
	public abstract RestrictionSequence getRestrictionSequence();
	public abstract String getItem();
}
