package logicprocess;

import java.util.ArrayList;

import semanticRestrictions.RestrictionSemantic;


/****************
 * Class representing the restriction being analyzed
 * @author antoniopereira
 *
 */

public class RestrictionSequence {

	final int ID;
	String sequenceName;
	ArrayList<String> items;
	ArrayList<RestrictionSemantic> relations;
	boolean hasRoot;
	boolean hasLeaf;
	
	public RestrictionSequence(String sequence) {
		ID = 1;
		this.sequenceName = sequence;
		relations = new ArrayList<RestrictionSemantic>();
		items = new ArrayList<String>();
	}
	
	public String getSequenceName() {
		return sequenceName;
	}

	public void setSequenceName(String sequence) {
		this.sequenceName = sequence;
	}
	
	public void addRelation(RestrictionSemantic restricitonSemantic){
		relations.add(restricitonSemantic);
	}
	
	public ArrayList<RestrictionSemantic> getRelations(){
		return relations;
	}
	
	public void insertItem(String item){
		items.add(item);
	}
	
	public boolean existsRoot () {
		return this.hasRoot;
	}
	
	public void insertRoot() {
		hasRoot = true;
	}
	
	public boolean existsLeaf () {
		return this.hasLeaf;
	}
	
	public void insertLeaf() {
		hasLeaf = true;
	}
}
