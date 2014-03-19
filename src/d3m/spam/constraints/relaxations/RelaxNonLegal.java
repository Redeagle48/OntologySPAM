/*------------------------------------------------------------------
--               PACKAGE - SEQUENTIAL PATTERN MINING              --
--                           CONSTRAINT                           --
--                                                                --
--                       Claudia M. Antunes                       --
--                         November 2003                          --
--------------------------------------------------------------------
--					      RelaxNonLegal.java                      --
--------------------------------------------------------------------*/

package d3m.spam.constraints.relaxations;

import d3m.spam.core.*;
import d3m.spam.constraints.*;
import ontologies.timeOntology.*;
import java.util.*;

/**
 * Class <CODE>RelaxNonLegal</CODE>
 * @author Claudia Antunes
 */
public class RelaxNonLegal extends RelaxNonAcc
{

/** Creates a new instance of Relaxation */
public RelaxNonLegal() 
{
	super();
	m_name = "RelaxNonLegal";
}

/**
 * 
 * @param tdm
 * @param timeError
 * @param alphabet
 * @param gap
 */
public RelaxNonLegal(TDMConstraint tdm, TimeQuantity timeError, Vector<Element> alphabet, int gap) 
{
	super(tdm, timeError, alphabet, gap);
	m_name = "RelaxNonLegal";
}

/**
 * Verifies if a sequence may be accepted by the constraint.
 * Since this method is applied after verifying if s is accepted 
 * as a prefix, it allways return true.
 * @param s The sequence to verify.
 */
@Override
public boolean accepts(EventSequence s)
{	return !m_tdm.m_contentC.m_dfa.acceptsAsLegal(s);	}

}
