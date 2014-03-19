/*------------------------------------------------------------------
--               PACKAGE - SEQUENTIAL PATTERN MINING              --
--                               CORE                             --
--                                                                --
--                       Claudia M. Antunes                       --
--						  September 2003                          --
--------------------------------------------------------------------
--                            Event.java                          --
--------------------------------------------------------------------*/

package d3m.spam.core;

import ontologies.timeOntology.*;

//--------------------------------------------------------------------
//			  CLASS Event
//--------------------------------------------------------------------
/**
 * Class <CODE>Event</CODE> for handling an element that occurs at a
 * given instant.
 * @version 2.0
 * @author Cláudia M. Antunes
 */
public class Event extends Object
{
//public static final int DEFAULT_ID = -1;
    
/** The event time occurrence. */
protected TimePoint m_time;
/** The event type. */
protected ItemSet m_set;

//________________________________________________________________
/** Creates a new empty instance of Event */
public Event()
{
    m_set = new ItemSet();
    m_time = new TimePoint();
}
//________________________________________________________________
/**
 * @param ev
 */
public Event (Event ev)
{
    m_set = ev.m_set;
    m_time = ev.m_time;
}
//________________________________________________________________
/**
 * @param ev
 * @param time
 */
public Event (Event ev, TimePoint time)
{
    m_set = ev.m_set;
    m_time = time;
}
//________________________________________________________________
/**
 * @param set
 * @param time
 */
public Event (ItemSet set, TimePoint time)
{
    m_set = set;
    m_time = time;
}
//________________________________________________________________
@Override
public Object clone()
{
    Event e = new Event();
    e.m_set = (ItemSet)m_set.clone();
    e.m_time = m_time;
    return e;
}
/**________________________________________________________________
 * 		SELECTORS
 * ________________________________________________________________ */
/**
 * @return Return the itemset of the event.
 */
public ItemSet getSet()
{	
    return m_set;	
}
//________________________________________________________________
/**
 * @return Return the instant of event occurrence.
 */
public TimePoint getInstant()
{	
    return m_time;	
}
/**________________________________________________________________
 *		TRANSFORMERS
 * ________________________________________________________________ 
 */
/**
 * @param set
 * Update the itemset of the event.
 */
public void setSet(ItemSet set)
{	
    m_set = set;	
}
//________________________________________________________________
/**
 * Set the instant of event occurrence.
 * @param value
 */
public void setInstant(TimePoint value)
{	
    m_time = value;	
}
//________________________________________________________________
/** @return Returns the event as a string.	*/
@Override
public String toString ()
{	
    return "[" + m_set.toString()+ " " + m_time.toString() + "]";
//	return m_set.toString();
}
/**________________________________________________________________
 *						TESTS
 * ________________________________________________________________ */
/** Verifies if the events are equal. */
@Override
public boolean equals(Object e)
{
	return (m_set.equals(((Event)e).m_set));
//		&&(m_time==((Event)e).m_time)); 
}
//________________________________________________________________
/**
 * @param e 
 * @return true, if the events have the same type, ie, the same element. 
 */
public boolean haveSameItemSet(Event e)
{	
    return m_set.equals(e.m_set);	
}
//________________________________________________________________
/**
 * @param e 
 * @return true, if events occur in simultaneous. 
 */
public boolean occurSimultaneousWith(Event e)
{	
    return (m_time.equals(e.m_time));	
}
//________________________________________________________________
/**
 * @param e 
 * @return true, if this event occurs after the received one. 
 */
public boolean occursAfterThan(Event e)
{	
    return (m_time.after(e.m_time));	
}
//________________________________________________________________
}
