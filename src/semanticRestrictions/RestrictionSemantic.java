package semanticRestrictions;

import java.util.ArrayList;

import logicprocess.RestrictionSequence;

public abstract class RestrictionSemantic {
	
	RestrictionSequence restrictionSequence;
	
	public abstract RestrictionSequence getRestrictionSequence();
	public abstract String getItem();
	
	public abstract String getRelationName();
	
	public boolean execute(ArrayList<String> sequence){
		return false;
	}
}
