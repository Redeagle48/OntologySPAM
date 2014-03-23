package semanticRestrictions;

import logicprocess.RestrictionSequence;

public class Restriction_hasLast extends RestrictionSemantic {
	String item;
	
	public Restriction_hasLast(String item, RestrictionSequence restrictionSequence) {
		this.item = item;
		super.restrictionSequence = restrictionSequence;
	}

	@Override
	public String getItem() {
		return item;
	}

	@Override
	public RestrictionSequence getRestrictionSequence() {
		return super.restrictionSequence;
	}
}
