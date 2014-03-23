package semanticRestrictions;

import java.util.ArrayList;

import logicprocess.RestrictionSequence;

public class Restriction_hasFirstItem extends RestrictionSemantic {
	
	String item;
	
	public Restriction_hasFirstItem(String item, RestrictionSequence restrictionSequence) {
		this.item = item;
		super.restrictionSequence = restrictionSequence;
	}

	@Override
	public RestrictionSequence getRestrictionSequence() {
		return restrictionSequence;
	}

	@Override
	public String getItem() {
		return item;
	}

	@Override
	public boolean execute(ArrayList<String> sequence){
		return true;
	}

	@Override
	public String getRelationName() {
		return "hasFirstItem";
	}
	
}
