/**
 * 
 */
package d3m.spam.constraints.contentConstraints.automata;

import java.util.Stack;
import java.util.Vector;

import d3m.spam.core.ItemSet;

/**
 * @author Claudia Antunes
 *
 */
public class PDAState extends State {
    /** A vector with the transitions allowed from this state. 
	 Several transitions from one state to another are allowed */
   protected Vector<Vector<PDATransition>> m_transitions;    
   /**     */
	public Vector<Vector<PDATransition>> m_from; 

	/**
	 * 
	 */
	public PDAState() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param id
	 * @param idString
	 * @param isFinal
	 * @param transitions
	 */
	public PDAState(int id, String idString, boolean isFinal,
			Vector<Vector<PDATransition>> transitions) {
        m_id = id;
        m_idString = idString;
        m_isFinal = isFinal;
        m_transitions = transitions;
		m_from = new Vector<Vector<PDATransition>>();
	}

    /** 
	 * @return Return the transitions from this state. 
	 */
	public Vector<Vector<PDATransition>> getPDATransitions()
	{	return m_transitions;	}
    
	/**
     * 
     * @param states
     */
	public void setPDAFrom(Vector<PDAState> states)
	{
		for (int i=0; i<m_transitions.size();i++){
			PDAState state = states.elementAt(i);
			state.m_from.insertElementAt(m_transitions.elementAt(i),m_id);
		}
	}
	
	/**
	 * The same for deterministic PDAs
	 * @param set
	 * @param stack
	 * @return the id for the state achieved
	 */
	public int stateAchievedBy (ItemSet set, PDAStack stack)
	{
		for (int i=0; i<m_transitions.size(); i++) {
			Vector<PDATransition> v = m_transitions.elementAt(i);
			for (int j=0; j<v.size(); j++) {
				PDATransition trans = v.elementAt(j);
				if (stack.isApplicable(trans, set)){
					stack.performOp(trans);
					return i;
				}
			}
		}
		return -1;
	}

	
	/**
	 * 	The same for deterministic PDAs
	 * @param set
	 * @param stack
	 * @param first
	 * @param transStack
	 * @return ????
	 */
	public int legalStateAchievedBy (ItemSet set, PDAStack stack, boolean first, 
			Stack<PDATransition> transStack)
	{
		for (int i=0; i<m_transitions.size(); i++) {
			Vector<PDATransition> v = m_transitions.elementAt(i);
			for (int j=0; j<v.size(); j++) {
				PDATransition trans = v.elementAt(j);
				if (stack.isLegalApplicable(trans, set, first, transStack)){
					stack.performOp(trans);
					transStack.push(trans);
					return i;
				}
			}
		}
		return -1;
	}

    /**
     * 
     * @param stId
     * @param stack
     * @param set
     * @return the transition to apply
     */
    public PDATransition getApplicableTo(int stId, PDAStack stack, ItemSet set)
    {
    	Vector<PDATransition> transV = m_transitions.elementAt(stId);
    	for (int i=0; i<transV.size(); i++) {
    		PDATransition trans = transV.elementAt(i);
    		if (stack.isApplicable(trans, set))
    			return trans;
    	}
    	return null;	
    }

}
