package semanticRestrictions;

import logicprocess.RestrictionSequence;

public class Restriction_hasFirst extends RestrictionSemantic {
	
	String item;
	//RestrictionSequence restrictionSequence;
	
	public Restriction_hasFirst(String item, RestrictionSequence restrictionSequence) {
		this.item = item;
		//this.restrictionSequence = restrictionSequence;
		super.restrictionSequence = restrictionSequence;
	}
	
	public String getItem() {
		return item;
	}
	
	public RestrictionSequence getRestrictionSequence() {
		return super.restrictionSequence;
	}
}
