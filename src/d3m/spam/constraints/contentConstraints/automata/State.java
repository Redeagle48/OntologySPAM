/*------------------------------------------------------------------
--                  PACKAGE - SEQUENTIAL PATTERN MINING           --
--                                MACHINES                        --
--                                                                --
--                       Cláudia M. Antunes                       --
--	                       December 2003                          --
--------------------------------------------------------------------
--                          State.java                            --
--------------------------------------------------------------------*/

package d3m.spam.constraints.contentConstraints.automata;

import d3m.spam.core.ItemSet;
import java.util.*;
//--------------------------------------------------------------------
//                         CLASS State
//--------------------------------------------------------------------
/** Class <CODE>State</CODE> implements a DFA state.
 * The transitions for a state is a vector of elements responsible for going
 * from this state to all states of the automata.
 * For example, for the automata equivalent to 1*(22|234|44), transitions for  
 * each state are: <br>
 * <table align=center><tr>
 * <td><table border><tr><td colspan=4>for q0</td></tr>
 *      <tr><td>q0</td><td>q1</td><td>q2</td><td>q3</td></tr>
 *      <tr><td>1</td><td>2</td><td>4</td><td>&nbsp;</td></tr>
 * </table></td>
 * <td><table border><tr><td colspan=4>for q1</td></tr>
 *      <tr><td>q0</td><td>q1</td><td>q2</td><td>q3</td></tr>
 *      <tr><td>&nbsp;</td><td>&nbsp;</td><td>3</td><td>2</td></tr>
 * </table></td>
 * <td><table border><tr><td colspan=4>for q2</td></tr>
 *      <tr><td>q0</td><td>q1</td><td>q2</td><td>q3</td></tr>
 *      <tr><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>4</td></tr>
 * </table></td>
 * <td><table border><tr><td colspan=4>for q3</td></tr>
 *      <tr><td>q0</td><td>q1</td><td>q2</td><td>q3</td></tr>
 *      <tr><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td></tr>
 * </table></td></tr></table>
 * @version 1.0
 * @author Cláudia Antunes
 */
public class State 
{
    /** The state internal id */
    protected int m_id;
    /** The state external id */
    protected String m_idString;
	/** True if this is a final state */
    protected boolean m_isFinal = false;  
    /** A vector with the transitions allowed from this state. 
	 Several transitions from one state to another are allowed */
    protected Vector<Vector<ItemSet>> m_transitions;    
    /**     */
	public Vector<Vector<ItemSet>> m_from; 

	/** Creates a new empty instance of State */
    public State() 
    {   
		m_transitions = new Vector<Vector<ItemSet>>();    
		m_from = new Vector<Vector<ItemSet>>();    
	}   

    /** Creates a new instance of State, with the following parameters
     * @param id the internal state id
     * @param idString the external state id
     * @param isFinal true if it is a final state
     * @param transitions A vector with state transitions
     */    
    public State(int id, String idString, boolean isFinal, 
    		Vector<Vector<ItemSet>> transitions)
    {
        m_id = id;
        m_idString = idString;
        m_isFinal = isFinal;
        m_transitions = transitions;
		m_from = new Vector<Vector<ItemSet>>();
    }
    
    /**
     * 
     * @param states
     */
	public void setFrom(Vector<State> states)
	{
		for (int i=0; i<m_transitions.size();i++){
			State state = states.elementAt(i);
			state.m_from.insertElementAt(m_transitions.elementAt(i),m_id);
		}
	}

	/** 
     * Verifies if it is a final state.
     * @return true if it is a final state
     */
    public boolean isFinal()
    {   return m_isFinal;    }

    /** 
	 * @return Return the transitions from this state. 
	 */
	public Vector<Vector<ItemSet>> getTransitions()
	{	return m_transitions;	}

	/**
	 * 
	 * @return the name for this state
	 */
	public String getName()
	{	return m_idString;	}

	/**
     * Returns the id of the state achieved by set when applied in this state
     * @param set
     * @return Returns -1 when there isn't such state
     */
	public int stateAchievedBy (ItemSet set)
	{
		for (int i=0; i<m_transitions.size(); i++) {
			Vector<ItemSet> v = m_transitions.elementAt(i);
			for (int j=0; j<v.size(); j++) {
				ItemSet setI = v.elementAt(j);
				if ((null!=setI)&&(set.equals(setI)))
					return i;
			}
		}
		return -1;
	}

	/**
	 * 
	 * @param set
	 * @return the state reached
	 */
	public int legalStateAchievedByPrefix (ItemSet set)
	{
		for (int i=0; i<m_transitions.size(); i++) {
			Vector<ItemSet> v = m_transitions.elementAt(i);
			for (int j=0; j<v.size(); j++) {
				ItemSet transSet = v.elementAt(j);
				if (set.isSuffixOf(transSet)) return i;
				else if (set.isSubsetOf(transSet)) return m_id;
			}
		}
		return -1;
	}

	/**
	 * 
	 * @param set
	 * @param first
	 * @return the state reached
	 */
	public int legalStateAchievedBy (ItemSet set, boolean first)
	{
		for (int i=0; i<m_transitions.size(); i++) {
			Vector<ItemSet> v = m_transitions.elementAt(i);
			for (int j=0; j<v.size(); j++) {
				ItemSet transSet = v.elementAt(j);
				if (isLegalApplicable(transSet, set, first))
					return i;
			}
		}
		return -1;
	}
	
	
	private boolean isLegalApplicable(ItemSet transSet, ItemSet set, boolean first)
	{
		if (first)
			return set.isSuffixOf(transSet);
		else 
			return set.equals(transSet);
	}

	/**
     * Tests the equality bettween this state and the object obj
     */
    @Override
	public boolean equals(Object obj)
    {
        State state = (State) obj;
        return this.m_id == state.m_id;
    }
    
}
