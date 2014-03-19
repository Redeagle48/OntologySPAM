/*-------------------------------------------------------------------
 --                      PhD Claudia Antunes                       --
 --               PACKAGE - SEQUENTIAL PATTERN MINING              --
 --                        TIME ONTOLOGY                           --
 --                                                                --
 --                         Claudia Antunes                        --
 --                        September 2003                          --
 --------------------------------------------------------------------
 --                  NonConvexTimeInterval.java                    --
 --------------------------------------------------------------------*/
package ontologies.timeOntology;

import java.util.*;

/**
 * Non-Convex-Time-Interval must not be connected.
 */
public class NonConvexTimeInterval extends TimeInterval {
	/** The list of time intervals that composes the Time Interval itself. */
	protected Vector<TimeInterval> m_listTIs;

	/** The number of connected time intervals. */
	protected byte m_nrOfConnectedIntervals;

	/**
	 * 
	 */
	public NonConvexTimeInterval() {
		super();
		m_nrOfConnectedIntervals = (byte) 0;
	}

	
	/**
	 * It assumes that all intervals' duration have the same granularity
	 * @param orderedTIs
	 */
	public NonConvexTimeInterval(Vector<TimeInterval> orderedTIs) {
		m_listTIs = orderedTIs;
		m_nrOfConnectedIntervals = (byte) m_listTIs.size();
		m_startingPoint = ((TimeInterval) m_listTIs.elementAt(0))
				.getStartingPoint();
		m_endingPoint = ((TimeInterval) m_listTIs
				.elementAt(m_nrOfConnectedIntervals - 1)).getEndingPoint();
		int duration = 0;
		for (int i = 0; i < m_nrOfConnectedIntervals; i++)
			duration += ((TimeInterval) m_listTIs.elementAt(i)).getDuration()
					.getQuantity();
		m_duration = new TimeQuantity(duration, ((TimeInterval) m_listTIs
				.elementAt(0)).getDuration().getUnit());
	}

	/**
	 * 
	 * @return the number of connected intervals
	 */
	public byte getNrOfConnectedIntervals() {
		return m_nrOfConnectedIntervals;
	}

	/*
     * (non-Javadoc)
     * @see pamda.ontologies.timeOntology.TimeInterval#contains(TimePoint)
     */
	public boolean contains(TimePoint p) {
		boolean ok = false;
		int i = 0;
		while (!ok && i < m_nrOfConnectedIntervals) {
			TimeInterval ti = (TimeInterval) m_listTIs.elementAt(i++);
			ok = ((p.after(ti.m_startingPoint) || p.equals(ti.m_startingPoint)) && (p
					.before(ti.m_endingPoint) || p.equals(ti.m_endingPoint)));
		}
		return ok;
	}

    /*
     * (non-Javadoc)
     * @see pamda.ontologies.timeOntology.TimeInterval#contains(TimeInterval)
     */
	@Override
	public boolean contains(TimeInterval t) {
		boolean ok = false;
		int i = 0;
		while (!ok && i < m_nrOfConnectedIntervals) {
			TimeInterval ti = (TimeInterval) m_listTIs.elementAt(i++);
			ok = ((ti.m_startingPoint.after(m_startingPoint) || ti.m_startingPoint
					.equals(m_startingPoint)) && (ti.m_endingPoint
					.before(m_endingPoint) || ti.m_endingPoint
					.equals(m_endingPoint)));
		}
		return ok;
	}

    /*
     * (non-Javadoc)
     * @see pamda.ontologies.timeOntology.TimeInterval#aproxContains(TimePoint, TimeQuantity)
     */
	@Override
	public boolean aproxContains(TimePoint tp, TimeQuantity error) {
		return false;
	}
	// _____________________________________________________________________
}
