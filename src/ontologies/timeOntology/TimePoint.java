/*-------------------------------------------------------------------
 --                      PhD Claudia Antunes                       --
 --               PACKAGE - SEQUENTIAL PATTERN MINING              --
 --                        TIME ONTOLOGY                           --
 --                                                                --
 --                         Claudia Antunes                        --
 --                        September 2003                          --
 --------------------------------------------------------------------
 --                        TimePoint.java                          --
 --------------------------------------------------------------------*/
package ontologies.timeOntology;

import java.util.*;

/**
 * A time-point is a point in real, historical time (on earth). It is
 * independent of observer and context. A time-point is not a measurement of
 * time, nor is it a specification of time. It is the point in time. The
 * time-points at which events occur can be _known_ with various degrees of
 * precision and approximation, but conceptually time-points are point-like and
 * not interval-like. That is, it doesn't make sense to talk about what happens
 * during a time-point, or how long the time-point lasts.
 */
public class TimePoint extends GregorianCalendar {
	/** Serial Version UID */
	private static final long serialVersionUID = 1544308382160602713L;

	/** The TimePoint location. */
	protected TimeQuantity m_location;

	/** The TimePoint minimum granularity. */
	protected TimeUnit m_granularity;

	/** */
	public static final int DEFAULT_INTERVAL = 1;

	/** */
	protected int[] m_newFields;

	/** Constant fields. */
	public static int SEMESTER = 18;
	/** */
	public static int QUARTER = 19;
	/** */
	public static int FIRST_SEM = 0;
	/** */
	public static int SECOND_SEM = 1;
	/** */
	public static int FIRST_Q = 0;
	/** */
	public static int SECOND_Q = 1;
	/** */
	public static int THIRD_Q = 2;
	/** */
	public static int FOURTH_Q = 3;

	private void fillWithDefaultValues() {
		clear(MONTH);
		clear(DAY_OF_MONTH);
		set(DAY_OF_MONTH, 1);
		clear(HOUR);
		clear(HOUR_OF_DAY);
		clear(MINUTE);
		clear(SECOND);
		clear(MILLISECOND);
	}

	private void fillOtherFields() {
		int month = get(MONTH);
		if (month < 3) {
			m_newFields[SEMESTER - FIELD_COUNT - 1] = FIRST_SEM;
			m_newFields[QUARTER - FIELD_COUNT - 1] = FIRST_Q;
		} else if (month < 6) {
			m_newFields[SEMESTER - FIELD_COUNT - 1] = FIRST_SEM;
			m_newFields[QUARTER - FIELD_COUNT - 1] = SECOND_Q;
		} else if (month < 9) {
			m_newFields[SEMESTER - FIELD_COUNT - 1] = SECOND_SEM;
			m_newFields[QUARTER - FIELD_COUNT - 1] = THIRD_Q;
		} else {
			m_newFields[SEMESTER - FIELD_COUNT - 1] = SECOND_SEM;
			m_newFields[QUARTER - FIELD_COUNT - 1] = FOURTH_Q;
		}
	}

	/**
	 * Constructs a default TimePoint using the current time in the default time
	 * zone with the default locale.
	 */
	public TimePoint() {
		super();
		m_newFields = new int[2];
		fillOtherFields();
		m_location = new TimeQuantity((int) (getTimeInMillis() / 1000),
				TimeUnit.SECOND);
		m_granularity = new TimeUnit();
	}

