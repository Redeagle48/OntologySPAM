/*------------------------------------------------------------------
--               PACKAGE - SEQUENTIAL PATTERN MINING              --
--                           CONSTRAINT                           --
--                                                                --
--                       Claudia M. Antunes                       --
--                        September 2003                          --
--------------------------------------------------------------------
--                        Constraint.java                         --
--------------------------------------------------------------------*/
package d3m.spam.constraints.contentConstraints;

import d3m.spam.constraints.contentConstraints.automata.*;

/**
 * 
 * @author Claudia Antunes
 *
 */
public class ContentConstraint extends Object
{
    /** The Taxonomy. */
    public Taxonomy m_taxonomy;
    /** The Constraint it self. */
    public DFA m_dfa;
    /** m_level */
	public byte m_level;
//________________________________________________________________
/** 
 * Creates a new instance of Constraint 
 */
public ContentConstraint()
{
    m_dfa = null;
    m_taxonomy = null;
	m_level = (byte)-1;
}
//________________________________________________________________
/** 
 * Creates a new instance of Constraint 
 * @param tax
 * @param level
 * @param machine
 */
public ContentConstraint(Taxonomy tax, byte level, DFA machine)
{
    m_dfa = machine;
    m_taxonomy = tax;
	m_level = level;
}
//________________________________________________________________
}