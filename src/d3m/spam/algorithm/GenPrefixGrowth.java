/*-------------------------------------------------------------
--             PACKAGE - SEQUENTIAL PATTERN MINING           --
--                       PATTERN GROWTH                      --
--                                                           --
--                       Claudia M. Antunes                  --
--                           March 2003                      --
---------------------------------------------------------------
--                      GenPrefixGrowth.java                 --
--------------------------------------------------------------*/
package d3m.spam.algorithm;

import d3m.spam.core.*;
import d3m.spam.constraints.relaxations.*;
import ontologies.timeOntology.*;
import java.util.*;

//--------------------------------------------------------------------
//                   CLASS GenPrefixGrowth
//--------------------------------------------------------------------
/** Class <CODE>GenPrefixSpan</CODE> implements the GenPrefixSpan algorithm described 
 * in <B>"Generalization of Pattern Growth Methods to Use Gap Constraints"</B> by 
 * Claudia Antunes [MLDM 2003].
 * @version 1.0
 * @author Claudia M. Antunes 
 */
public class GenPrefixGrowth extends SpamAlgorithm
{
	/** */
    protected final int DISCOVER_L1 = 1;
	/** */
    protected final int DISCOVER_NEXT = 2;
	/** */
	protected final int CREATE_PROJECTED_DB = 3;
	/** */
	protected final int VERIFY_GENERAL_ACC = 4;
	/** */
	protected final int VERIFY_ACC = 5;
	/** */
	protected final int SIMULATION = 6;
	/** */
	protected final int SORT = 7;
	/** */
	protected final int ACCEPTS = 8;

/** Creates a new empty instance of GenPrefixSpan */	
public GenPrefixGrowth()
{    super();	}

/** 
 * Creates a new instance of GenPrefixSpan and initializes it with user parameters.
 * @param reader The reader created to read a file with sequences.
 * @param tdmRelax The Spam constraint to apply to the mining process.
 * @param on A flag for profiling.
 */
public GenPrefixGrowth(EventSequencesReader reader, Relaxation tdmRelax, boolean on)
{    super(reader, tdmRelax, on);	}

/** 
 * Creates a new instance of PrefixSpan and initializes it with user parameters.
 * @param db The database with sequences
 * @param tdmRelax The Spam constraint to apply to the mining process.
 * @param on A flag for profiling.
 */
public GenPrefixGrowth(Database db, Relaxation tdmRelax, boolean on)
{    super(db, tdmRelax, on);	}

@Override
protected void initializeProfiling()
{
    super.initializeProfiling();
    m_membersNameProfiling.addElement(new String("discoverL1"));
    m_membersNameProfiling.addElement(new String("discoverNext"));
    m_membersNameProfiling.addElement(new String("createProjDB"));
    m_membersNameProfiling.addElement(new String("verifyGeneralAcc"));
    m_membersNameProfiling.addElement(new String("verifyAcc"));
    m_membersNameProfiling.addElement(new String("simulation"));
    m_membersNameProfiling.addElement(new String("sort"));
    m_membersNameProfiling.addElement(new String("accepts"));
    for (int i=0;i<8; i++) 
    	m_membersProfiling.addElement(new Long(0));
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
/** 
 * Discover temporally and alphabetically accepted elements   
 */
private Vector<Element>[] discoverNextElements(ProjectedDB db, EventSequence alpha, 
		Vector<Element> alphabet, ProjectedDB[] projDbs, int[] sup, ProjectedDB[] projDbsPar, 
		int[] supPar) throws d3m.spam.constraints.contentConstraints.automata.DFASimulationException
{
    long time = 0;
    if (m_profileOn) time = System.currentTimeMillis();
		
	BitSet visPar = new BitSet(alphabet.size()); visPar.clear();
	BitSet accPar = new BitSet(alphabet.size()); accPar.clear();
	BitSet vis = new BitSet(alphabet.size()); vis.clear();
	BitSet acc = new BitSet(alphabet.size()); acc.clear();
	m_tdmRelax.simulateSequence(alpha);
	for (int i=0; i<db.size(); i++)	{
        // pDB <-- the index of s in the original DB
        int pDB = db.indexOfSequenceAt(i);
        // pSub <-- the index of the start of subsequence
        int pSub = db.indexOfStartSubsequenceAt(i);
    	EventSequence s = m_db.elementAt(pDB);
        // Looks for parallel events
        m_tdmRelax.verifyParallelAcceptance(alpha, s.elementAtIndex(pSub).getSet(), 
                	alphabet, supPar, projDbsPar, i, pDB, pSub, visPar, accPar);
    	// Looks for serial events
        m_tdmRelax.verifyAcceptance(alpha, s, alphabet, sup, projDbs, i, pDB, 
            												pSub, false, vis, acc); 
    }
    @SuppressWarnings("unchecked")	
    Vector<Element>[] f_listPair = new Vector[2];
    @SuppressWarnings("unchecked")
    Vector<Element> vec = (Vector<Element>)alphabet.clone();
    m_tdmRelax.m_tdm.getSortedFrequentElements(vec, sup, projDbs);
    @SuppressWarnings("unchecked")
    Vector<Element> vecPar = (Vector<Element>)alphabet.clone();
    m_tdmRelax.m_tdm.getSortedFrequentElements(vecPar, supPar, projDbsPar);
    f_listPair[0] = vec;
    f_listPair[1] = vecPar;
    if (m_profileOn) {
        Long old1 = m_membersProfiling.elementAt(DISCOVER_NEXT);
        m_membersProfiling.setElementAt(new Long(old1.longValue()+System.currentTimeMillis()-time), DISCOVER_NEXT);
    } 
    return f_listPair;
}

/**
 * Computes the complete set of sequential patterns with some prefix.
 * @param alpha The sequential pattern (empty in the first step of recursion).
 * @param alphaSize The size of alfa.
 * @param alphabet The set of different elements in the database.
 * @param db The alfa-projected database. Initially it corresponds to the DB.
 * @return A vector with the complete set of sequential patterns with prefix alfa.
 */
protected Vector<EventSequence> runRecursively(EventSequence alpha, int alphaSize, 
		Vector<Element> alphabet, ProjectedDB db)
{
	int arsize = alphabet.size();
	int[] supPar = new int[arsize];
	int[] sup = new int[arsize];
	ProjectedDB[] projDbs = new ProjectedDB[arsize];
	ProjectedDB[] projDbsPar = new ProjectedDB[arsize];
	for (int i=0; i<arsize; i++){
 		sup[i] = 0;
		supPar[i] = 0;
		projDbs[i] = new ProjectedDB(db.size());
		projDbsPar[i] = new ProjectedDB(db.size());
	}
	try{
		Vector<Element>[] pairFList = discoverNextElements(db, alpha, alphabet, 
												  projDbs, sup,
												  projDbsPar, supPar);
		Vector<Element> f_list = pairFList[0];
		Vector<Element> f_listPar = pairFList[1];
		Vector<EventSequence> frequent = new Vector<EventSequence>(0);
		// Create Parallel Sequences
		ItemSet sk = alpha.elementAtIndex(alphaSize-1).getSet();
		for (int i=0; i<f_listPar.size(); i++)	{
		    Element el = f_listPar.elementAt(i);
			ItemSet set = new ItemSet(sk);
			set.addElement(el);
		    EventSequence alpha2 = (EventSequence)alpha.clone();
			alpha2 = alpha2.addElement(el);  
			alpha2.setSupport(supPar[i]);
			alpha2.setConfidence(((double)supPar[i])/((double)alpha.getPrefixSupport()));
//System.out.println("PREFIX -"+alpha2.toString());
			if (m_tdmRelax.accepts(alpha2))
//			{System.out.println("\t"+alpha2.toString());
				frequent.addElement(alpha2);
//			}
			frequent.addAll(runRecursively(alpha2, alphaSize, alphabet, projDbsPar[i])); 
		}
		// Create Serial Sequences
		for (int i=0; i<f_list.size(); i++){
		    Element el = f_list.elementAt(i);
			ItemSet set = new ItemSet(el);
		    EventSequence b = new EventSequence(new Event(set, alpha.getNextInstant()));
			EventSequence alpha2 = alpha.concatenateWith(b);
			alpha2.setSupport(sup[i]);
			alpha2.setPrefixSupport(alpha.getSupport());
			alpha2.setConfidence(((double)sup[i])/((double)alpha.getSupport()));
//System.out.println("PREFIX -"+alpha2.toString());
			if (m_tdmRelax.accepts(alpha2))
//			{System.out.println("\t"+alpha2.toString());
				frequent.addElement(alpha2);
//			}
			frequent.addAll(runRecursively(alpha2, alphaSize+1, alphabet, projDbs[i])); 
		}
		return frequent;
	}catch (d3m.spam.constraints.contentConstraints.automata.DFASimulationException ex){
		return new Vector<EventSequence>(0);
	}
}

/**
 * Runs the algorithm to find the large itemsets.
 * @return return a vector with frequent patterns in the first element, 
 *          and the execution time in the second element.
 */
@Override
public Vector<EventSequence> run() 
{
    long time = 0;
    if (m_profileOn) time = System.currentTimeMillis();
	
	Vector<Element> alphabet = m_tdmRelax.getCandidates();
	int arsize = alphabet.size();
	int[] sup = new int[arsize];
	ProjectedDB[] projDbs = new ProjectedDB[arsize];
	for (int i=0; i<arsize; i++){
 		sup[i] = 0;
//		projDbs[i] = new ProjectedDB(m_db.getSize()*m_db.getMaxLength());
		//PARA POUPAR MEMORIA
//		projDbs[i] = new ProjectedDB(m_db.getSize()*m_db.getLengthMode());
		projDbs[i] = new ProjectedDB(m_db.getSize()*5);
	}
	Vector<Element> f_list = discoverL1(alphabet, projDbs, sup);
	// Calls procedure runRecursevely for each frequent item 
	// that are accepted by the constraint
    for (int i=0; i<f_list.size(); i++){
		Element el = f_list.elementAt(i);
		EventSequence s = new EventSequence(new Event(new ItemSet(el), 
                                            new TimePoint(m_tdmRelax.m_tdm.m_temporalC.getGranularity())));
		m_tdmRelax.resetSimulation();
		if (m_tdmRelax.acceptsAsPrefix(s))	{
			s.setSupport(sup[i]);
//System.out.println("PREFIX -"+s.toString());
			if (m_tdmRelax.accepts(s))
//			{System.out.println("\t"+s.toString());
				m_litemsets.addElement(s);
//			}
			m_litemsets.addAll(runRecursively(s,1,f_list, projDbs[i]));
		}
   }
    m_nrOfPatterns = m_litemsets.size();
	
    if (m_profileOn) {
		Long old = m_membersProfiling.elementAt(RUN);
		m_membersProfiling.setElementAt(new Long(old.longValue()+System.currentTimeMillis()-time), RUN);
    }	
    return m_litemsets;
}

/*void calculateInterestingnessMeasures(Vector L)
{
	for (int i=0;i<L.size();i++) {
		EventSequence s = (EventSequence) L.elementAt(i);
		int size = s.length();
		if (1!=size) {
			long sup = s.getSupport();
			EventSequence prefix = s.subsequence(0, size-2);
			long supP = getSupportOf(prefix, L);
			double conf = ((double)sup)/((double)supP);
			s.setConfidence(conf);
			EventSequence suffix = s.subsequence(size-1, size-1);
			long supS = getSupportOf(suffix, L);
			double lift = conf*m_db.getSize()/((double)supS);
			s.setLift(lift);
			L.setElementAt(s, i);
		}
	}
}*/

/*long getSupportOf(EventSequence s, Vector L)
{
	for (int i=0;i<L.size();i++) {
		EventSequence ss = (EventSequence)L.elementAt(i);
		if (s.equals(ss))
			return ss.getSupport();
	}
	return 0;
}*/

/**
 * Calls the run method and measures the time and memory wasted.
 */
@Override
public Vector<String> exec()
{
/*	String line = "Algoritmo -> "+getClass().getName()
                      +" DBsize="+String.valueOf(m_db.getSize())
                      +" Constraint="+m_tdmRelax.toString();
*/	String line = m_tdmRelax.toString();    
	System.out.println(line);
    long initialTime = System.currentTimeMillis();

    Vector<EventSequence> L = run();
System.out.println("Finishes!!!!");	
    String st_alg = line+"\t\t\t";
    String st_resume = "time\tLk";
    String st_results = String.valueOf((System.currentTimeMillis()-initialTime));
//	calculateInterestingnessMeasures(L);
	Vector<EventSequence> maxPat = filter(L);
	m_nrOfPatterns = maxPat.size();
System.out.println("L="+String.valueOf(L.size()));//+"->"+L.toString());
System.out.println("F="+String.valueOf(m_nrOfPatterns)+"->"+maxPat.toString());
	st_results += "\t" + String.valueOf(m_nrOfPatterns);
    if (m_profileOn)   {
		for (int i=0; i<m_membersNameProfiling.size(); i++){
            st_alg += "\t";
            st_resume += "\t" +m_membersNameProfiling.elementAt(i);
            st_results += "\t" +(m_membersProfiling.elementAt(i)).toString();
		}
    }
//	calculateInterestingnessMeasures(L);
	Vector<String> vec = new Vector<String>(3);
    vec.addElement(st_alg);
    vec.addElement(st_resume);
    vec.addElement(st_results);
    vec.addElement(maxPat.toString());  
    return vec;
}

}