	// _____________________________________________________________________
	/**
	 * @param st
	 * @param gran
	 */
	public TimePoint(String st, TimeUnit gran) {
		super();
		fillWithDefaultValues();
		m_newFields = new int[2];
		try {
			int value = Integer.valueOf(st).intValue();
			set(unit(gran), value);
		} catch (NumberFormatException ex) {
			if (gran.equals(new TimeUnit(TimeUnit.SEMESTER))) {
				int ind = st.indexOf(":");
				set(YEAR, Integer.valueOf(st.substring(0, ind)).intValue());
				set(SEMESTER, Integer.valueOf(
						st.substring(ind + 1, st.length())).intValue());
			} else if (gran.equals(new TimeUnit(TimeUnit.DAY))) {
				// "dd/MM/YYYY HH:mm:ss";
				int ind = st.indexOf("/", 0);
				set(DAY_OF_MONTH, Integer.valueOf(st.substring(0, ind))
						.intValue());
				int ind2 = st.indexOf("/", ind + 1);
				set(MONTH, Integer.valueOf(st.substring(ind + 1, ind2))
						.intValue());
				// ind = st.indexOf(" ",ind2);
				set(YEAR, Integer.valueOf(st.substring(ind2 + 1, st.length()))
						.intValue());
				/*
				 * ind2 = st.indexOf(":",ind); set(HOUR_OF_DAY,
				 * Integer.valueOf(st.substring(ind+1,ind2)).intValue()); ind =
				 * st.indexOf(":",ind2+1); set(MINUTE,
				 * Integer.valueOf(st.substring(ind2+1,ind)).intValue());
				 * set(SECOND,
				 * Integer.valueOf(st.substring(ind+1,st.length())).intValue());
				 */}
		}
		fillOtherFields();
		m_granularity = gran;
		m_location = new TimeQuantity(gran.getValueIn(TimeUnit.SECOND),
				TimeUnit.SECOND);
	}

	// ________________________________________________________________
	/**
	 * @param gran
	 */
	public TimePoint(TimeUnit gran) {
		super();
		fillWithDefaultValues();
		if (gran.equals(new TimeUnit(TimeUnit.SEMESTER)))
			set(MONTH, SEPTEMBER);
		m_newFields = new int[2];
		fillOtherFields();
		m_granularity = gran;
		m_location = new TimeQuantity(gran.getValueIn(TimeUnit.SECOND),
				TimeUnit.SECOND);
	}

	/**
	 * @param refs
	 * @param values
	 */
	public TimePoint(Vector<TimeUnit> refs, Vector<String> values) {
		super();
		m_newFields = new int[2];
		fillWithDefaultValues();
		m_granularity = (TimeUnit) refs.elementAt(refs.size() - 1);
		int ref = YEAR;
		for (int i = 0; i < refs.size(); i++) {
			ref = unit((TimeUnit) refs.elementAt(i));
			set(ref, (new Integer((String) values.elementAt(i))).intValue());
		}
		fillOtherFields();
		m_location = new TimeQuantity(
				m_granularity.getValueIn(TimeUnit.SECOND), TimeUnit.SECOND);
	}

	/**
	 * 
	 * @param y
	 * @param mm
	 * @param d
	 * @param h
	 * @param min
	 * @param s
	 * @param wd
	 * @param wn
	 * @param sem
	 * @param q
	 * @param gran
	 */
	public TimePoint(String y, String mm, String d, String h, String min,
			String s, String wd, String wn, String sem, String q, TimeUnit gran) {
		super();
		fillWithDefaultValues();
		m_granularity = gran;
		if (!y.equals(""))
			set(YEAR, Integer.valueOf(y).intValue());
		if (!mm.equals(""))
			set(MONTH, Integer.valueOf(mm).intValue() - 1);
		if (!d.equals(""))
			set(DAY_OF_MONTH, Integer.valueOf(d).intValue());
		if (!h.equals(""))
			set(HOUR_OF_DAY, Integer.valueOf(h).intValue());
		if (!min.equals(""))
			set(MINUTE, Integer.valueOf(min).intValue());
		if (!s.equals(""))
			set(SECOND, Integer.valueOf(s).intValue());
		if (!wd.equals(""))
			set(DAY_OF_WEEK, Integer.valueOf(wd).intValue());
		if (!wn.equals(""))
			set(WEEK_OF_YEAR, Integer.valueOf(wn).intValue());
		m_newFields = new int[2];
		fillOtherFields();
		if (!sem.equals(""))
			set(SEMESTER, Integer.valueOf(sem).intValue());
		if (!q.equals(""))
			set(QUARTER, Integer.valueOf(q).intValue());
		m_location = new TimeQuantity(gran.getValueIn(TimeUnit.SECOND),
				TimeUnit.SECOND);
	}

