/*------------------------------------------------------------------
--               PACKAGE - SEQUENTIAL PATTERN MINING              --
--                           CONSTRAINT                           --
--                                                                --
--                       Claudia M. Antunes                       --
--                         November 2003                          --
--------------------------------------------------------------------
--						  RelaxLegal.java                         --
--------------------------------------------------------------------*/

package d3m.spam.constraints.relaxations;

import d3m.spam.core.*;
import d3m.spam.constraints.*;
import ontologies.timeOntology.*;

/**
 * Class <CODE>RelaxLegal</CODE>
 * @author Claudia Antunes
 *
 */
public class RelaxLegal extends Relaxation
{

/** Creates a new instance of Relaxation */
public RelaxLegal() 
{	
	super();	
	m_name = "RelaxLegal";
}

/**
 * 
 * @param tdm
 * @param timeError
 * @param gap
 */
public RelaxLegal(TDMConstraint tdm, TimeQuantity timeError, int gap) 
{	
	super(tdm, timeError, gap);	
	m_name = "RelaxLegal";
}

/**
 * Verifies if a sequence may be accepted by the constraint.
 * Since this method is applied after verifying if s is accepted 
 * as a prefix, it allways return true.
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
//{	return m_tdm.m_contentC.m_dfa.acceptsAsLegal(s);	}
{	return true; }

/**
 *  Simulates the sequence in the automata, creating the possible scenarios
 *  for procede with other elements.
 * @param s The sequence to verify.
 * @throws d3m.spam.constraints.contentConstraints.automata.DFASimulationException 
 */
@Override
public void simulateSequence(EventSequence s) throws d3m.spam.constraints.contentConstraints.automata.DFASimulationException
{	m_tdm.m_contentC.m_dfa.simulateSubSequence(s);	}

/**
 * Returns true if the el can be appended to s, in accordance to this relaxation.
 * @param s The sequence already accepted.
 * @param el The element to append
 * @param inParallel A flag to state if we are verifying at the same instant or in the next ones
 */
@Override
protected boolean isAcceptedByRelaxation(EventSequence s, Element el, boolean inParallel)
{
	return m_tdm.m_contentC.m_dfa.legalAcceptsToJoinToAlpha(s, el, inParallel);
}

}
