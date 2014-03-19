/*------------------------------------------------------------------
--               PACKAGE - SEQUENTIAL PATTERN MINING              --
--                           CONSTRAINT                           --
--                                                                --
--                       Claudia M. Antunes                       --
--                         November 2003                          --
--------------------------------------------------------------------
--						  RelaxPrefix.java                        --
--------------------------------------------------------------------*/

package d3m.spam.constraints.relaxations;

import d3m.spam.core.EventSequence;
import d3m.spam.constraints.*;
import ontologies.timeOntology.*;

/**
 * Class <CODE>RelaxPrefix</CODE>
 * @author Claudia Antunes
 *
 */
public class RelaxPrefix extends Relaxation
{
	/** Creates a new instance of Relaxation */
	public RelaxPrefix() 
	{
		super();
		m_name = "RelaxPrefix";
	}
	
	/**
	 * 
	 * @param tdm
	 * @param timeError
	 * @param gap
	 */
	public RelaxPrefix(TDMConstraint tdm, TimeQuantity timeError, int gap) 
	{
		super(tdm, timeError, gap);
		m_name = "RelaxPrefix";
	}
	
	/**
	 * Verifies if a sequence may be accepted as a prefix.
	 * @param s The sequence to verify.
	 */
	@Override
	public boolean acceptsAsPrefix(EventSequence s)
	{	return m_tdm.m_contentC.m_dfa.acceptsAsPrefix(s);	}
	
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
	 *  Simulates the sequence in the automata, creating the possible scenarios
	 *  for procede with other elements.
	 * @param s The sequence to verify.
	 * @throws pamda.spam.constraints.contentConstraints.automata.DFASimulationException
	 */
	@Override
	public void simulateSequence(EventSequence s) throws d3m.spam.constraints.contentConstraints.automata.DFASimulationException
	{	m_tdm.m_contentC.m_dfa.simulateSequence(s);	}
}