	private int unit(TimeUnit un) {
		switch (un.getUnit()) {
		case TimeUnit.YEAR:
			return YEAR;
		case TimeUnit.MONTH:
			return MONTH;
		case TimeUnit.WEEK:
			return WEEK_OF_YEAR;
		case TimeUnit.DAY:
			return DAY_OF_MONTH;
		case TimeUnit.HOUR:
			return HOUR_OF_DAY;
		case TimeUnit.MINUTE:
			return MINUTE;
		case TimeUnit.SECOND:
			return SECOND;
		case TimeUnit.SEMESTER:
			return SEMESTER;
		case TimeUnit.QUARTER:
			return QUARTER;
		default:
			return DAY_OF_MONTH;
		}
	}

	/**
	 * 
	 * @return a time quantity
	 */
	public TimeQuantity getLocation() {
		return m_location;
	}

	/** PREDICATES */
	/**
	 * @return true, if 
	 */
	public boolean concrete() {
		// It only specifies the year.
		if (isSet(YEAR))
			return true;
		// It only specifies the year and the month.
		if (isSet(YEAR) && isSet(MONTH))
			return true;
		// It only specifies the year, month and day.
		if (isSet(YEAR) && isSet(MONTH) && isSet(DAY_OF_MONTH))
			return true;
		// It specifies the year, month, day and hour.
		if (isSet(YEAR) && isSet(MONTH) && isSet(DAY_OF_MONTH) && isSet(HOUR))
			return true;
		// Else it returns false, since the time point does not corresponds to a
		// specific real timepoint
		return false;
	}

	/** RELATIONS */
	/**
	 * @param time
	 * @return true, if this time point is equal to another received
	 */
	public boolean equals(TimePoint time) {
		boolean ok = m_location.equals(time.m_location);
		return ok;
	}

	/** FUNCTIONS */
	/**
	 * @param t
	 * @return a time quantity representing the distance from this time point to another
	 */
	public TimeQuantity differenceTo(TimePoint t) {
		return m_location.differenceTo(t.m_location);
	}

	/**
	 * 
	 * @param q
	 * @return a new time point resulting from adding a time quantity to this tp
	 */
	public TimePoint add(TimeQuantity q) {
		TimePoint nT = (TimePoint) clone();
		nT.add((int) q.getQuantity(), unit(q.getUnit()));
		return nT;
	}

	/**
	 * 
	 * @return the next time point
	 */
	public TimePoint next() {
		TimePoint nT = (TimePoint) clone();
		int gran = unit(m_granularity);
		if (gran < SEMESTER)
			nT.add(gran, DEFAULT_INTERVAL);
		else {
			if (SEMESTER == gran) {
				if (FIRST_SEM == m_newFields[SEMESTER - FIELD_COUNT - 1]) {
					nT.set(SEMESTER, SECOND_SEM);
					nT.set(MONTH, SEPTEMBER);
				} else {
					nT.set(SEMESTER, FIRST_SEM);
					nT.set(YEAR, fields[YEAR] + 1);
					nT.set(MONTH, JANUARY);
				}
			}
			// else
			// nT.add(MONTH, 3); // add 3 monthes = one quarter;
			// fillOtherFields();
		}
		return nT;
	}

	/**
	 * @param field
	 */
	public int get(int field) {
		if (field < FIELD_COUNT)
			return super.get(field);
		else {
			computeTime();
			return m_newFields[field - FIELD_COUNT - 1];
		}
	}

	/**
	 * @param field
	 * @param value
	 */
	public void set(int field, int value) {
		if (field < FIELD_COUNT)
			super.set(field, value);
		else {
			if (FIRST_SEM == value)
				set(MONTH, JANUARY);
			else
				set(MONTH, JULY);
			m_newFields[field - FIELD_COUNT - 1] = value;
		}
	}

