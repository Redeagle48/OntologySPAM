/*------------------------------------------------------------------
--               PACKAGE - SEQUENTIAL PATTERN MINING              --
--                           CONSTRAINT                           --
--                                                                --
--                       Claudia M. Antunes                       --
--                         November 2003                          --
--------------------------------------------------------------------
--					   RelaxNonAccepted.java                      --
--------------------------------------------------------------------*/
package d3m.spam.constraints.relaxations;

import d3m.spam.core.*;
import d3m.spam.constraints.*;
import ontologies.timeOntology.*;
import java.util.*;

/**
 * Class <CODE>RelaxNonAcc</CODE>
 * @author Claudia Antunes
 */
public class RelaxNonAcc extends Relaxation
{
	/**	 */
	protected Vector<Element> m_alphabet;

/** Creates a new instance of Relaxation */
public RelaxNonAcc() 
{
	super();
	m_alphabet = new Vector<Element>(0);
	m_name = "RelaxNonAcc";
}

/**
 * 
 * @param tdm
 * @param timeError
 * @param alphabet
 * @param gap
 */
public RelaxNonAcc(TDMConstraint tdm, TimeQuantity timeError, Vector<Element> alphabet, int gap) 
{
	super(tdm, timeError, gap);
	m_alphabet = alphabet;
	m_name = "RelaxNonAcc";
}

/**
 * Returns the set of elements that belong to the relaxation alphabet.
 */
@Override
public Vector<Element> getCandidates()
{	return m_alphabet;	}

/**
 * Verifies if a sequence may be accepted as a prefix. For non-accepted all 
 * sequences may be accepted in order to be extended.
 * @param s The sequence to verify.
 */
@Override
public boolean acceptsAsPrefix(EventSequence s)
{	return true;	}

/**
 * It is not need to simulate the sequence. Every sequence can be accepted.
 * @param s The sequence to verify.
 * @throws d3m.spam.constraints.contentConstraints.automata.DFASimulationException
 */
@Override
public void simulateSequence(EventSequence s)throws d3m.spam.constraints.contentConstraints.automata.DFASimulationException
{	
// To be defined	
}

/**
 * Verifies if a sequence can't be accepted by the constraint itself.
 * @param s The sequence to verify.
 */
@Override
public boolean accepts(EventSequence s)
{	return !m_tdm.m_contentC.m_dfa.accepts(s);	}

/**
 * Returns true if the el can be appended to s, in accordance to this relaxation.
 * @param s The sequence already accepted.
 * @param el The element to append
 * @param inParallel A flag to state if we are verifying at the same instant or in the next ones
 */
@Override
protected boolean isAcceptedByRelaxation(EventSequence s, Element el, boolean inParallel)
{
	if (inParallel){	
		ItemSet set = new ItemSet(s.elementAtIndex(s.length()-1).getSet());
		return el.belongsTo(m_alphabet) && el.isGreaterThan(set.elementAt(set.size()-1));
	}
	return el.belongsTo(m_alphabet);
}

@Override
protected void verifyContentGeneralAcceptance(Event ev, Vector<Element> alphabet, 
		boolean[] bs, int[] sup, ProjectedDB[] projDBs, int sInd, int eInd)
{
	ItemSet set = ev.getSet();
	for (int i=0; i<set.size(); i++) {
		Element el = set.elementAt(i);
		if (el.belongsTo(m_alphabet)){
			int found = m_tdm.m_contentC.m_taxonomy.getParentOf(el, alphabet);
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
}

}
