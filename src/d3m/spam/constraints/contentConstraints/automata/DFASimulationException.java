/*
 * DFASimulateException.java
 *
 * Created on February 15, 2004, 8:04 PM
 */

package d3m.spam.constraints.contentConstraints.automata;

/**
 * Class <CODE>DFASimulationException</CODE>
 * @author  Claudia Antunes
 */
public class DFASimulationException extends Exception
{
	private static final long serialVersionUID = 0;
	/** Creates a new instance of DFASimulateException */
	public DFASimulationException() {
		super();
	}
	/** Creates a new instance of DFASimulateException 
	 * @param st
	 */
	public DFASimulationException(String st) {
		super(st);
	}
	
}
