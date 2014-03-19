/*------------------------------------------------------------------
--                  PACKAGE - SEQUENTIAL PATTERN MINING           --
--                                                                --
--                          Claudia Antunes                       --
--	                          January 2004                        --
--------------------------------------------------------------------
--                              PDA.java                          --
--------------------------------------------------------------------*/

package d3m.spam.constraints.contentConstraints.automata;
import d3m.spam.core.*;
import java.util.*;
import java.io.*;
//--------------------------------------------------------------------
//                         CLASS PDA
//--------------------------------------------------------------------
/** Class <CODE>PDA</CODE> implements a PushDown Automata,
 * whose transitions are performed by itemsets.
 * @version 1.0
 * @author Cláudia Antunes
 */
public class PDA extends DFA
{
	private static String STACK = "<stack alphabet=";
    /** The set of states */
    protected Vector<PDAState> m_states;
    /** The stack alphabet. */
    protected Vector<Element> m_stackAlphabet;
    /** The start symbol for the stack. */
    protected Element m_stackStartSymbol;
	/** The stack achieved by simulating one sequence without one itemset. */
	protected PDAStack m_stackAchievedByPrefix;
	/** The stack achieved by simulating one sequence. */
	protected PDAStack m_stackAchievedBySeq;
	/** */
	protected int m_stateAchievedBySeq;
	/** */
	protected Stack<PDATransition> m_transStack;
	/** */
	protected Stack<Integer> m_statesStack;

/** Creates a new empty instance of PDA */
public PDA()
{
	super();
	m_stackAlphabet = new Vector<Element>();
}

/** Creates a new instance of PDA, with the specified parameters. 
 * @param states The set of states.
 * @param alphabet The alphabet accepted by the automata.
 * @param stackAlphabet The stack alphabet.
 * @param startStateId The initial state id.
 * @param stackStartSymbol The start symbol for the stack.
 */
public PDA(Vector<PDAState> states, Vector<Element> alphabet, 
		Vector<Element> stackAlphabet, int startStateId, Element stackStartSymbol)
{
	m_states = states;
	m_alphabet = alphabet;
	m_startStateId = startStateId;
	m_stackAlphabet = stackAlphabet;
	m_stackStartSymbol = stackStartSymbol;
	fillFrom();
}

/**
 * 
 */
protected void fillFrom()
{
	for (int i=0;i<m_states.size(); i++){
		PDAState state = m_states.elementAt(i);
		state.setPDAFrom(m_states);
	}
}

/**
 * 
 * @param filename
 * @param alphabet
 */
public PDA(String filename, Vector<Element> alphabet)
{
	m_states = new Vector<PDAState>();
	m_alphabet = new Vector<Element>();
	m_stackAlphabet = new Vector<Element>(0);
	try {
		FileReader in = new FileReader(filename);
        BufferedReader dataIn = new BufferedReader(in);
		int last;
		// Read the stack alpahbet
        String line = dataIn.readLine();
		if ((null!=line)&&(line.startsWith(STACK+"["))){
			int first = line.indexOf("[");
			int i=-1;
			while (-1!=(last=line.indexOf(", ", first+1))){
				m_stackAlphabet.addElement(new Element(line.substring(first+1, last), ++i));
				first = last+1;
			}
			m_stackAlphabet.addElement(new Element(line.substring(first+1, line.indexOf("]")), ++i));
			m_stackStartSymbol = m_stackAlphabet.elementAt(0);
		}
		// Read states and transitions
		String name = "";
		boolean finalState = false; int id=-1;
		Vector<Vector<PDATransition>> transV = new Vector<Vector<PDATransition>>(0);
        while (null != (line=dataIn.readLine())) {
			if (line.startsWith(TRANS))	{
				int first = line.indexOf("\"");
				last = line.indexOf("\"",first+1);
				String st = line.substring(first+1, last);
				transV.addElement(readTransitions(st, alphabet));
			}
			else if (line.startsWith(STATE)){
				id++;
				int first = line.indexOf("name=\"");
				last = line.indexOf("\"", first+6);
				name = line.substring(first+6, last);
				if (-1!=line.indexOf(INIT)) m_startStateId = id;
				finalState = (-1!=line.indexOf(FINAL));
				transV = new Vector<Vector<PDATransition>>();
			}
			else if(line.startsWith(END_STATE))
			{	m_states.addElement(new PDAState(id, name, finalState, transV));	}
		}
		fillFrom();
		in.close();
        dataIn.close();
	} catch (Exception ex)
	{ ex.printStackTrace();	}	
}

// Create the transitions from one state to another
private Vector<PDATransition> readTransitions(String line, Vector<Element> symbols)
{
	String OP = "->";
	String pb = "[";
	String pe = "]";
	String SEP = ":";
	String right = ")";
	String left = "(";
	Vector<PDATransition> trans = new Vector<PDATransition>(1);
	int first = -1;
	// It found another one
	while (-1!=(first = line.indexOf(pb, first+1))){
		int last = line.indexOf(pe, first);
		String st = line.substring(first+1, last);
		ItemSet set = new ItemSet(st.substring(st.indexOf(left,0),
											   st.indexOf(right,0)),
								  symbols);
		for (int i=0; i<set.size(); i++){
			Element el = set.elementAt(i);
			if (null==Element.getElement(m_alphabet, el.getName()))
				m_alphabet.addElement(el);
		}
		int sep = line.indexOf(SEP, first);
		int arrow = line.indexOf(OP, first)+2;
		Vector<Element> old = readStackElems(line.substring(sep+1, arrow-2));
		first = line.indexOf(left, arrow);
		last = line.indexOf(right, first);
		Vector<Element> newSt = readStackElems(line.substring(first, last+1));
		int op;
		String stOp = line.substring(arrow, first);
		if (stOp.equals("push"))
			op = PDAStack.PUSH;
		else if (stOp.equals("pop"))
			op = PDAStack.POP;
		else
			op = PDAStack.NO_OP;
		trans.addElement(new PDATransition(old, set, op, newSt));
	}
	return trans;
}

/**
 * 
 * @param line
 * @return the set of elements in the stack
 */
protected Vector<Element> readStackElems(String line)
{
	Vector<Element> v = new Vector<Element>(1);
	int first=0;
	int last = 0;
	while (-1!=(last=line.indexOf(",",first+1))) {
		v.addElement(Element.getElement(m_stackAlphabet, line.substring(first+1, last)));	
		first = last;
	}
	v.addElement(Element.getElement(m_stackAlphabet, line.substring(first+1, line.length()-1)));		
	return v;
}

@Override
public void write(String filename)
{
	try{
	FileWriter out = new FileWriter(filename, true);
	out.write(STACK+m_stackAlphabet.toString()+">\n");
	for (int i=0; i<m_states.size(); i++) {
		State state = m_states.elementAt(i);
		out.write(STATE+" name=\""+state.getName()+"\"");
		if (0==i)
			out.write(" "+INIT);
		if (state.isFinal()) 
			out.write(" "+FINAL+">\n");
		else 
			out.write(">\n");
		Vector<Vector<ItemSet>> transitions = state.getTransitions();
		for (int j=0;j<transitions.size(); j++) {
			out.write(TRANS+" "+EL);
			Vector<ItemSet> v = transitions.elementAt(j);
			for (int k=0; k<v.size(); k++) {
				PDATransition trans = (PDATransition) v.elementAt(k);
				if (null!=trans) out.write(trans.toString());
			}
			out.write(END+"\n");
		}
		out.write(END_STATE+"\n");
	}
	out.close();
	}catch (Exception ex)
	{	ex.printStackTrace();	}
}

/** @return the stack alphabet. */ 
public Vector<Element> getStackAlphabet()
{   return m_stackAlphabet;  }

/** @return the start symbol for the stack. */
public Element getStackStartSymbol()
{   return m_stackStartSymbol;  }

/**
 * @param n
 * @return the stack alphabet accepted by the automata 
 */
public Element getStackAlphabetElement(int n)
{   return m_stackAlphabet.elementAt(n);  }

/**
 * 
 * @return the size of the alphabet stack
 */
public int getSizeOfStackAlphabet()
{   return m_stackAlphabet.size();  }

/** 
 * @param state
 * @param stack
 * @return if the PDA is in an acceptance state.
 */
protected boolean isInAcceptingState(PDAState state, PDAStack stack)
{
	return ((state.isFinal()&&(1==stack.size()))
		|| (state.isFinal() && stack.isEmpty()));
}

/*
 * Verifies if a sequence is legal with respect the state with id stateId
 * A sequence s is legal with respect to state b of the automata, if s 
 * defines a path in the automata.
 * @param stateId The target state of the automata
 * @param s The target sequence
 * @return True if s is legal with respect to state stateId
 */  
/*protected boolean isLegalWithRespectTo(int stateId, EventSequence s, 
									PDAStack stack, int first, int last)
{
	int i = first;
	int stId = stateId;
    while (i<last) {
		ItemSet set = s.elementAtIndex(i).getSet();
		State state = getState(stId);
		int ind = state.legalStateAchievedBy(set, stack, (i==first));
		if (-1==ind)
			return false;
        stId = ind;
		i++;
	}    
	// Verifies the last element
	return (-1!=legalAcceptsSetAfterState(stId, stack, s.elementAtIndex(last).getSet()));
}*/

/**
 * @param stId
 * @param stack
 * @param sk
 * @return the state reached 
 */
protected int acceptsSetAfterState(int stId, PDAStack stack, ItemSet sk)
{
	Vector<Vector<PDATransition>> transStates = ((PDAState)getState(stId)).getPDATransitions();
	for (int j=0; j<transStates.size(); j++){
		Vector<PDATransition> transV = transStates.elementAt(j);
		for (int i=0; i<transV.size(); i++) {
			PDATransition trans = transV.elementAt(i);
			if (stack.isApplicable(trans, sk)) {
				stack.performOp(trans);
				return j;
			}
			else if (stack.isPartiallyApplicable(trans, sk)) {
				stack.performPartialOp(trans,sk);
				return stId;
			}
		}
	}
    return -1;
}

/**
 * 
 * @param s
 * @param fromState
 * @param stack
 * @param until
 * @return the state reached
 */
protected int achievesStateWithSequence(EventSequence s, int fromState, 
											PDAStack stack, int until)
{
	int stId = fromState;
	for (int i=0; i<=until; i++) {
		ItemSet set = s.elementAtIndex(i).getSet();
		PDAState state = (PDAState)getState(stId);
		int ind = state.stateAchievedBy(set, stack);
		if (-1==ind)
			return -1;
        stId = ind;
	}    
	return stId;
}

//________________________________________________________________
//								ACCEPTS TO JOIN
//________________________________________________________________
/**
 * @param s
 * @param el
 * @param inParallel
 * @return true, if
 */
@Override
public boolean acceptsToJoinToAlpha(EventSequence s, Element el, boolean inParallel)
{
	if (inParallel) {
		PDAStack stack = new PDAStack(m_stackAchievedByPrefix);
		ItemSet set = new ItemSet(s.elementAtIndex(s.length()-1).getSet());
		if (el.isGreaterThan(set.elementAt(set.size()-1))) {
			set.addElement(el);
			return (-1!=acceptsSetAfterState(m_stateAchievedByPrefix, 
											stack, set));
		}
		else return false;
	}
	else {
		PDAStack stack = new PDAStack(m_stackAchievedBySeq);
		return (-1!=acceptsSetAfterState(m_stateAchievedBySeq, 
										 stack,
										 new ItemSet(el)));
	}
}
//________________________________________________________________
//							LEGAL
//________________________________________________________________
/**
 * @param transStack
 * @param statesStack
 * @return true, if
 */
protected boolean reallyAcceptsAsLegalWRT(Stack<PDATransition> transStack, Stack<Integer> statesStack)
{
	int npops = 0;
	int npushs = 0;
	for (int i=0; i<transStack.size(); i++){
		switch ((transStack.elementAt(i)).getOp()) {
			case PDAStack.PUSH: npushs++; break;
			case PDAStack.POP: npops++; break;
		}
	}
	if (npops<=npushs) 
		return true;
	else {
		PDAStack copy = new PDAStack();
		int indStack = -1;
		boolean found = false;
		for (int i=0;i<transStack.size(); i++){
			PDATransition trans = transStack.elementAt(i);
			Vector<Element> old = trans.getOldStackElems();
			PDAState state = (PDAState)getState((statesStack.elementAt(i)).intValue());
			found = !copy.isEmpty() && old.equals(copy.top()); 
			int j=0;
			while (!found && j<state.m_from.size()){
				Vector<PDATransition> v = state.m_from.elementAt(j++);
				for (int k=0; k<v.size(); k++){
					PDATransition t = v.elementAt(k);
					if (t.getOp()==PDAStack.PUSH && old.equals(t.getNewStackElems())){
						copy.m_elements.add(indStack+1, t.getNewStackElems());
						found = true;
						break;
					}else if (t.getOp()==PDAStack.NO_OP && old.equals(t.getOldStackElems())){
						copy.m_elements.add(indStack+1, t.getOldStackElems());
						found = true;
						break;
					}		
				}
			}
		}
		return found;
	}
}

/**
 * 
 * @param stId
 * @param stack
 * @param sk
 * @param s
 * @param last
 * @return true, if
 */
protected int legalAcceptsSetAfterState(int stId, PDAStack stack, ItemSet sk, EventSequence s, int last)
{
	Vector<Vector<PDATransition>> transStates = ((PDAState)getState(stId)).getPDATransitions();
	for (int j=0; j<transStates.size(); j++){
		Vector<PDATransition> transV = transStates.elementAt(j);
		for (int k=0; k<transV.size(); k++) {
			PDATransition trans = transV.elementAt(k);
			if (stack.isLegalApplicable(trans, sk, false, m_transStack)) {
			    @SuppressWarnings("unchecked")
				Stack<PDATransition> copy = (Stack<PDATransition>)m_transStack.clone();
				copy.push(trans);
			    @SuppressWarnings("unchecked")
				Stack<Integer> statesCp = (Stack<Integer>)m_statesStack.clone();
				statesCp.push(new Integer(j));
				if (reallyAcceptsAsLegalWRT(copy, statesCp)){
					stack.performOp(trans);
					return j;
				}
			}
			else if (stack.isPartiallyLegalApplicable(trans, sk, m_transStack)) {
			    @SuppressWarnings("unchecked")
				Stack<PDATransition> copy = (Stack<PDATransition>)m_transStack.clone();
				copy.push(trans);
			    @SuppressWarnings("unchecked")
				Stack<Integer> statesCp = (Stack<Integer>)m_statesStack.clone();
				statesCp.push(new Integer(stId));
				if (reallyAcceptsAsLegalWRT(copy, statesCp)){
					stack.performPartialOp(trans,sk);
					return stId;
				}
			}
		}
	}
    return -1;
}

/**
 * @param s
 * @param el
 * @param inParallel
 * @return true, if
 */
@Override
public boolean legalAcceptsToJoinToAlpha(EventSequence s, Element el, boolean inParallel)
{
	if (inParallel) {
		PDAStack stack = new PDAStack(m_stackAchievedByPrefix);
		ItemSet set = new ItemSet(s.elementAtIndex(s.length()-1).getSet());
		if (el.isGreaterThan(set.elementAt(set.size()-1))) {
			set.addElement(el);
			return (-1!=legalAcceptsSetAfterState(m_stateAchievedByPrefix, 
													stack, set, s, s.length()-2));
		}
		else return false;
	}
	else {
		PDAStack stack = new PDAStack(m_stackAchievedBySeq);
		return (-1!=legalAcceptsSetAfterState(m_stateAchievedBySeq, 
												stack, new ItemSet(el), s, s.length()-1));
	}
}
//________________________________________________________________
/**
 * Procedure to simulate the path achieved by a subset of events of the pattern s.
 * The pattern has to be legal to some automata state.
 * @param s The pattern to simulate
 */
@Override
public void simulateSubSequence(EventSequence s)
{	
	int nrOfStates = m_states.size();
	PDAStack stack = new PDAStack();
	m_transStack = new Stack<PDATransition>();
	m_statesStack = new Stack<Integer>();
	if (1==s.length()){
		ItemSet set = s.elementAtIndex(0).getSet();
		for (int j=0; j<nrOfStates; j++){
			int newState = ((PDAState)getState(j)).legalStateAchievedBy(set, stack, true, m_transStack);
			if (-1!=newState){
				m_statesStack.push(new Integer(newState));
				m_stateAchievedByPrefix = newState;
				m_stateAchievedBySeq = newState;
				m_stackAchievedByPrefix = new PDAStack(stack);
				m_stackAchievedBySeq = new PDAStack(stack);
				break;
			}
		}
	}
	else {	
		int current = 0;
		boolean first = true;
		// Try each state to simulate the prefix
		for (int j=0; j<nrOfStates; j++){
			stack = new PDAStack();
			current = j;
			int ind = 0;
			first = true;
		    while (ind<s.length()-1) {
				ItemSet set = s.elementAtIndex(ind).getSet();
				PDAState state = (PDAState)getState(current);
				int newState = state.legalStateAchievedBy(set, stack, first, m_transStack);
				if (-1==newState) break;
				m_statesStack.push(new Integer(newState));
				current = newState;
				ind++;
				first = false;
			}
			if (s.length()-1==ind) break;
		}
		//Prefix was achieved, looks for the last element
		m_stateAchievedByPrefix = current;
		m_stackAchievedByPrefix = new PDAStack(stack);
		ItemSet set = s.elementAtIndex(s.length()-1).getSet();
		m_stateAchievedBySeq = ((PDAState)getState(m_stateAchievedByPrefix)).legalStateAchievedBy(set, stack, first, m_transStack);
		m_stackAchievedBySeq = stack;	
		m_statesStack.push(new Integer(m_stateAchievedBySeq));
	}
}
//________________________________________________________________
//							SIMULATE
//________________________________________________________________
@Override
public void resetSimulation()
{	
	m_stateAchievedByPrefix = 0;	
	m_stateAchievedBySeq = 0;
}
//________________________________________________________________
/**
 * Procedure to simulate the path achieved by the first events of the pattern s.
 * It does not allow any error in the sequence (the pattern has to exactly 
 * follows the automata.
 * @param alpha The pattern to simulate
 */
@Override
public void simulateSequence(EventSequence alpha) throws DFASimulationException
{
	m_stackAchievedByPrefix = new PDAStack(m_stackStartSymbol);
	m_stateAchievedByPrefix=achievesStateWithSequence(alpha,
													m_startStateId, 
													m_stackAchievedByPrefix, 
													alpha.length()-2);
    if (-1==m_stateAchievedByPrefix) throw new DFASimulationException("PDA: it didn't get a legal state!\n");
	m_stackAchievedBySeq = new PDAStack(m_stackAchievedByPrefix);
	m_stateAchievedBySeq=acceptsSetAfterState(m_stateAchievedByPrefix, 
											  m_stackAchievedBySeq,
							                  alpha.elementAtIndex(alpha.length()-1).getSet());
}

/**
 * @param inParallel
 */
@Override
public boolean canExtend(boolean inParallel)
{	
	if (inParallel)
		return (-1 != m_stateAchievedByPrefix);
	else
		return (-1 != m_stateAchievedBySeq);	
}
//________________________________________________________________
//						ACCEPTS
//________________________________________________________________
@Override
public boolean accepts(EventSequence s)
{
	PDAStack stack = new PDAStack(m_stackStartSymbol);
	int stId = achievesStateWithSequence(s,m_startStateId,stack,s.length()-1);
	if (-1!=stId)
		return isInAcceptingState((PDAState)getState(stId), stack);
	return false;
}
//________________________________________________________________
@Override
public boolean acceptsAsPrefix(EventSequence s)
{
	PDAStack stack = new PDAStack(m_stackStartSymbol);
	int stId = achievesStateWithSequence(s, m_startStateId, 
										 stack, s.length()-2);
	if (-1!=stId)
		return (-1!=acceptsSetAfterState(stId, stack,
									s.elementAtIndex(s.length()-1).getSet()));
	return false;
}
//________________________________________________________________
/**
 * Returns true if the sequence is legal with respect to any state
 */
/*public boolean acceptsAsLegal(EventSequence s)
{
	if (acceptsAsPrefix(s))
			return true;	
	for (int stId=0; stId<m_states.size(); stId++){
		PDAStack stack = new PDAStack();
		if (isLegalWithRespectTo(stId, s, stack, 0,  s.length()-1))
			return true;
	}
	return false;
}*/
//________________________________________________________________
//							APROX PREFIX
//________________________________________________________________
@Override
public boolean aproxAcceptsPrefix(EventSequence s, Element el, 
									boolean inParallel, int error)
{	
	return (-1!=aproxAccPrefix(s, 0, el, inParallel, s.length(), m_startStateId, 
								new PDAStack(m_stackStartSymbol), 0, error));
}
//________________________________________________________________
private int verifyElCanBeAppAtLast(EventSequence s, Element el, int to, int fromSt, 
										PDAStack stack, int acc_er, int max_er)
{
	ItemSet si = new ItemSet(s.elementAtIndex(to-1).getSet());
	if (el.isGreaterThan(si.elementAt(si.size()-1))) {
		si.addElement(el);
		Vector<Vector<PDATransition>> transStates = ((PDAState)getState(fromSt)).getPDATransitions();
		for (int j=0; j<transStates.size(); j++){
			Vector<PDATransition> transV = transStates.elementAt(j);
			for (int i=0; i<transV.size(); i++) {
				PDATransition trans = transV.elementAt(i);
				if (stack.isApplicable(trans)){
					ItemSet set = trans;
					// Dif corresponds to the best option to adopt (a conjunction of
					// edit operations or just deleting the entire set;
					int common = set.nrOfCommon(si);
					int dif = Math.max(si.size(), set.size())-common;
					if (dif+acc_er<=max_er)
						return j;
				}
			}
		}
		if (acc_er+si.size()<=max_er)
			return 0;
		else
			return -1;
	}
	return -1;
}
// Verifies if the new element can be accepted at the end
private int verifyElCanBeAppAtEnd(EventSequence s, Element el, int fromSt, 
									PDAStack stack, int acc_er, int max_er)
{
	ItemSet si = new ItemSet(el);
	Vector<Vector<PDATransition>> transStates = ((PDAState)getState(fromSt)).getPDATransitions();
	for (int j=0; j<transStates.size(); j++){
		Vector<PDATransition> transV = transStates.elementAt(j);
		for (int i=0; i<transV.size(); i++) {
			PDATransition trans = transV.elementAt(i);
			if (stack.isApplicable(trans)){
				ItemSet set = trans;
				// At most there is an error - the deletion of EL
				if (set.equals(si) || acc_er+1<=max_er) 
					return j;
			}
		}
	}
	if (acc_er+1<=max_er)
		return 0;
	else
		return -1;
}

private int aproxAccPrefix(EventSequence s, int init, Element el, boolean inParallel, 
					int to, int fromSt, PDAStack stack, int acc_er, int max_er)
{
	// It exceeds the maximal acceptable error
	if (acc_er>max_er) return -1;
	// Verifies if EL can be appended to the last element
	if (inParallel && init==to-1)
		return verifyElCanBeAppAtLast(s, el, to, fromSt, stack, acc_er, max_er);
	// Verifies if the new element can be accepted at the end
	else if (!inParallel && init==to) 
		return verifyElCanBeAppAtEnd(s, el, fromSt, stack, acc_er, max_er);
	// Procede with the next basket
	else{
		ItemSet si = s.elementAtIndex(init).getSet();
		int next=-1;
		PDAState current = (PDAState)getState(fromSt);
		// Looks for a perfect match
		for (int i=0; i<m_states.size(); i++){
			PDATransition trans = current.getApplicableTo(i, stack, si);
			// It founds a perfect transition procede recursively
			if (null!=trans){
				PDAStack stack2 = new PDAStack(stack);
				stack2.performOp(trans);
				next = aproxAccPrefix(s, init+1, el, inParallel, to, 
									i, stack2, acc_er, max_er);
				if (-1!=next) return next;
			}
		}
		// If it not found a perfect match try to apply an edit operation
		Vector<Vector<PDATransition>> transStates = ((PDAState)getState(fromSt)).getPDATransitions();
		int size = si.size();
		for (int j=0; j<transStates.size(); j++){
			Vector<PDATransition> transV = transStates.elementAt(j);
			for (int i=0; i<transV.size(); i++) {
				PDATransition trans = transV.elementAt(i);
				boolean isApp = stack.isApplicable(trans);
				ItemSet set = trans;
				if (0!=size) {
					int common = set.nrOfCommon(si);
					int dif = Math.max(size,set.size())-common;
					// It is possible to procede considering si, by combining edit operations
					// or simply by using replacement
					if (isApp && acc_er+dif<=max_er) {
						PDAStack stack2 = new PDAStack(stack);
						stack2.performOp(trans);
						next = aproxAccPrefix(s, init+1, el, inParallel, to, 
											j, stack2, acc_er+dif, max_er);
					}
					if (-1!=next)
						return next;
					// IF TRANSITION SI FAILS, TRY DELETE
					next = aproxAccPrefix(s, init+1, el, inParallel, to, 
										fromSt, stack, acc_er+size, max_er);
					if (-1!=next)
						return next;
				}
				// IF DELETION FAILS, TRY INSERTION
				if (isApp) {
					PDAStack stack2 = new PDAStack(stack);
					stack2.performOp(trans);
					next = aproxAccPrefix(s, init, el, inParallel, to, 
										j, stack2, acc_er+set.size(), max_er);
					if (-1!=next)
						return next;
				}
			}
		}
		return -1;
	}
}
//________________________________________________________________
//							APROX ACCEPTS
//________________________________________________________________
@Override
public boolean aproxAccepts(EventSequence s, int error)
{
	return (-1!=aproxAcc(s, 0, s.length(), m_startStateId, 
						new PDAStack(m_stackStartSymbol), 0, error));
}

private int aproxAcc(EventSequence s, int init, int to, int fromSt, 
										PDAStack stack, int acc_er, int max_er)
{
	// It exceeds the maximal acceptable error
	if (acc_er>max_er) 
		return -1;
	// Procede with the next basket
	if (init<to){
		ItemSet si = s.elementAtIndex(init).getSet();
		return auxAproxTrans(s, si, init, to, fromSt, stack, acc_er, max_er);
	}
	// It is needed to test if a final state was achieved 
	// and perform edit operations, otherwise
	else if (isInAcceptingState((PDAState)getState(fromSt), stack))
		return fromSt;
	// Simulate the rest of the sequence
	else 
		return auxAproxTrans(s, new ItemSet(), init, to, fromSt, stack, acc_er, max_er);
}

private int auxAproxTrans(EventSequence s, ItemSet si, int init, int to, 
							int fromSt, PDAStack stack, int acc_er, int max_er)
{
	int next=-1;
	PDAState current = (PDAState)getState(fromSt);
	// Looks for a perfect match
	for (int i=0; i<m_states.size(); i++){
		PDATransition trans = current.getApplicableTo(i, stack, si);
		// It founds a perfect transition procede recursively
		if (stack.isApplicable(trans)){
			PDAStack stack2 = new PDAStack(stack);
			stack2.performOp(trans);
			next = aproxAcc(s, init+1, to, i, stack2, acc_er, max_er);
		}
	}
	// If it not found a perfect match try to apply an edit operation
	Vector<Vector<PDATransition>> transStates = ((PDAState)getState(fromSt)).getPDATransitions();
	int size = si.size();
	for (int j=0; j<transStates.size(); j++){
		Vector<PDATransition> transV = transStates.elementAt(j);
		for (int i=0; i<transV.size(); i++) {
			PDATransition trans = transV.elementAt(i);
			boolean isApp = stack.isApplicable(trans);
			ItemSet set = trans;
			if (0!=size) {
				int common = set.nrOfCommon(si);
				int dif = Math.max(size,set.size())-common;
				// It is possible to procede considering si, by combining edit operations
				// or simply by using replacement
				if (isApp && acc_er+dif<=max_er) {
					PDAStack stack2 = new PDAStack(stack);
					stack2.performOp(trans);
					next = aproxAcc(s, init+1, to, j, stack2, acc_er+dif, max_er);
				}
				if (-1!=next)
					return next;
				// IF TRANSITION SI FAILS, TRY DELETE
				next = aproxAcc(s, init+1, to, fromSt, stack, acc_er+size, max_er);
				if (-1!=next)
					return next;
			}
			// IF DELETION FAILS, TRY INSERTION
			// Insertion
			if (isApp) {
				PDAStack stack2 = new PDAStack(stack);
				stack2.performOp(trans);
				next = aproxAcc(s, init, to, j, stack2, acc_er+set.size(), max_er);
				if (-1!=next)
					return next;
			}
		}
	}
	return -1;
}

@Override
public String toString()
{	return "PDA";	}

}
