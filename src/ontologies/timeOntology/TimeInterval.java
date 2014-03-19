/*-------------------------------------------------------------------
 --                      PhD Claudia Antunes                       --
 --               PACKAGE - SEQUENTIAL PATTERN MINING              --
 --                        TIME ONTOLOGY                           --
 --                                                                --
 --                         Claudia Antunes                        --
 --                        September 2003                          --
 --------------------------------------------------------------------
 --                       TimeInterval.java                        --
 --------------------------------------------------------------------*/

package ontologies.timeOntology;

/**
 * Time-Interval can be discontinuous but must contain more than one time point.
 */
public abstract class TimeInterval extends Object {
	/** */
	protected TimePoint m_startingPoint;
	/** */
	protected TimePoint m_endingPoint;
	/** */
	protected TimeQuantity m_duration;

	// _____________________________________________________________________
	/** 
	 * Default constructor
	 */
	public TimeInterval() {
		m_startingPoint = null;
		m_endingPoint = null;
		m_duration = null;
	}

	/**
	 * @return a time quantity representing the duration for the this time interval
	 */
	public TimeQuantity getDuration() {
		return m_duration;
	}

	/**
	 * 
	 * @return the starting time point    
	 */
	public TimePoint getStartingPoint() {
		return m_startingPoint;
	}

	/**
	 * 
	 * @return the ending time point    
	 */
	public TimePoint getEndingPoint() {
		return m_endingPoint;
	}

	/**
	 * 
	 * @param p
	 * @return true, if this time interval contains the time point received
	 */
	abstract public boolean contains(TimePoint p);

	/**
	 * 
	 * @param ti
	 * @param error
	 * @return true, if this time interval approximatelly contains the time point received
	 */
	abstract public boolean aproxContains(TimePoint ti, TimeQuantity error);

	/**
	 * 
	 * @param ti
	 * @return true, if this time interval contains the received time interval 
	 */
	abstract public boolean contains(TimeInterval ti);

	/**
	 * 
	 * @return true, if this object
	 */
	public boolean isConcrete() {
		return (m_startingPoint.concrete() && m_endingPoint.concrete());
	}

	public String toString() {
		return m_startingPoint.toString() + " to " + m_endingPoint.toString();
	}
}