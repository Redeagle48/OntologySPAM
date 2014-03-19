/*------------------------------------------------------------------
--                  PACKAGE - SEQUENTIAL PATTERN MINING           --
--                                                                --
--                          Claudia Antunes                       --
--	                          January 2004                        --
--------------------------------------------------------------------
--                             NPDA.java                          --
--------------------------------------------------------------------*/

package d3m.spam.constraints.contentConstraints.automata;

import d3m.spam.core.*;
import java.util.*;

//--------------------------------------------------------------------
//                         CLASS NPDA
//--------------------------------------------------------------------
/** Class <CODE>NPDA</CODE> implements a non-deterministic Pushdown Automaton,  
 * that manipulates itemsets.
 * @version 2.0
 * @author Cláudia Antunes
 */
public class NPDA extends PDA
{

/** Creates a new empty instance of PDA. */
public NPDA() 
{
	super();
}

/**
 * 
 * @param filename
 * @param alphabet
 */
public NPDA(String filename, Vector<Element> alphabet)
{
	super(filename, alphabet);
}

/** Creates a new instance of PDA, with the specified parameters. 
 * @param states The set of states.
 * @param alphabet The alphabet accepted by the automata.
 * @param stackAlphabet The stack alphabet.
 * @param startStateId The initial state id.
 * @param stackStartSymbol The start symbol for the stack.
 */
public NPDA(Vector<PDAState> states, Vector<Element> alphabet, Vector<Element> stackAlphabet, 
		int startStateId, Element stackStartSymbol)
{
	super(states, alphabet, stackAlphabet, startStateId, stackStartSymbol); 
}

@Override
public boolean acceptsToJoinToAlpha(EventSequence s, Element el, boolean inParallel)
{
	PDAStack stack = new PDAStack(m_stackStartSymbol);
	if (inParallel) {
		ItemSet set = new ItemSet(s.elementAtIndex(s.length()-1).getSet());
		if (el.isGreaterThan(set.elementAt(set.size()-1))) {
			EventSequence ss = (EventSequence)s.clone();
			ss = ss.addElement(el); 
			return (-1!= auxAccPrefix(ss, m_startStateId, stack, 0, ss.length()-1));
		}
	}
	else {
		EventSequence b = new EventSequence(new Event(new ItemSet(el), s.getNextInstant()));
		EventSequence ss = s.concatenateWith(b);
		return (-1!= auxAccPrefix(ss, m_startStateId, stack, 0, ss.length()-1));
	}
	return false;
}
//________________________________________________________________
//						ACCEPTS
//________________________________________________________________
@Override
public boolean accepts(EventSequence s)
{
	PDAStack stack = new PDAStack(m_stackStartSymbol);
	return (-1!= auxAcc(s, m_startStateId, stack, 0, s.length()-1));
}

private int auxAcc(EventSequence s, int fromSt, PDAStack stack, int init, int to)
{
	if (isInAcceptingState((PDAState)getState(fromSt), stack))
		return fromSt;
	else if (init>to)
		return -1;
	else {
		ItemSet si = s.elementAtIndex(init).getSet();
		int next = -1;
		Vector<Vector<PDATransition>> transStates = ((PDAState)getState(fromSt)).getPDATransitions();
		for (int j=0; j<transStates.size(); j++) {
			Vector<PDATransition> transV = transStates.elementAt(j);
			for (int i=0; i<transV.size(); i++) {
				PDATransition trans = transV.elementAt(i);
				if (stack.isApplicable(trans, si)) {
					PDAStack stack2 = new PDAStack(stack);
					stack2.performOp(trans);
					next = auxAcc(s, j, stack2, init+1, to);
				}
				if (-1!=next)
					return next;
			}
		}
	}
	return -1;
}
//________________________________________________________________
//						ACCEPTS AS PREFIX
//________________________________________________________________
@Override
public boolean acceptsAsPrefix(EventSequence s)
{
	PDAStack stack = new PDAStack(m_stackStartSymbol);
	return (-1!= auxAccPrefix(s, m_startStateId, stack, 0, s.length()-1));
}

private int auxAccPrefix(EventSequence s, int fromSt, PDAStack stack, int init, int to)
{
	if (init>to)
		return -1;
	// It is necessary to verify if the last set is potentially accepted
	else if (init == to)
		return acceptsSetAfterState(fromSt,stack,s.elementAtIndex(init).getSet());
	else {
		ItemSet si = s.elementAtIndex(init).getSet();
		int next = -1;
		Vector<Vector<PDATransition>> transStates = ((PDAState)getState(fromSt)).getPDATransitions();
		for (int j=0; j<transStates.size(); j++) {
			Vector<PDATransition> transV = transStates.elementAt(j);
			for (int i=0; i<transV.size(); i++) {
				PDATransition trans = transV.elementAt(i);
				if (stack.isApplicable(trans, si)) {
					PDAStack stack2 = new PDAStack(stack);
					stack2.performOp(trans);
					next = auxAccPrefix(s, j, stack2, init+1, to);
				}
				if (-1!=next)
					return next;
			}
		}
	}
	return -1;
}

@Override
protected int acceptsSetAfterState(int stId, PDAStack stack, ItemSet sk)
{
	Vector<Vector<PDATransition>> transStates = ((PDAState)getState(stId)).getPDATransitions();
	for (int j=0; j<transStates.size(); j++){
		Vector<PDATransition> transV = transStates.elementAt(j);
		for (int i=0; i<transV.size(); i++) {
			PDATransition trans = transV.elementAt(i);
			if (null!=sk && stack.isPartiallyApplicable(trans, sk))
				return j;
		}
	}
    return -1;
}
//________________________________________________________________
//						ACCEPTS AS PREFIX
//________________________________________________________________
/*public boolean acceptsAsLegal(EventSequence s)
{
	PDAStack stack = new PDAStack(m_stackStartSymbol);
	for (int stId=0; stId<m_states.size(); stId++){
		int achieved = auxAccLegal(s, stId, stack, 0, s.length()-1);
		if (-1 != achieved)
			return true;
	}
	return false;
}*/

/*private int auxAccLegal(EventSequence s, int fromSt, PDAStack stack, int init, int to)
{
	if (init>to)
		return -1;
	// It is necessary to verify if the last set is potentially accepted
	else if (init == to)
		return acceptsSetAfterState(fromSt,stack,s.elementAtIndex(init).getSet());
	else {
		ItemSet si = s.elementAtIndex(init).getSet();
		int next = -1;
		Vector transStates = getState(fromSt).getTransitions();
		for (int j=0; j<transStates.size(); j++) {
			Vector transV = (Vector)transStates.elementAt(j);
			for (int i=0; i<transV.size(); i++) {
				PDATransition trans = (PDATransition) transV.elementAt(i);
				if (stack.isApplicable(trans, si)) {
					PDAStack stack2 = new PDAStack(stack);
					stack2.performOp(trans);
					next = auxAccLegal(s, j, stack2, init+1, to);
				}
				if (-1!=next)
					return next;
			}
		}
	}
	return -1;
}*/

@Override
public void simulateSequence(EventSequence alpha) throws DFASimulationException
{
// To be defined	
}

@Override
public String toString()
{	return "NPDA";	}

/*public static void main (String[] args)
{
	try{
		EventSequencesReader reader = new EventSequencesReader("D://Adinha//ID//seqs//palindromes2.txt", 
																new TimeUnit(TimeUnit.YEAR));
//		EventSequencesReader reader = new EventSequencesReader("D://Adinha//ID//seqs//baskets.example.txt", 
//													new TimeUnit(TimeUnit.YEAR));
		Database db = reader.getSequences();
//		PDA pda = new PDA("D://ID//Projects//cConstraints//pdafig42.txt",
		NPDA npda = new NPDA("D://Adinha//ID//cConstraints//npdaPalindromes.txt", db.getAlphabet());
		for (int i=0; i<db.getSize(); i++){
			EventSequence s = (EventSequence)db.elementAt(i);
//			EventSequence s = new EventSequence("<[(a,o) 4],[(b,e) 5],[(b,e) 6]>", db.getAlphabet(), new TimeUnit(TimeUnit.YEAR));
//			pda.simulateSubSequence(s);
			System.out.println(s.toString()+"->"+String.valueOf(npda.accepts(s)));
//			System.out.println(s.toString()+"->"+String.valueOf(pda.acceptsAsPrefix(s)));
//			System.out.println(s.toString()+"->"+String.valueOf(pda.acceptsAsLegal(s)));
//			System.out.println(s.toString()+"->"+String.valueOf(pda.aproxAccepts(s,1)));
//			System.out.println(s.toString()+"->"+String.valueOf(pda.aproxAcceptsPrefix(s, new Element("e",0), true, 1)));
		}
	}catch (Exception ex)
	{	ex.printStackTrace();	}
}*/
//_________________________________________________________________________
}
