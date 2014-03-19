/*------------------------------------------------------------------
--               PACKAGE - SEQUENTIAL PATTERN MINING              --
--                           CONSTRAINT                           --
--                                                                --
--                       Claudia M. Antunes                       --
--                        September 2003                          --
--------------------------------------------------------------------
--                    TemporalConstraint.java                     --
--------------------------------------------------------------------*/
package d3m.spam.constraints.temporalConstraints;

import d3m.spam.core.*;
import ontologies.timeOntology.*;

/**
 * Class <B>TemporalConstraint</B>
 * @author Claudia Antunes
 *
 */
public abstract class TemporalConstraint extends Object
{
    /** The Time Interval. */
    protected TimeInterval m_interval;
    /** The allowed time distance between consecutive events. */
    protected TimeQuantity m_gap;
    /** The data granularity. */
    protected TimeUnit m_granularity;
//________________________________________________________________
/** Creates a new instance of TemporalConstraint */
public TemporalConstraint()
{
    m_interval = null;
    m_gap = new TimeQuantity();
    m_granularity = new TimeUnit();
}
//________________________________________________________________
/**
 * @param ti
 * @param gap
 * @param unit
 */
public TemporalConstraint(TimeInterval ti, TimeQuantity gap, TimeUnit unit)
{
    m_interval = ti;
    m_gap = gap;
    m_granularity = unit;
}
//________________________________________________________________
//                      SELECTORS
//________________________________________________________________
/**
 * @return the granularity
 */
public TimeUnit getGranularity()
{   return m_granularity;   }
//________________________________________________________________
/**
 * @return the gap
 */
public TimeQuantity getMaxDistance()
{   return m_gap;   }
//________________________________________________________________
/**
 * @param e
 * @return true, if the event e is accepted by this constraint
 */
public boolean accepts(Event e)
{
    return m_interval.contains(e.getInstant());
}
//________________________________________________________________
/**
 * @param s
 * @param error
 * @param pos
 * @return the position in the sequence with the first acceptable event, 
 * 				according to this constraint
 */
public int findAcceptableEvent(EventSequence s, TimeQuantity error, int pos)
{
	while (pos<s.length() 
	&& !m_interval.aproxContains(s.elementAtIndex(pos).getInstant(), error))
		pos++;
	if (pos<s.length()) return pos;
	else return -1;
}
//________________________________________________________________
/**
 * @param s
 * @param error
 * @param init
 * @param from
 * @param dist
 * @return the position in the sequence with the next acceptable event, 
 * 				according to this constraint
 */
public int findNextAcceptableEvent(EventSequence s, TimeQuantity error, int init, int from, int dist)
{
	TimePoint first = s.elementAtIndex(init).getInstant();
	TimePoint prox = new TimePoint();
	boolean ok = false;
	int pos = from+1;
	while((pos-init<=dist) && (pos<s.length()) && !ok)
	{
		prox = s.elementAtIndex(pos++).getInstant();
		ok = m_interval.aproxContains(prox, error);
//System.out.println(first.toString()+"<"+String.valueOf(ok)+">"+m_interval.toString());	
	}
	if (ok && (m_gap.isEqualOrGreaterThan(prox.differenceTo(first))))
		return pos-1;
	return -1;
}
/*public int findNextAcceptableEvent(EventSequence s, TimeQuantity error, int init, int from, int dist)
{
	int pos = from+1;
	if (pos-init<=dist && pos<s.length())
		return pos;
	return -1;
}*/
//________________________________________________________________
}