	public String toString() {
		switch (m_granularity.getUnit()) {
		case TimeUnit.YEAR:
			return String.valueOf(get(YEAR));
		case TimeUnit.SEMESTER:
			return String.valueOf(get(YEAR)) + ":"
					+ String.valueOf(get(SEMESTER) + 1);
		case TimeUnit.QUARTER:
			return String.valueOf(get(YEAR)) + ":"
					+ String.valueOf(get(SEMESTER) + 1) + ":"
					+ String.valueOf(get(QUARTER) + 1);
		case TimeUnit.MONTH:
			return String.valueOf(get(YEAR)) + "-"
					+ String.valueOf(get(MONTH) + 1);
			// case TimeUnit.WEEK: return WEEK_OF_YEAR;
		case TimeUnit.DAY:
			return String.valueOf(get(DAY_OF_MONTH)) + "/"
					+ String.valueOf(get(MONTH) + 1) + "/"
					+ String.valueOf(get(YEAR));
		case TimeUnit.HOUR:
			return String.valueOf(get(HOUR_OF_DAY));
		case TimeUnit.MINUTE:
			return String.valueOf(get(HOUR_OF_DAY)) + ":"
					+ String.valueOf(get(MINUTE));
		case TimeUnit.SECOND:
		default:
			return String.valueOf(get(HOUR_OF_DAY)) + ":"
					+ String.valueOf(get(MINUTE)) + ":"
					+ String.valueOf(get(SECOND));
		}
	}

	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		String st = "12/04/2004 ";
		TimePoint tp = new TimePoint(st, new TimeUnit(TimeUnit.DAY));

		System.out.println(tp.toString());

		// TimePoint calendar = new TimePoint();
		// TimePoint calendar = new TimePoint("2003", "11", "5", "14", "", "",
		// "", "", "", "", new TimeUnit(TimeUnit.DAY));
		/*
		 * String year = "1993"; String sem = "2";
		 * System.out.println(year+":"+sem+"(SPRING)"); Vector refs = new
		 * Vector(2); refs.addElement(new TimeUnit(TimeUnit.YEAR));
		 * refs.addElement(new TimeUnit(TimeUnit.SEMESTER)); Vector values = new
		 * Vector(2); // For scholar years FALL semester corresponds to the 2nd
		 * semester of year // and SPRING semester corresponds to 1st semester
		 * if (sem.equals("1")){ values.addElement(year);
		 * values.addElement("2"); } else {
		 * values.addElement(String.valueOf((Integer.valueOf(year).intValue()+1)));
		 * values.addElement("1"); } TimePoint calendar = new TimePoint(refs,
		 * values); // System.out.println("ERA: " + calendar.get(Calendar.ERA));
		 * System.out.println("YEAR: " + calendar.get(Calendar.YEAR));
		 * System.out.println("MONTH: " + calendar.get(Calendar.MONTH)); /*
		 * System.out.println("WEEK_OF_YEAR: " +
		 * calendar.get(Calendar.WEEK_OF_YEAR));
		 * System.out.println("WEEK_OF_MONTH: " +
		 * calendar.get(Calendar.WEEK_OF_MONTH)); System.out.println("DATE: " +
		 * calendar.get(Calendar.DATE)); System.out.println("DAY_OF_MONTH: " +
		 * calendar.get(Calendar.DAY_OF_MONTH));
		 * System.out.println("DAY_OF_YEAR: " +
		 * calendar.get(Calendar.DAY_OF_YEAR)); System.out.println("DAY_OF_WEEK: " +
		 * calendar.get(Calendar.DAY_OF_WEEK));
		 * System.out.println("DAY_OF_WEEK_IN_MONTH: " +
		 * calendar.get(Calendar.DAY_OF_WEEK_IN_MONTH));
		 * System.out.println("AM_PM: " + calendar.get(Calendar.AM_PM));
		 * System.out.println("HOUR: " + calendar.get(Calendar.HOUR));
		 * System.out.println("HOUR_OF_DAY: " +
		 * calendar.get(Calendar.HOUR_OF_DAY)); System.out.println("MINUTE: " +
		 * calendar.get(Calendar.MINUTE)); System.out.println("SECOND: " +
		 * calendar.get(Calendar.SECOND)); System.out.println("MILLISECOND: " +
		 * calendar.get(Calendar.MILLISECOND)); System.out.println("SEMESTER: " +
		 * calendar.get(TimePoint.SEMESTER)); System.out.println("QUARTER: " +
		 * calendar.get(TimePoint.QUARTER));
		 * 
		 * System.out.println(calendar.toString());
		 */
	}
}
