/*------------------------------------------------------------------
--                  PACKAGE - SEQUENTIAL PATTERN MINING           --
--                                                                --
--                          Claudia Antunes                       --
--	                          January 2004                        --
--------------------------------------------------------------------
--                       PDATransition.java                       --
--------------------------------------------------------------------*/

package d3m.spam.constraints.contentConstraints.automata;

import d3m.spam.core.*;
import java.util.*;
/** Class <CODE>PDATransition</CODE> implements a PDA transition. 
 * Each transition consists on a tuple (s_oldEl, set, s_newEl, s_op), 
 * where set is an itemset, s_oldEl is a stack string, s_newEl is another
 * stack string and s_op is a stack operation. 
 * @version 2.0
 * @author Cláudia Antunes
 */
public class PDATransition extends ItemSet
{
    /** The stack operation to be performed. */
    int m_op;
    /** The stack element to be manipulated. */
    Vector<Element> m_newStackElems;
    /** The stack element present need to perform the transition. */
    Vector<Element> m_oldStackElems;
/**  */
public PDATransition()
{}
/** Creates a new instance of PDATransition 
 * @param oldStackElems
 * @param set The itemset responsible for this transition.
 * @param op The stack operation to be performed. 
 * @param newStackElems The stack element to be manipulated. 
 */
public PDATransition(Vector<Element> oldStackElems, ItemSet set, int op, 
		Vector<Element> newStackElems) 
{
	super(set);
    m_oldStackElems = oldStackElems;
    m_newStackElems = newStackElems;
    m_op = op;
}

/** 
 * @return Returns the element responsible for this transition. */
/*public ItemSet getSet()
{   return ;  }
*/
/** @return Returns the stack operation to be performed. */
public int getOp()
{   return m_op; }

/** @return Returns the stack element to be manipulated. */
public Vector<Element> getNewStackElems()
{   return m_newStackElems;  }

/** @return Returns the stack element to be manipulated. */
public Vector<Element> getOldStackElems()
{   return m_oldStackElems;  }

/**
 * 
 * @return a representation of the PDA stack operation
 */
protected String opToString()
{
	switch (m_op){
		case PDAStack.POP: return "pop()";
		case PDAStack.PUSH: 
			String st="push("+m_newStackElems.elementAt(0).toString();
			for (int i=1; i<m_newStackElems.size();i++)
				st+=","+m_newStackElems.elementAt(i).toString();
			return st+")";
		case PDAStack.NO_OP: 
		default:
			return "noop()";
	}
}

@Override
public String toString()
{
	String st = "["+super.toString()+":("+m_oldStackElems.elementAt(0).toString();
	for (int i=1; i<m_oldStackElems.size();i++)
		st+=","+m_oldStackElems.elementAt(i).toString();
	return st+")->"+ opToString() + "]";
}

}
