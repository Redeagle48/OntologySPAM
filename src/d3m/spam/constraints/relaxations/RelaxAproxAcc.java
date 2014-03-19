/*------------------------------------------------------------------
--               PACKAGE - SEQUENTIAL PATTERN MINING              --
--                           CONSTRAINT                           --
--                                                                --
--                       Claudia M. Antunes                       --
--                         November 2003                          --
--------------------------------------------------------------------
--						 RelaxAproxAcc.java                       --
--------------------------------------------------------------------*/

package d3m.spam.constraints.relaxations;

import d3m.spam.core.*;
import d3m.spam.constraints.*;
import d3m.spam.constraints.contentConstraints.*;
import ontologies.timeOntology.*;
import java.util.*;
/**
 * Class <CODE>RelaxApproxAcc</CODE>
 * @author Claudia Antunes
 */
public class RelaxAproxAcc extends Relaxation
{
	private int m_error;
	private Vector<Element> m_alphabet;


/** Creates a new instance of Relaxation */
public RelaxAproxAcc() 
{
	super();
	m_error = 0;
	m_alphabet = new Vector<Element>(0);
	m_name = "RelaxAprox";
}

/**
 * 
 * @param tdm
 * @param timeError
 * @param error
 * @param alphabet
 * @param gap
 */
public RelaxAproxAcc(TDMConstraint tdm, TimeQuantity timeError, int error, 
						Vector<Element> alphabet, int gap) 
{
	super(tdm, timeError, gap);
	m_error = error;
	m_alphabet = alphabet;
	tdm.m_contentC.m_dfa.setAlphabet(m_alphabet);
	m_name = "RelaxAprox";
}

/**
 * @param c
 * @return Returns the set of elements that belong to the relaxation alphabet.
 */
public Vector<Element> getCandidates(ContentConstraint c)
{
	return m_alphabet;
}

/**
 * Returns true if the el can be appended to s, in accordance to this relaxation.
 * @param s The sequence already accepted.
 * @param el The element to append
 * @param inParallel A flag to state if we are verifying at the same instant or in the next ones
 */
@Override
protected boolean isAcceptedByRelaxation(EventSequence s, Element el, boolean inParallel)
{	return m_tdm.m_contentC.m_dfa.aproxAcceptsPrefix(s, el, inParallel, m_error);	}

/**
 * Verifies if a sequence may be aproximatelly accepted by the constraint.
 * @param s The sequence to verify.
 */
@Override
public boolean accepts(EventSequence s)
{	return m_tdm.m_contentC.m_dfa.aproxAccepts(s, m_error);	}

/**
 * Since it is used after choosing the acceptable prefixes. It allways return TRUE.
 * @param s The sequence to verify.
 */
@Override
public boolean acceptsAsPrefix(EventSequence s)
{	return true;	}

/**
 * This method is not used. It is necessary to simulate the entire sequence
 * every time, in order to record the accumulated error.
 * @param s The sequence to verify.
 */
@Override
public void simulateSequence(EventSequence s)
{	
// To be defined	
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
