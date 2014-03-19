/*-------------------------------------------------------------------
 --                      PhD Claudia Antunes                       --
 --               PACKAGE - SEQUENTIAL PATTERN MINING              --
 --                        TIME ONTOLOGY                           --
 --                                                                --
 --                         Claudia Antunes                        --
 --                        September 2003                          --
 --------------------------------------------------------------------
 --               RegularNonConvexTimeInterval.java                --
 --------------------------------------------------------------------*/
package ontologies.timeOntology;

import java.util.*;

/**
 * RegularNonConvexTimeIntervals specify non contiguous portions of time. For
 * example, "every summer", "every April", "every dia útil from 9:00 to 17:00",
 * ...
 */
public class RegularNonConvexTimeInterval extends NonConvexTimeInterval {
	/**
	 * Specifies the interval pattern. For example, for "every April", the
	 * m_characteristicTI is "April".
	 */
	protected ConvexTimeInterval m_characteristicTI;

	/**
	 * 
	 */
	public RegularNonConvexTimeInterval() {
		super();
		m_characteristicTI = null;
	}

	/**
	 * 
	 * @param init
	 * @param characteristicTI
	 * @param nr
	 */
	public RegularNonConvexTimeInterval(TimePoint init,
			TimeInterval characteristicTI, byte nr) {
		m_nrOfConnectedIntervals = nr;
		m_startingPoint = init;
		m_listTIs = new Vector<TimeInterval>(nr);
		//TimeInterval ti = null;
		for (int i = 0; i < nr; i++) {
		}
		// m_endingPoint = ti.getEndingPoint();
	}

    /*
     * (non-Javadoc)
     * @see pamda.ontologies.timeOntology.TimeInterval#aproxContains(TimePoint, TimeQuantity)
     */
	@Override
	public boolean aproxContains(TimePoint tp, TimeQuantity error) {
		return false;
	}
}
