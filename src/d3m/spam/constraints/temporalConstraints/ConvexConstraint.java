/*------------------------------------------------------------------
--               PACKAGE - SEQUENTIAL PATTERN MINING              --
--                        TIME ONTOLOGY                           --
--                                                                --
--                       Claudia M. Antunes                       --
--                        September 2003                          --
--------------------------------------------------------------------
--                      ConvexConstraint.java                     --
--------------------------------------------------------------------*/
package d3m.spam.constraints.temporalConstraints;

import ontologies.timeOntology.*;
/**
 * ConvexConstraints define temporal constraints for specific convex time
 * intervals. They can constraint the acceptance of transactions
 * occurred in a concrete portion of time, for example, from 1st April 1990
 * to 30th April 2000.
 */
public class ConvexConstraint extends TemporalConstraint
{

//_____________________________________________________________________    
/**
 * 
 */
public ConvexConstraint()
{
    super();
}
//________________________________________________________________
/** Creates a new instance of ConvexConstraint 
 * @param ti
 * @param gap
 * @param unit
 * @throws Exception
 */
public ConvexConstraint(ConvexTimeInterval ti, TimeQuantity gap, TimeUnit unit) 
	throws Exception
{
    if (!ti.isConcrete())
        throw new Exception("Received Time Interval does not specify a concrete time interval!");
    m_interval = ti;
    m_gap = gap;
	m_granularity = unit;
}  
//_____________________________________________________________________    
}