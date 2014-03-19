/*------------------------------------------------------------------
--               PACKAGE - SEQUENTIAL PATTERN MINING              --
--                           CONSTRAINT                           --
--                                                                --
--                       Claudia M. Antunes                       --
--                         November 2003                          --
--------------------------------------------------------------------
--						  RelaxTotal.java                         --
--------------------------------------------------------------------*/

package d3m.spam.constraints.relaxations;

import d3m.spam.core.*;
import d3m.spam.constraints.*;
import ontologies.timeOntology.*;
import java.util.*;

/**
 * Class <CODE>RelaxTotal</CODE>
 * @author Claudia Antunes
 *
 */
public class RelaxTotal extends Relaxation
{

/** Creates a new instance of Relaxation */
public RelaxTotal() 
{	
	super();	
	m_name = "RelaxTotal";
}

/**
 * 
 * @param tdm
 * @param timeError
 * @param gap
 */
public RelaxTotal(TDMConstraint tdm, TimeQuantity timeError, int gap) 
{	
	super(tdm, timeError, gap);	
	m_name = "RelaxTotal";
}

/** 
 * Verifies the support and creates the infrastructure to generate initial 
 * projectedDBs for each element which respect to the content constraint. 
 * @param alpha The sequence to verify.
 * @param alphabet The set of elements in the db.
 * @param sup An array to store elements support.
 * @param projDBs An array of projectedDBs
 * @param sInd The sequence id.
 * @param eInd The event position in the sequence.
 * @param inParallel A flag to state if we are verifying at the same instant or in the next ones
 */
@Override
protected void verifyContentAcceptance(EventSequence alpha, ItemSet set, Vector<Element> alphabet, 
						int[] sup, ProjectedDB[] projDBs, int indProj, int sInd, int eInd, 
										boolean inParallel, BitSet visited, BitSet accepted)
{
	for (int i=0; i<set.size(); i++) {
		Element el = set.elementAt(i);
		int found = m_tdm.m_contentC.m_taxonomy.getParentOf(el, alphabet);
		if (-1!=found)// && isAcceptedByRelaxation(alpha, (Element)alphabet.elementAt(found), inParallel))
		{
//			System.out.println("ACC->"+alpha.toString()+((Element)alphabet.elementAt(found)).toString());
			if (!projDBs[found].contains(sInd))
					sup[found]++;
			projDBs[found].set(sInd, eInd);
		}
	}
}

/**
 * Returns true if the el can be appended to s, in accordance to this relaxation.
 * @param s The sequence already accepted.
 * @param el The element to append
 * @param inParallel A flag to state if we are verifying at the same instant or in the next ones
 */
@Override
protected boolean isAcceptedByRelaxation(EventSequence s, Element el, boolean inParallel)
{
	ItemSet sk = s.elementAtIndex(s.length()-1).getSet();
	Element last = sk.elementAt(sk.size()-1);
	return (!inParallel||el.isGreaterThan(last));
}

/**
 * Verifies if a sequence may be accepted by the constraint.
 * @param s The sequence to verify.
 */
@Override
public boolean accepts(EventSequence s)
{	return true;	}

/**
 * Verifies if a sequence may be accepted as a prefix.
 * @param s The sequence to verify.
 */
@Override
public boolean acceptsAsPrefix(EventSequence s)
{	return true;	}

/**
 *  Simulates the sequence in the automata, creating the possible scenarios
 *  for procede with other elements.
 * @param s The sequence to verify.
 */
@Override
public void simulateSequence(EventSequence s)
{
// To be defined	
}

}
