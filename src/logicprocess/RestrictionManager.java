package logicprocess;

import java.util.ArrayList;

public class RestrictionManager {
	ArrayList<RestrictionSequence> restrictions;
	
	public RestrictionManager() {
		restrictions = new ArrayList<>();
	}
	
	public void addRestriction(RestrictionSequence restriction){
		restrictions.add(restriction);
	}
	
	public ArrayList<RestrictionSequence> getRestrictionSet(){
		return restrictions;
	}
}
