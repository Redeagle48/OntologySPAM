package logicprocess;

import java.util.ArrayList;


/****************
 * Class representing the restriction being analyzed
 * @author antoniopereira
 *
 */

public class RestrictionSequence {

	final int ID;
	ArrayList<String> items;
	String sequenceName;
	boolean hasRoot;
	boolean hasLeaf;
	
	public RestrictionSequence(String sequence) {
		ID = 1;
		hasRoot = false;
		hasLeaf = false;
		items = new ArrayList<String>();
		this.sequenceName = sequence;
	}
	
	public String getSequenceName() {
		return sequenceName;
	}

	public void setSequenceName(String sequence) {
		this.sequenceName = sequence;
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
