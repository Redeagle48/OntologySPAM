/*------------------------------------------------------------------
--               PACKAGE - SEQUENTIAL PATTERN MINING              --
--                           CONSTRAINT                           --
--                                                                --
--                       Claudia M. Antunes                       --
--                         November 2003                          --
--------------------------------------------------------------------
--						 RelaxAccepted.java                       --
--------------------------------------------------------------------*/

package d3m.spam.constraints.relaxations;

import d3m.spam.core.*;
import d3m.spam.constraints.*;
import ontologies.timeOntology.*;

/**
 * Class <CODE>RelaxAccepted</CODE>
 * @author Claudia Antunes
 *
 */
public class RelaxAccepted extends RelaxPrefix
{
	/** Creates a new instance of Relaxation */
	public RelaxAccepted() 
	{
		super();
		m_name = "RelaxAcc";
	}

	/**
	 * 
	 * @param tdm
	 * @param timeError
	 * @param gap
	 */
	public RelaxAccepted(TDMConstraint tdm, TimeQuantity timeError, int gap) 
	{
		super(tdm, timeError, gap);
		m_name = "RelaxAcc";
	}

	/**
	* Verifies if a sequence may be accepted by the constraint.
	* @param s The sequence to verify.
	*/
	@Override
	public boolean accepts(EventSequence s)
	{	return m_tdm.m_contentC.m_dfa.accepts(s);	}

	/**
	 * Verifies if a sequence may be accepted as a prefix.
	 * @param s The sequence to verify.
	 */
	@Override
	public boolean acceptsAsPrefix(EventSequence s)
	{	return m_tdm.m_contentC.m_dfa.acceptsAsPrefix(s);	}

}
