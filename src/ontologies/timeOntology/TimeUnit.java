/*-------------------------------------------------------------------
 --                      PhD Claudia Antunes                       --
 --               PACKAGE - SEQUENTIAL PATTERN MINING              --
 --                        TIME ONTOLOGY                           --
 --                                                                --
 --                         Claudia Antunes                        --
 --                        September 2003                          --
 --------------------------------------------------------------------
 --                         TimeUnit.java                          --
 --------------------------------------------------------------------*/
package ontologies.timeOntology;

import java.util.*;

/**
 * Class for defining the possible time units.
 */
public class TimeUnit {
	/** */
	public static final int YEAR = 0;

	/** */
	public static final int MONTH = 1;

	/** */
	public static final int WEEK = 2;

	/** */
	public static final int DAY = 3;

	/** */
	public static final int HOUR = 4;

	/** */
	public static final int MINUTE = 5;

	/** */
	public static final int SECOND = 6;

	/** */
	public static final int SEMESTER = 7;

	/** */
	public static final int QUARTER = 8;

	/** The equivalences between time units. Values for month are approximations */
	static protected int[][] m_equivalences = {
			{ 1, 12, 52, 365, 8760, 525600, 31536000, 2 },
			{ 0, 1, 4, 30, 720, 43200, 2592000, 0 },
			{ 0, 0, 1, 7, 168, 1080, 64800, 0 },
			{ 0, 0, 0, 1, 24, 1440, 86400, 0 }, { 0, 0, 0, 0, 1, 60, 3600, 0 },
			{ 0, 0, 0, 0, 0, 1, 60, 0 }, { 0, 0, 0, 0, 0, 0, 1, 0 },
			{ 0, 6, 24, 183, 4380, 262800, 15768000, 1 } };

	/** The time unit. */
	protected int m_unit;

	// _____________________________________________________________________
	/**
	 * 
	 */
	public TimeUnit() {
		m_unit = TimeUnit.DAY;
	}

	/**
	 * 
	 * @param unit
	 */
	public TimeUnit(int unit) {
		m_unit = unit;
	}

	/**
	 * 
	 * @return the m_unit
	 */
	public int getUnit() {
		return m_unit;
	}

	/**
	 * 
	 * @param unit
	 * @return the equivalent time unit in the received unit
	 */
	public int getValueIn(TimeUnit unit) {
		return TimeUnit.m_equivalences[m_unit][unit.m_unit];
	}

	/**
	 * 
	 * @param unit
	 * @return the equivalent time unit in the received unit
	 */
	public int getValueIn(int unit) {
		return TimeUnit.m_equivalences[m_unit][unit];
	}

	/**
	 * 
	 * @param un
	 * @return true, if they are equal
	 */
	public boolean equals(TimeUnit un) {
		return m_unit == un.m_unit;
	}

	/**
	 * 
	 * @param un
	 * @return true, if the first one is less than the received one
	 */
	public boolean lessThan(TimeUnit un) {
		return m_unit < un.m_unit;
	}

	/**
	 * 
	 * @param un
	 * @return true, if the first one is greater than the received one
	 */
	public boolean greaterThan(TimeUnit un) {
		return m_unit > un.m_unit;
	}

	/**
	 * Returns the larger of the two received time units
	 * @param t1
	 * @param t2
	 * @return the larger of the two received time units
	 */
	static public TimeUnit max(TimeUnit t1, TimeUnit t2) {
		if (m_equivalences[t1.m_unit][t2.m_unit] > 0)
			return t1;
		else
			return t2;
	}

	/**
	 * 
	 * @return the set of available granularities
	 */
	static public Vector<String> getTimeGranularities() {
		Vector<String> v = new Vector<String>();
		v.addElement("Year");
		v.addElement("Month");
		v.addElement("Week");
		v.addElement("Day");
		v.addElement("Hour");
		v.addElement("Minute");
		v.addElement("Second");
		v.addElement("Semester");
		v.addElement("Quarter");
		return v;
	}
	// _____________________________________________________________________
}
