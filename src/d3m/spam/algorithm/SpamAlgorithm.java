/*------------------------------------------------------------------
--               PACKAGE - SEQUENTIAL PATTERN MINING              --
--                               CORE                             --
--                                                                --
--                       Claudia M. Antunes                       --
--						    November 2003                         --
--------------------------------------------------------------------
--                         Algorithm.java                         --
--------------------------------------------------------------------*/
package d3m.spam.algorithm;

import d3m.spam.core.*;
import d3m.spam.constraints.relaxations.*;
import java.util.*;

//--------------------------------------------------------------------
//                         CLASS Algorithm
//--------------------------------------------------------------------
/** Class <CODE><I>Algorithm</I></CODE> implements the basic structure for the
 * algorithms to Mining Sequential Patterns"
 * @version 1.0
 * @author Claudia M. Antunes
*/
public abstract class SpamAlgorithm
{
    /** The Spam constraint to apply to the mining process. */
    protected Relaxation m_tdmRelax;
    /** The read Sequences. */
    protected Database m_db;
    /** The large itemsets or frequent sequences. */
    protected Vector<EventSequence> m_litemsets;
    /** The number of discovered patterns. */
    protected int m_nrOfPatterns;
    /** The number of customer sequences that satisfy the temporal constraint. */
    protected int m_nrOfSupportSequences;
/**................................................................
 *		Members for profiling
 */
    protected boolean m_profileOn = true;
    /** A vector with the name of methods to be profiled. */
    protected Vector<String> m_membersNameProfiling;
    /** A vector with the time spent with the correspondent method. */
    protected Vector<Long> m_membersProfiling;
    /**     */
    protected final int RUN = 0;  
//.................................................................
/** Creates a new instance of Algorithm */
public SpamAlgorithm()
{
    m_db = new Database();
    m_litemsets = new Vector<EventSequence>();
	m_nrOfSupportSequences = 0;
    m_litemsets.trimToSize();
    m_tdmRelax = null;
    m_profileOn = false;
    if (m_profileOn) initializeProfiling();
}
//________________________________________________________________
/**
 * Creates a new instance of Algorithm and initializes it with user parameters.
 * @param reader The reader created to read a file with sequences.
 * @param tdmRelax The Spam constraint to apply to the mining process.
 * @param on A flag for profiling.
 */
public SpamAlgorithm (EventSequencesReader reader, Relaxation tdmRelax, boolean on)
{
    m_litemsets = new Vector<EventSequence>(0);
    m_profileOn = on;
	m_nrOfSupportSequences = 0;
    if (m_profileOn) initializeProfiling();
    try {
        m_db = reader.getSequences();
        m_tdmRelax = tdmRelax;
    }
    catch (Exception ex)
    {	ex.printStackTrace();	}
}
//________________________________________________________________
/**
 * Creates a new instance of Algorithm and initializes it with user parameters.
 *
 * @param db The database with sequences
 * @param tdmRelax The Spam constraint to apply to the mining process.
 * @param on A flag for profiling.
 */
public SpamAlgorithm (Database db, Relaxation tdmRelax, boolean on)
{
    m_profileOn = on;
    m_db = db;
    m_tdmRelax = tdmRelax;
	m_nrOfSupportSequences = 0;
    m_litemsets = new Vector<EventSequence>(0);
    if (m_profileOn) initializeProfiling();
}

/**
 * 
 */
protected void initializeProfiling()
{
    m_membersNameProfiling = new Vector<String>();
    m_membersNameProfiling.addElement(new String("run"));
    m_membersProfiling = new Vector<Long>();
    for (int i=0;i<1; i++)
        m_membersProfiling.addElement(new Long(0));
}

/**Runs the algorithm to find the large itemsets.
 * @return Returns a vector with frequent patterns in the first element,
 *          and the execution time in the second element.
 */
public abstract Vector<EventSequence> run();

/**
 * 
 * TODO Put here a description of what this method does.
 *
 * @param L
 * @return the list of valid sequences
 */
protected Vector<EventSequence> filter(Vector<EventSequence> L)
{
    Vector<EventSequence> F = new Vector<EventSequence>();
    if (L.size()>0){
	    for (int i=0; i<L.size(); i++){
			EventSequence s = L.elementAt(i);
			int j=0;
			boolean ok = false;
			while (!ok && j<L.size()){
				if (j!=i) {
					EventSequence ss = L.elementAt(j);
					ok = (-1!=s.isContainedIn(ss, 0, 0, s.length()));
				}
				j++;
			}
			if (!ok){
//System.out.println(s);
				F.addElement(s);
			}
		}
    }
    return F;
}

/** 
 * Calls the run method and measures the time and memory wasted. 
 * @return a string with results
 */
public Vector<String> exec()
{
    System.out.println("Algoritmo -> "+getClass().getName()
                      +" DBsize="+String.valueOf(m_db.getSize())
                      +" Constraint="+m_tdmRelax.toString());
    long initialTime = System.currentTimeMillis();
    Vector<EventSequence> L = run();
    String st_alg = getClass().getName()+"==";
    String st_resume = "=time=Lk";
    String st_results = "="+String.valueOf((System.currentTimeMillis()-initialTime))
                      + "=" + String.valueOf(m_nrOfPatterns);
    if (m_profileOn)
    {
        for (int i=0; i<m_membersNameProfiling.size(); i++)
        {
            st_alg += "=";
            st_resume += "=" +m_membersNameProfiling.elementAt(i);
            st_results += "=" +(m_membersProfiling.elementAt(i)).toString();
        }
    }
    System.out.println(L);
    Vector<String> vec = new Vector<String>(4);
    vec.addElement(st_alg);
    vec.addElement(st_resume);
    vec.addElement(st_results);
    vec.addElement(L.toString());
    return vec;
}

}
