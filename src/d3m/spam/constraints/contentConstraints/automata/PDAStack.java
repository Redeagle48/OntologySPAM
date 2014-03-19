/*------------------------------------------------------------------
--                  PACKAGE - SEQUENTIAL PATTERN MINING           --
--                                                                --
--                          Claudia Antunes                       --
--	                          January 2004                        --
--------------------------------------------------------------------
--                         PDAStack.java                          --
--------------------------------------------------------------------*/

package d3m.spam.constraints.contentConstraints.automata;

import d3m.spam.core.Element;
import d3m.spam.core.ItemSet;
import java.util.*;
//--------------------------------------------------------------------
//                         CLASS PDAStack
//--------------------------------------------------------------------
/** Class <CODE>PDAStack</CODE> implements a stack for simulate the PDA 
 * functionality.  
 * @version 1.0
 * @author Cláudia Antunes
 */
public class PDAStack
{
    /** Refers to the inexistence of a stack operation */
    public static final int NO_OP = 0;
    /** Refers to the push operation, ie, the addition of an element 
     *  to the top of the stack. */
    public static final int PUSH = 1;
    /** Refers to the pop operation, ie, the removal of an element 
     *  from the top of the stack. */
    public static final int POP = 2;
    /** The stack, it self. */
    public Vector<Vector<Element>> m_elements;
    

/** Creates a new empty stack. */
public PDAStack()
{   m_elements = new Vector<Vector<Element>>(0);  }

/** 
 * Creates a new stack, with element el on the top. 
 * @param el
 */
public PDAStack(Element el)
{   
	m_elements = new Vector<Vector<Element>>(1); 
	Vector<Element> elems = new Vector<Element>(1);
	elems.addElement(el);
	m_elements.addElement(elems);
}

/** Creates a copy of the current stack. 
 * @param st
 */
public PDAStack(PDAStack st)
{   
	m_elements = new Vector<Vector<Element>>();
	for (int i=0; i<st.m_elements.size(); i++){
	    @SuppressWarnings("unchecked")
	    Vector<Element> v = (Vector<Element>)(st.m_elements.elementAt(i)).clone();
		m_elements.addElement(v);
	}
}

/** The push operation, ie, the addition of an element to the top of the stack. 
 * @param symbols
 */
public void push (Vector<Element> symbols)
{   m_elements.addElement(symbols);  }

/**
 * 
 * @param symbol
 */
public void addToLast(Element symbol)
{
	Vector<Element> elems = m_elements.lastElement();
	elems.addElement(symbol);
	m_elements.setElementAt(elems, m_elements.size()-1);
}

/** The pop operation, ie, the removal of an element 
*  from the top of the stack. */
public void pop ()
{   
	if (!m_elements.isEmpty()) 
		m_elements.remove(m_elements.size()-1);    
}

/**
 * 
 */
public void removeFromLast()
{
	if (!m_elements.isEmpty()){
		Vector<Element> elems = m_elements.lastElement();
		if (!elems.isEmpty()){
			elems.removeElementAt(0);
			m_elements.setElementAt(elems, m_elements.size()-1);	
		}
	}
}

/** @return Returns the size of the stack, ie, the number of elements in the stack. */
public int size()
{   return m_elements.size();   }

/** @return Returns the element on the top of the stack. */
public Vector<Element> top ()
{   return m_elements.lastElement();   }

/**
 * 
 * @return true, if it is empty
 */
public boolean isEmpty()
{   return 0==m_elements.size();    }

/**
 * Verifies if a transition may be applicabe with the presence of si and the current stack
 * @param trans
 * @return true, if the transition is applicable
 */
public boolean isApplicable(PDATransition trans) 
{
	return (null!= trans) && top().equals(trans.getOldStackElems());
}

/**
 * 
 * @param trans
 * @param si
 * @return true, if the transition is applicable
 */
public boolean isApplicable(PDATransition trans, ItemSet si) 
{
	return isApplicable(trans) && si.equals(trans);
}

/**
 * Verifies if a transition may be applicabe with the presence of si 
 * and the current stack
 * @param trans
 * @param si
 * @return true, if the received transition i applicable with si in the top
 */
public boolean isPartiallyApplicable(PDATransition trans, ItemSet si) 
{
	if (null==trans || !si.isPrefixOf(trans))
		return false;
	int n = si.size();
	if (1==n || POP!=trans.getOp())
		return  top().equals(trans.getOldStackElems());
	else
		return  top().size()==0 || 
				(top().elementAt(0)).equals(trans.getOldStackElems().lastElement());
}

/**
 * It ignores the case of prefixes
 * @param trans
 * @param si
 * @param first
 * @param transStack
 * @return true, if the received transition i applicable with si in the top
 */
public boolean isLegalApplicable(PDATransition trans, ItemSet si, boolean first, 
		Stack<PDATransition> transStack) 
{
	if (null==trans) return false;
	// if it is the first set on the sequence it can be a suffix of si,
	// and the stack is empty
	if (first)
		return si.isSuffixOf(trans);
	else {
		if (!si.equals(trans))
			return false;
		if (!isEmpty())
			return top().equals(trans.getOldStackElems());
		else
			// If it is a PUSH or NOOP, then the top of the stack has to be equal to:
			// the old stack elems if the previous transition was a NOOP
//			return trans.getOldStackElems().equals(((PDATransition)transStack.peek()).getOldStackElems());
	return true;
	}
}

/**
 * Verifies if a transition may be applicabe with the presence of si and the current stack
 * @param trans
 * @param si
 * @param transStack
 * @return true, if a transition may be applicabe with the presence of si and the current stack
 */
public boolean isPartiallyLegalApplicable(PDATransition trans, ItemSet si, Stack<PDATransition> transStack) 
{
	if (null==trans || !si.isPrefixOf(trans))
		return false;
	int n = si.size();
	if (!isEmpty()){
		if (1==n || POP!=trans.getOp())
			return top().equals(trans.getOldStackElems());	
		else
			return top().size()==0 || 
				(top().elementAt(0)).equals(trans.getOldStackElems().lastElement());
	}
	else return trans.getOldStackElems().equals((transStack.peek()).getOldStackElems());
}

/**
 * Performs the stack operation
 * @param trans
 */
public void performOp(PDATransition trans)
{
	switch (trans.getOp()) {
		case NO_OP: 
			break;
		case PUSH:  
			push(trans.getNewStackElems());
			break;
		case POP:   
			pop();
			break;
	}
}

/**
 * 
 * @param trans
 * @param set
 */
public void performPartialOp(PDATransition trans, ItemSet set)
{
	int size = set.size();
	switch (trans.getOp()) {
		case NO_OP: 
			break;
		case PUSH:  
			// It's the first element in the basket transition
			// then push a new element on the stack
			if (1==size){
				Vector<Element> newV = new Vector<Element>(1);
				newV.addElement((trans.getNewStackElems()).elementAt(0));
				push(newV);
			}
			// It's another symbol in the same transition
			else 
				addToLast((trans.getNewStackElems()).lastElement());
			break;
		case POP:
			// If it's the last symbol in the transition pop the stack
			if (size == ((ItemSet)trans).size())
				pop();
			// Else remove another symbol from the top of the stack
			else
				removeFromLast();
			break;    
	} 
}	

@Override
public String toString()
{	return m_elements.toString();	}

}