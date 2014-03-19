/*------------------------------------------------------------------
--               PACKAGE - SEQUENTIAL PATTERN MINING              --
--                           CONSTRAINT                           --
--                                                                --
--                       Claudia M. Antunes                       --
--                        September 2003                          --
--------------------------------------------------------------------
--                     SupportConstraint.java                     --
--------------------------------------------------------------------*/
package d3m.spam.constraints;

/**
 * Class <B><CODE>ExistentialConstraint</CODE></B>
 * @author Claudia Antunes
 *
 */
public class ExistentialConstraint extends Object
{
    /** The minimum support accepted. */
    protected double m_minSupport;
    /** The global number of occurrences to consider a sequence frequent. */
    protected int m_globalNr;
    /** m_threshold */
	protected int m_threshold;
//________________________________________________________________
/** Creates a new instance of Constraint */
public ExistentialConstraint()
{
}
//________________________________________________________________
/** Creates a new instance of Constraint 
 * @param min_sup
 */
public ExistentialConstraint(double min_sup)
{
    m_minSupport = min_sup;
	m_threshold = 0;
}
//________________________________________________________________
/**
 * @param sup
 * @return true, if sup is enough
 */
public boolean accepts(int sup)
{
    return (sup >= m_threshold);
}
//________________________________________________________________
/**
 * Updates the nr for the global support.
 * @param global
 */
public void setSupportGlobalNr(int global)
{
	m_globalNr = global;
	m_threshold = (int) Math.round(Math.ceil(m_minSupport * m_globalNr));
}
//________________________________________________________________
/**
 * Updates the minimum support accepted.
 * @param sup
 */
public void setSupport(double sup)
{	m_minSupport = sup;	}
//________________________________________________________________
/**
 * Returns the threshold
 * @return the threshold
 */
public int getThreshold()
{	return m_threshold;	}
//________________________________________________________________
@Override
public String toString()
{	return "sup="+String.valueOf(m_minSupport);	}
//________________________________________________________________
}