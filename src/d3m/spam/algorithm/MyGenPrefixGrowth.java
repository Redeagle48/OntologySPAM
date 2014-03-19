package d3m.spam.algorithm;

import java.util.Vector;

import d3m.spam.constraints.relaxations.Relaxation;
import d3m.spam.core.Database;
import d3m.spam.core.Element;
import d3m.spam.core.EventSequence;
import d3m.spam.core.EventSequencesReader;
import d3m.spam.core.ProjectedDB;

public class MyGenPrefixGrowth extends SpamAlgorithm {
	
	protected final int DISCOVER_L1 = 1;

	/** Creates a new empty instance of GenPrefixSpan */	
	public MyGenPrefixGrowth()
	{    super();	}

	/** 
	 * Creates a new instance of GenPrefixSpan and initializes it with user parameters.
	 * @param reader The reader created to read a file with sequences.
	 * @param tdmRelax The Spam constraint to apply to the mining process.
	 * @param on A flag for profiling.
	 */
	public MyGenPrefixGrowth(EventSequencesReader reader, Relaxation tdmRelax, boolean on)
	{    super(reader, tdmRelax, on);	}

	/** 
	 * Creates a new instance of PrefixSpan and initializes it with user parameters.
	 * @param db The database with sequences
	 * @param tdmRelax The Spam constraint to apply to the mining process.
	 * @param on A flag for profiling.
	 */
	public MyGenPrefixGrowth(Database db, Relaxation tdmRelax, boolean on)
	{    super(db, tdmRelax, on);	}
	
	/**
	 * Runs the algorithm to find the large itemsets.
	 * @return return a vector with frequent patterns in the first element, 
	 *          and the execution time in the second element.
	 */
	@Override
	public Vector<EventSequence> run() {
		
		long time = 0;
	    if (m_profileOn) time = System.currentTimeMillis();
		
		Vector<Element> alphabet = m_tdmRelax.getCandidates();
		int arsize = alphabet.size();
		int[] sup = new int[arsize];
		ProjectedDB[] projDbs = new ProjectedDB[arsize];
		for (int i=0; i<arsize; i++){
	 		sup[i] = 0;
//			projDbs[i] = new ProjectedDB(m_db.getSize()*m_db.getMaxLength());
			//PARA POUPAR MEMORIA
//			projDbs[i] = new ProjectedDB(m_db.getSize()*m_db.getLengthMode());
			projDbs[i] = new ProjectedDB(m_db.getSize()*5);
		}
		
		Vector<Element> f_list = discoverL1(alphabet, projDbs, sup);
		
		return null;
	}
	
	/**
	 * Filters the elements that satisfy the temporal constraint.
	 * @param alphabet
	 * @param projDbs
	 * @param sup
	 * @return A vector with frequent elements, ordered by their support.
	 */
	protected Vector<Element> discoverL1(Vector<Element> alphabet, ProjectedDB[] projDbs, 
			int[] sup)
	{
	    long time = 0;
	    if (m_profileOn) time = System.currentTimeMillis();
	    // Discover temporally and alphabetically accepted elements   
		for (int i=0; i<m_db.getSize(); i++) {
			if (m_tdmRelax.verifyGeneralAcceptance(m_db.elementAt(i),alphabet,sup, projDbs, i))
				m_nrOfSupportSequences++;
		}
		// Now, that the number of m_nrOfSupportSequences is known, it is set in
		// the existential constraint
		m_tdmRelax.m_tdm.m_existC.setSupportGlobalNr(m_nrOfSupportSequences);
	    @SuppressWarnings("unchecked")
		Vector<Element> vec = (Vector<Element>)alphabet.clone();
	//for (int i=0; i<vec.size(); i++)
	// System.out.println(((Element)vec.elementAt(i)).toString()+":"+String.valueOf(sup[i]));
		m_tdmRelax.m_tdm.getSortedFrequentElements(vec, sup, projDbs);
		if (m_profileOn) {
	        Long old = m_membersProfiling.elementAt(DISCOVER_L1);
	        m_membersProfiling.setElementAt(new Long(old.longValue()+System.currentTimeMillis()-time), DISCOVER_L1);
	    }
		return vec;
	}
}
