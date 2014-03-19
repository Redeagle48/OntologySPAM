/*------------------------------------------------------------------
--               PACKAGE - SEQUENTIAL PATTERN MINING              --
--                           CONSTRAINT                           --
--                                                                --
--                       Claudia M. Antunes                       --
--                         November 2003                          --
--------------------------------------------------------------------
--						  Relaxation.java                         --
--------------------------------------------------------------------*/
package d3m.spam.constraints.relaxations;

import d3m.spam.core.*;
import d3m.spam.constraints.*;
import ontologies.timeOntology.*;
import java.util.*;

/**
 * Class <CODE>Relaxation</CODE>
 * @author Claudia Antunes
 *
 */
public abstract class Relaxation extends Object
{
	/** The relaxation on time */
	protected TimeQuantity m_timeError;
	/** The gap relaxation */
	protected int m_maxDist;
	/** The constraint to relax */
	public TDMConstraint m_tdm;
	/** The name for the relaxation */
	protected String m_name;

/** Creates a new instance of Relaxation */
public Relaxation() 
{
	m_timeError = new TimeQuantity();
	m_maxDist = 1;
	m_tdm = null;
	m_name = "Relax";
}

/**
 * 
 * @param tdm
 * @param timeError
 * @param gap
 */
public Relaxation(TDMConstraint tdm, TimeQuantity timeError, int gap) 
{
	m_timeError = timeError;
	m_maxDist = gap+1;
	m_tdm = tdm;
	m_name = "Relax";
}

/**
 * @return Returns the set of elements that belong to the relaxation alphabet.
 * In general, subclasses donot need to redefine the method, 
 * but Aprox and NonAcc do.
 */
public Vector<Element> getCandidates()
{	return m_tdm.m_contentC.m_dfa.getAlphabet();	}

/** 
 * Verifies the support and creates the infrastructure to generate initial 
 * projectedDBs for each element in the database. 
 * *************It is not need to be redefined by its subclasses.************
 * @param s The db sequence to verify.
 * @param alphabet The set of elements in the db.
 * @param sup An array to store elements support.
 * @param projDBs An array of projectedDBs
 * @param index The first index to look for.
 * @return True if the sequence supports some valid elements.
 */
public boolean verifyGeneralAcceptance(EventSequence s, Vector<Element> alphabet,int[] sup, 
										ProjectedDB[] projDBs, int index)
{
	boolean seqSupports = false;
	boolean[] bs = new boolean[alphabet.size()];
//	int from = 0;
	int from = -1;
	do{
		from = m_tdm.m_temporalC.findAcceptableEvent(s, m_timeError, from+1);
		if (-1!=from) {
			seqSupports = true;
			verifyContentGeneralAcceptance(s.elementAtIndex(from), alphabet, bs,
												sup, projDBs, index, from);
//from++;			
		}
	} while (-1!=from && from<s.length());
	return seqSupports;
}

/** 
 * Counts the support and creates the infrastructure to generate initial 
 * projectedDBs. 
 * *************It is not need to be redefined by its subclasses.************
 * @param ev The event to verify.
 * @param alphabet The set of elements in the db.
 * @param bs A bitset to verify if actual sequence has already contributed to the support of each element.
 * @param sup An array to store elements support.
 * @param projDBs An array of projectedDBs
 * @param sInd The sequence id.
 * @param eInd The event position in the sequence.
 */
protected void verifyContentGeneralAcceptance(Event ev, Vector<Element> alphabet, 
		boolean[] bs, int[] sup, ProjectedDB[] projDBs, int sInd, int eInd)
{
	ItemSet set = ev.getSet();
	for (int i=0; i<set.size(); i++) {
		Element el = set.elementAt(i);
		int found = m_tdm.m_contentC.m_taxonomy.getParentOf(el, alphabet);
//		int found = alphabet.indexOf(el);
		if (-1!=found)	{
			// If current sequence did not contribute to the support of 
			// this element yet, increment its support
			if (!bs[found]){
				sup[found]++;
				bs[found] = true; 
			}
			// In any case, add another object to projected db
			projDBs[found].set(sInd, eInd);
		}
	}
}

/** 
 * Verifies the support and creates the infrastructure to generate initial 
 * projectedDBs for each element which respects the temporal constraint. 
 * *************It is not need to be redefined by its subclasses.************
 * @param alpha The sequence to verify.
 * @param s
 * @param alphabet The set of elements in the db.
 * @param sup An array to store elements support.
 * @param projDBs An array of projectedDBs
 * @param indProj
 * @param sPos The sequence id.
 * @param eInd The event position in the sequence.
 * @param inParallel A flag to state if we are verifying at the same instant or in the next ones
 * @param visited
 * @param accepted
 */
public void verifyAcceptance(EventSequence alpha, EventSequence s, Vector<Element> alphabet, int[] sup, 
		ProjectedDB[] projDBs, int indProj, int sPos, int eInd, boolean inParallel, 
		BitSet visited, BitSet accepted)
{
	if (m_tdm.m_contentC.m_dfa.canExtend(inParallel)) {
		// Warrants that the temporal constraint is respected
		int from = eInd-1;
		do{
			from = m_tdm.m_temporalC.findNextAcceptableEvent(s, m_timeError, eInd, from+1, m_maxDist);
			if (-1!=from)
				verifyContentAcceptance(alpha, s.elementAtIndex(from).getSet(), 
						alphabet, sup, projDBs, indProj, sPos, from, inParallel, visited, accepted);
		} while (-1!=from && from<s.length()-1);
	}
}
/**
 * 
 * @param alpha
 * @param set
 * @param alphabet
 * @param sup
 * @param projDBs
 * @param indProj
 * @param sInd
 * @param eInd
 * @param vis
 * @param acc
 */
public void verifyParallelAcceptance(EventSequence alpha, ItemSet set, Vector<Element> alphabet, int[] sup, 
			ProjectedDB[] projDBs, int indProj, int sInd, int eInd, BitSet vis, BitSet acc)
{	
	if (set.size()>1)
		verifyContentAcceptance(alpha, set, alphabet, sup, projDBs, indProj, sInd, eInd, true, vis, acc);	
}

/** 
 * Verifies the support and creates the infrastructure to generate initial 
 * projectedDBs for each element which respect to the content constraint. 
 * *************It is not need to be redefined by its subclasses.************
 * @param alpha sequence to verify.
 * @param set
 * @param alphabet The set of elements in the db.
 * @param sup An array to store elements support.
 * @param projDBs An array of projectedDBs
 * @param indProj
 * @param sInd The sequence id.
 * @param eInd The event position in the sequence.
 * @param visited
 * @param accepted
 * @param inParallel A flag to state if we are verifying at the same instant or in the next ones
 */
protected void verifyContentAcceptance(EventSequence alpha, ItemSet set, Vector<Element> alphabet, 
				int[] sup, ProjectedDB[] projDBs, int indProj, int sInd, int eInd, 
				boolean inParallel, BitSet visited, BitSet accepted)
{
	for (int i=0; i<set.size(); i++) {
		int found = m_tdm.m_contentC.m_taxonomy.getParentOf(set.elementAt(i), alphabet);
//		Element el = set.elementAt(i);
//		int found = alphabet.indexOf(el);
		if (-1!=found){
			Element el = alphabet.elementAt(found);
//System.out.println("Try "+el.toString());				
			if (!visited.get(found)){
				visited.set(found);
				accepted.set(found, isAcceptedByRelaxation(alpha, el, inParallel));
			}
			if (accepted.get(found)){
//System.out.println("Accepts "+el.toString());	
				if (!projDBs[found].contains(sInd))
					sup[found]++;
				projDBs[found].set(sInd, eInd);
			}
		}
	}
}

/**
 * ******** It is needed to redefine this method, for some subclasses. *******
 * @param s The sequence already accepted.
 * @param el The element to append
 * @param inParallel A flag to state if we are verifying at the same instant or in the next ones
 * @return Returns true if the el can be appended to s, in accordance to this relaxation.
 */
protected boolean isAcceptedByRelaxation(EventSequence s, Element el, boolean inParallel)
{
	return m_tdm.m_contentC.m_dfa.acceptsToJoinToAlpha(s, el, inParallel);
}

/**
 * Verifies if a sequence may be accepted by the constraint.
 * ************* It is needed to redefine this method. **********
 * @param s The sequence to verify.
 * @return true, if s is valid according to the relaxation
 */
abstract public boolean accepts(EventSequence s);

/**
 * Verifies if a sequence may be accepted as a prefix.
 * ************* It is needed to redefine this method. **********
 * @param s The sequence to verify.
 * @return true, if s is valid prefix according to the relaxation
 */
public boolean acceptsAsPrefix(EventSequence s)
{	return true;	}

/**
 *  Simulates the sequence in the automata, creating the possible scenarios
 *  for procede with other elements.
 * ************* It is needed to redefine this method. **********
 * @param s The sequence to verify.
 * @throws d3m.spam.constraints.contentConstraints.automata.DFASimulationException
 */
public void simulateSequence(EventSequence s)throws d3m.spam.constraints.contentConstraints.automata.DFASimulationException
{
// To be defined	
}

/**
 * 
 */
public void resetSimulation()
{	m_tdm.m_contentC.m_dfa.resetSimulation();	}

@Override
public String toString()
{
	return m_name+"->"+m_tdm.m_contentC.m_dfa.toString()+"->"+m_tdm.m_existC.toString();
}

}
