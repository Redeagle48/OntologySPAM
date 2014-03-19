/*------------------------------------------------------------------
--               PACKAGE - SEQUENTIAL PATTERN MINING              --
--                        TIME ONTOLOGY                           --
--                                                                --
--                       Claudia M. Antunes                       --
--                        September 2003                          --
--------------------------------------------------------------------
--                      CyclicConstraint.java                     --
--------------------------------------------------------------------*/
package d3m.spam.constraints.temporalConstraints;

import ontologies.timeOntology.*;
/**
 * CyclicConstraints define temporal constraints for regular-non-convex-
 * -time-intervals. They constraint the acceptance of transactions
 * occurred in an abstract portion of time, that is, at time intervals not
 * completely defined. For example, "every Weednesday" or "every Summer.
 */
public class CyclicConstraint extends TemporalConstraint
{
//_____________________________________________________________________  
/**
 * 
 */
public CyclicConstraint()
{
    super();
}
//_____________________________________________________________________    
/**
 * @param ti
 * @param gap
 * @param unit
 */
public CyclicConstraint(RegularNonConvexTimeInterval ti, TimeQuantity gap, TimeUnit unit)
{
    super(ti, gap, unit);
}  
//_____________________________________________________________________    
}