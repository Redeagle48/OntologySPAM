/*-------------------------------------------------------------------
 --                      PhD Claudia Antunes                       --
 --               PACKAGE - SEQUENTIAL PATTERN MINING              --
 --                        TIME ONTOLOGY                           --
 --                                                                --
 --                         Claudia Antunes                        --
 --                        September 2003                          --
 --------------------------------------------------------------------
 --                    ConvexTimeInterval.java                     --
 --------------------------------------------------------------------*/
package ontologies.timeOntology;

/**
 * Convex-Time-Interval must be connected.
 */
public class ConvexTimeInterval extends TimeInterval {

	/**
	 * 
	 */
	public ConvexTimeInterval() {
		super();
	}

	/**
	 * 
	 * @param t1
	 * @param t2
	 * @throws Exception
	 */
	public ConvexTimeInterval(TimePoint t1, TimePoint t2) throws Exception {
		if (t1.after(t2))
			throw new Exception(
					"The ending time point is before the starting one!");
		m_startingPoint = t1;
		m_endingPoint = t2;
		m_duration = t2.differenceTo(t1);
	}

    /*
     * (non-Javadoc)
     * @see pamda.ontologies.timeOntology.TimeInterval#contains(TimePoint)
     */
	@Override
	public boolean contains(TimePoint p) {
		return ((p.after(m_startingPoint) || p.equals(m_startingPoint)) && (p
				.before(m_endingPoint) || p.equals(m_endingPoint)));
	}

    /*
     * (non-Javadoc)
     * @see pamda.ontologies.timeOntology.TimeInterval#contains(TimeInterval)
     */
	@Override
	public boolean contains(TimeInterval ti) {
		return ((ti.m_startingPoint.after(m_startingPoint) || ti.m_startingPoint
				.equals(m_startingPoint)) && (ti.m_endingPoint
				.before(m_endingPoint) || ti.m_endingPoint
				.equals(m_endingPoint)));
	}

    /*
     * (non-Javadoc)
     * @see pamda.ontologies.timeOntology.TimeInterval#aproxContains(TimePoint, TimeQuantity)
     */
	@Override
	public boolean aproxContains(TimePoint tp, TimeQuantity error) {
		return (((tp.after(m_startingPoint) || tp.equals(m_startingPoint)) && (tp
				.before(m_endingPoint) || tp.equals(m_endingPoint)))
				|| error.lessThan(tp.differenceTo(m_startingPoint)) || error
				.lessThan(tp.differenceTo(m_endingPoint)));
	}
	// _____________________________________________________________________
}
