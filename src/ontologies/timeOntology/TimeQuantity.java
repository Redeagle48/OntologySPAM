/*-------------------------------------------------------------------
 --                      PhD Claudia Antunes                       --
 --               PACKAGE - SEQUENTIAL PATTERN MINING              --
 --                        TIME ONTOLOGY                           --
 --                                                                --
 --                         Claudia Antunes                        --
 --                        September 2003                          --
 --------------------------------------------------------------------
 --                       TimeQuantity.java                        --
 --------------------------------------------------------------------*/
package ontologies.timeOntology;

/**
 * Time-Quantity denotes a period of time. It consists of a value and a measure.
 */
public class TimeQuantity extends Object {
	/** The time quantity itself. */
	protected int m_quantity;

	/** The unit for the quantity. */
	protected TimeUnit m_unit;

	/**
	 * 
	 */
	public TimeQuantity() {
		m_quantity = 0;
		m_unit = new TimeUnit(TimeUnit.SECOND);
	}

	/**
	 * 
	 * @param nr
	 * @param tunit
	 */
	public TimeQuantity(int nr, int tunit) {
		m_quantity = nr;
		m_unit = new TimeUnit(tunit);
	}

	/**
	 * 
	 * @param nr
	 * @param tunit
	 */
	public TimeQuantity(int nr, TimeUnit tunit) {
		m_quantity = nr;
		m_unit = tunit;
	}

	/**
	 * 
	 * @return the m_quantity
	 */
	public int getQuantity() {
		return m_quantity;
	}

	/**
	 * 
	 * @return the unit in which the time quantity is measured
	 */
	public TimeUnit getUnit() {
		return m_unit;
	}

	/**
	 * 
	 * @param tq
	 * @return true, if they are equal, or the first one is less than the received one
	 */
	public boolean isEqualOrLessThan(TimeQuantity tq) {
		if (m_unit.equals(tq.m_unit))
			return m_quantity <= tq.m_quantity;
		if (m_unit.greaterThan(tq.m_unit))
			return m_quantity * m_unit.getValueIn(tq.m_unit) <= tq.m_quantity;
		else
			return m_quantity <= tq.m_quantity * tq.m_unit.getValueIn(m_unit);
	}

	/**
	 * 
	 * @param tq
	 * @return true, if they are equal, or the first one is greater than the received one
	 */
	public boolean isEqualOrGreaterThan(TimeQuantity tq) {
		if (m_unit.equals(tq.m_unit))
			return m_quantity >= tq.m_quantity;
		if (m_unit.greaterThan(tq.m_unit))
			return m_quantity * m_unit.getValueIn(tq.m_unit) >= tq.m_quantity;
		else
			return m_quantity >= tq.m_quantity * tq.m_unit.getValueIn(m_unit);
	}

	/**
	 * 
	 * @param tq
	 * @return true, if the first one is less than the received one
	 */
	public boolean lessThan(TimeQuantity tq) {
		return !isEqualOrGreaterThan(tq);
	}

	/**
	 * 
	 * @param tq
	 * @return true, if the first one is greater than the received one
	 */
	public boolean greaterThan(TimeQuantity tq) {
		return !isEqualOrLessThan(tq);
	}

	/**
	 * Returns the time quantity that goes from this time quantity to the received one
	 * @param tq
	 * @return the time quantity that goes from this time quantity to the received one
	 */
	public TimeQuantity differenceTo(TimeQuantity tq) {
		if (m_unit.equals(tq.m_unit))
			return new TimeQuantity(Math.abs(m_quantity - tq.m_quantity),
					m_unit);
		else if (m_unit.greaterThan(tq.m_unit))
			return new TimeQuantity((int) Math.abs(m_quantity
					* m_unit.getValueIn(tq.m_unit) - tq.m_quantity), tq.m_unit);
		else
			return new TimeQuantity((int) Math.abs(tq.m_quantity
					* tq.m_unit.getValueIn(m_unit) - m_quantity), m_unit);
	}
	
	/**
	 * 
	 * @param tq
	 * @return true, if they are equal
	 */
	public boolean equals(TimeQuantity tq) {
		if (m_unit.equals(tq.m_unit))
			return m_quantity == tq.m_quantity;
		else if (m_unit.greaterThan(tq.m_unit))
			return (m_quantity * m_unit.getValueIn(tq.m_unit) == tq.m_quantity);
		else
			return tq.m_quantity * tq.m_unit.getValueIn(m_unit) == m_quantity;
	}
}
