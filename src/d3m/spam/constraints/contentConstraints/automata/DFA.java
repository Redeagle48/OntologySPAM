/*------------------------------------------------------------------
--                  PACKAGE - SEQUENTIAL PATTERN MINING           --
--                            MACHINES                            --
--                                                                --
--                       Claudia M. Antunes                       --
--	                      September 2003                          --
--------------------------------------------------------------------
--                            DFA.java                            --
--------------------------------------------------------------------*/

package d3m.spam.constraints.contentConstraints.automata;
import d3m.spam.core.*;
import java.util.*;
import java.io.*;
//--------------------------------------------------------------------
//                         CLASS DFA
//--------------------------------------------------------------------
/** Class <CODE>DFA</CODE> implements a Deterministic Finite Automata, 
 * whose transitions are performed by itemsets.
 * @version 1.0
 * @author Cláudia M. Antunes
 */
public class DFA extends Object
{
	/** */
	protected static String STATE = "<state";
	/** */
	protected static String END_STATE = "</state>";
	/** */
	protected static String TRANS = "\t<trans";
	/** */
	protected static String FINAL = "final";
	/** */
	protected static String INIT = "init";
	/** */
	protected static String EL = "el=\"";
	/** */
	protected static String END = "\"/>";
    /** The set of states */
    protected Vector<State> m_states;
    /** The alphabet accepted by the automata */
    protected Vector<Element> m_alphabet;
    /** The initial state id */
    protected int m_startStateId;
	/** The state id achieved by simulating one sequence 
	 and its prefix without one itemset. */
	protected int m_stateAchievedByPrefix;

/** Creates a new empty instance of DFA */
public DFA ()
{
	m_states = new Vector<State>();
	m_alphabet = new Vector<Element>();
}

/** Creates a new instance of DFA with the specified parameters
 * @param states The set of states
 * @param alphabet The alphabet accepted by the automata
 * @param startStateId The initial state id
 */
public DFA (Vector<State> states, Vector<Element> alphabet, int startStateId)
{
	m_states = states;
	m_alphabet = alphabet;
	m_startStateId = startStateId;
}

/**
 * 
 * @param filename
 * @param alphabet
 */
public DFA (String filename, Vector<Element> alphabet)
{
	
	m_states = new Vector<State>();
	m_alphabet = new Vector<Element>();
	try {
		FileReader in = new FileReader(filename);
        BufferedReader dataIn = new BufferedReader(in);
        String line;
		String name = "";
		boolean finalState = false; int id=-1;
		Vector<Vector<ItemSet>> trans = new Vector<Vector<ItemSet>>(0);
        while (null != (line=dataIn.readLine())) {
			if (line.startsWith(TRANS))	{
				int first = line.indexOf(EL);
				int last = line.indexOf(END);
				Vector<ItemSet> sets = ItemSet.createItemSets(line.substring(first+4, last), alphabet);
				for (int j=0; j<sets.size(); j++){
					ItemSet set = sets.elementAt(j);
					for (int i=0; i<set.size(); i++){
						Element el = set.elementAt(i);
						if (null==Element.getElement(m_alphabet, el.getName()))
							m_alphabet.addElement(el);
					}
				}
				trans.addElement(sets);
			}
			else if (line.startsWith(STATE)){
				id++;
				int first = line.indexOf("name=\"");
				int last = line.indexOf("\"", first+6);
				name = line.substring(first+6, last);
				if (-1!=line.indexOf(INIT)) m_startStateId = id;
				finalState = (-1!=line.indexOf(FINAL));
				trans = new Vector<Vector<ItemSet>>();
			}
			else if(line.startsWith(END_STATE))
			{	m_states.addElement(new State(id, name, finalState, trans));	}
		}
		in.close();
        dataIn.close();
	} catch (Exception ex)
	{ ex.printStackTrace();	}	
}

/**
 * 
 * @param filename
 */
public void write(String filename)
{
	try{
	FileWriter out = new FileWriter(filename, true);
	
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
				ItemSet set = v.elementAt(k);
				if (null!=set) out.write(set.toString());
			}
			out.write(END+"\n");
		}
		out.write(END_STATE+"\n");
	}
	out.close();
	}catch (Exception ex)
	{	ex.printStackTrace();	}
}

/**
 * 
 */
public void resetSimulation()
{	m_stateAchievedByPrefix = 0;	}

/**
 * @return the alphabet accepted by the automata 
 */
public Vector<Element> getAlphabet()
{   return m_alphabet;  }

/**
 * 
 * @param v
 */
public void setAlphabet(Vector<Element> v)
{   m_alphabet = v;  }

/** 
 * @return the initial state id
 */
public int getStartStateId()
{   return m_startStateId;  }

/** 
 * return the specified state
 * @param i The state id
 * @return The state with i as id
 */
public State getState(int i)
{   return m_states.elementAt(i);  }

/** 
 * @return the set of automata states
 */
public Vector<State> getAllStates()
{   return m_states;  }

/**
 * @return the set of final states
 */
public Vector<State> getAllFinalStates()
{   
	Vector<State> finalStates = new Vector<State>();
	for (int i=0; i<m_states.size(); i++) {
		State state = getState(i);
		if (state.isFinal())
			finalStates.addElement(state);
	} 
	return finalStates;
}

/**
 * @return the number of states
 */
public int getNumberOfStates()
{   return m_states.size();  }

/**
 * Verifies if a sequence is legal with respect the state with id stateId
 * A sequence s is legal with respect to state b of the automata, if s 
 * defines a path in the automata.
 * @param stateId The target state of the automata
 * @param s The target sequence
 * @param first
 * @param last
 * @return True if s is legal with respect to state stateId
 */  
protected boolean isLegalWithRespectTo(int stateId, EventSequence s, int first, int last)
{
	int i = first;
	int stId = stateId;
    while (i<=last) {
		Event ev = s.elementAtIndex(i++);
        ItemSet set = ev.getSet();
		int ind = getState(stId).stateAchievedBy(set);
		if (-1==ind)
			return false;
        stId = ind;
	}    
    return true;
}

/**
 * 
 * @param stId
 * @param sk
 * @return the index of the element on the set
 */
protected int acceptsSetAfterState(int stId, ItemSet sk)
{
	Vector<Vector<ItemSet>> sets = getState(stId).getTransitions();
	for (int j=0; j<sets.size(); j++){
		Vector<ItemSet> trans = sets.elementAt(j);
		for (int i=0; i<trans.size(); i++) {
			ItemSet set = trans.elementAt(i);
			if (null!=set && sk.isPrefixOf(set))
				return j;
		}
	}
    return -1;
}

/**
 * 
 * @param s
 * @param fromState
 * @param until
 * @return the state id reached
 */
protected int achievesStateWithSequence(EventSequence s, int fromState, int until)
{
	int stId = fromState;
	for (int i=0; i<=until; i++) {
		ItemSet set = s.elementAtIndex(i).getSet();
		int ind = getState(stId).stateAchievedBy(set);
		if (-1==ind)
			return -1;
        stId = ind;
	}    
	return stId;
}

/**
 * 
 * @param inParallel
 * @return ????
 */
public boolean canExtend(boolean inParallel)
{	return (-1 != m_stateAchievedByPrefix);	}

//________________________________________________________________
//						ACCEPTS
//________________________________________________________________
/**
 * @param s
 * @return true, if s is valid in accordance with this DFA
 */
public boolean accepts(EventSequence s)
{
	int stId = achievesStateWithSequence(s, m_startStateId, s.length()-1);
	if (-1!=stId)
		return getState(stId).isFinal();
	return false;
}

/**
 * 
 * @param s
 * @return true, if s is a valid prefix
 */
public boolean acceptsAsPrefix(EventSequence s)
{
	int stId = achievesStateWithSequence(s, m_startStateId, s.length()-2);
	if (-1!=stId)
		return (-1!=acceptsSetAfterState(stId, 
									s.elementAtIndex(s.length()-1).getSet()));
	return false;
}

/**
 * @param s
 * @return true, if the sequence is legal with respect to any state
 */
public boolean acceptsAsLegal(EventSequence s)
{
	for (int stId=0; stId<m_states.size(); stId++){
		if (isLegalWithRespectTo(stId, s, 0,  s.length()-1))
			return true;
	}
	return false;
}

//________________________________________________________________
//							APROX
//________________________________________________________________


/**
 * @param s
 * @param el
 * @param inParallel
 * @param error
 * @return true, if s is a prefix approximatelly accepted 
 */
public boolean aproxAcceptsPrefix(EventSequence s, Element el, 
									boolean inParallel, int error)
{	
	return (-1!=aproxAccPrefix(s, 0, el, inParallel, s.length(), 
													m_startStateId, 0, error));
}

private int aproxAccPrefix(EventSequence s, int init, Element el, boolean inParallel, 
									int to, int fromSt, int acc_er, int max_er)
{
	// It exceeds the maximal acceptable error
	if (acc_er>max_er) return -1;
	// Verifies if EL can be appended to the last element
	if (inParallel && init==to-1){
		ItemSet si = new ItemSet(s.elementAtIndex(to-1).getSet());
		if (el.isGreaterThan(si.elementAt(si.size()-1))) {
			si.addElement(el);
			Vector<Vector<ItemSet>> trans = getState(fromSt).getTransitions();
			for (int j=0; j<trans.size(); j++){
				Vector<ItemSet> aux = trans.elementAt(j);
				for (int i=0; i<aux.size(); i++) {
					ItemSet set = aux.elementAt(i);
					// Dif corresponds to the best option to adopt (a conjunction of
					// edit operations or just deleting the entire set;
					ItemSet prefix = set.getPrefixUntil(si);				
					int common = prefix.nrOfCommon(si);
					int dif = Math.max(si.size(), prefix.size())-common;
					if (dif+acc_er<=max_er)
						return j;
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
	else if (!inParallel && init==to) {
		ItemSet si = new ItemSet(el);
		Vector<Vector<ItemSet>> trans = getState(fromSt).getTransitions();
		for (int j=0; j<trans.size(); j++){
			Vector<ItemSet> aux = trans.elementAt(j);
			for (int i=0; i<aux.size(); i++) {
				ItemSet set = aux.elementAt(i);
				// At most there is an error - the deletion of EL
ItemSet prefix = set.getPrefixUntil(si);				
					int common = prefix.nrOfCommon(si);
					int dif = Math.max(si.size(), prefix.size())-common;
					if (dif+acc_er<=max_er)
					return j;
			}
		}
		if (acc_er+1<=max_er)
			return 0;
		else
			return -1;
	}
	// Procede with the next basket
	else{
		ItemSet si = s.elementAtIndex(init).getSet();
		// It didnot find a valid path, choose the best operation to perform and procede
		Vector<Vector<ItemSet>> trans = getState(fromSt).getTransitions();
		int size = si.size();
		int next=-1;
		for (int j=0; j<trans.size(); j++){
			Vector<ItemSet> aux = trans.elementAt(j);
			for (int i=0; i<aux.size(); i++) {
				ItemSet set = aux.elementAt(i);
				if (0!=size) {
					int common = set.nrOfCommon(si);
					int dif = Math.max(size,set.size())-common;
					// It found the transition
					if (0==dif) //(size == common) 
						next = aproxAccPrefix(s, init+1, el, inParallel, to, j, acc_er, max_er);
					// It is possible to procede considering si, by combining edit operations
					// or simply by using replacement
					else	
						next = aproxAccPrefix(s, init+1, el, inParallel, to, j, acc_er+dif, max_er);
					if (-1!=next)
						return next;
					// IF TRANSITION SI FAILS, TRY DELETE
					next = aproxAccPrefix(s, init+1, el, inParallel, to, fromSt, acc_er+size, max_er);
					if (-1!=next)
						return next;
				}
				// IF DELETION FAILS, TRY INSERTION
				// Insertion
				next = aproxAccPrefix(s, init, el, inParallel, to, j, acc_er+set.size(), max_er);
				if (-1!=next)
					return next;
			}
		}
		return -1;
	}
}

/**
 * 
 * @param s
 * @param error
 * @return true, if s is aproximatelly accepted
 */
public boolean aproxAccepts(EventSequence s, int error)
{	return (-1!=aproxAcc(s, 0, s.length(), m_startStateId, 0, error));	}

private int aproxAcc(EventSequence s, int init, int to, int fromSt, int acc_er, int max_er)
{
	// It exceeds the maximal acceptable error
	if (acc_er>max_er) return -1;
	// Procede with the next basket
	if (init<to){
		ItemSet si = s.elementAtIndex(init).getSet();
		return auxAproxTrans(s, si, init, to, fromSt, acc_er, max_er);
	}
	// It is needed to test if a final state was achieved 
	// and perform edit operations, otherwise
	else if (getState(fromSt).isFinal())
		return fromSt;
	// Simulate the rest of the sequence
	else 
		return auxAproxTrans(s, new ItemSet(), init, to, fromSt, acc_er, max_er);
}

private int auxAproxTrans(EventSequence s, ItemSet si, int init, int to, int fromSt, int acc_er, int max_er)
{
	// It didnot find a valid path, choose the best operation to perform and procede
	Vector<Vector<ItemSet>> trans = getState(fromSt).getTransitions();
	int size = si.size();
	int next=-1;
	for (int j=0; j<trans.size(); j++){
		Vector<ItemSet> aux = trans.elementAt(j);
		for (int i=0; i<aux.size(); i++) {
			ItemSet set = aux.elementAt(i);
			if (0!=size) {
				int common = set.nrOfCommon(si);
				int dif = Math.max(size,set.size())-common;
				// It found the transition
				if (0==dif) //(size == common) 
					next = aproxAcc(s, init+1, to, j, acc_er, max_er);
				// It is possible to procede considering si, by combining edit operations
				// or simply by using replacement
				else	
					next = aproxAcc(s, init+1, to, j, acc_er+dif, max_er);
				if (-1!=next)
					return next;
				// IF TRANSITION SI FAILS, TRY DELETE
				next = aproxAcc(s, init+1, to, fromSt, acc_er+size, max_er);
				if (-1!=next)
					return next;
			}
			// IF DELETION FAILS, TRY INSERTION
			// Insertion
			next = aproxAcc(s, init, to, j, acc_er+set.size(), max_er);
			if (-1!=next)
				return next;
		}
	}
	return -1;
}
//________________________________________________________________
//							ACCEPTS TO JOIN
//________________________________________________________________
/**
 * @param s
 * @param el
 * @param inParallel
 * @return true, if it is possible to join el to s
 */
public boolean acceptsToJoinToAlpha(EventSequence s, Element el, boolean inParallel)
{
	if (inParallel) {
		ItemSet set = new ItemSet(s.elementAtIndex(s.length()-1).getSet());
		if (el.isGreaterThan(set.elementAt(set.size()-1))) {
			set.addElement(el);
			return (-1!=acceptsSetAfterState(m_stateAchievedByPrefix, set));
		}
		else return false;
	}
	else {
		int ind = getState(m_stateAchievedByPrefix).stateAchievedBy(s.elementAtIndex(s.length()-1).getSet());
		if (-1!=ind)
			return (-1!=acceptsSetAfterState(ind, new ItemSet(el)));
		else
			return false;
	}
}

/**
 * 
 * @param s
 * @param el
 * @param inParallel
 * @return true, if it is possible to join el to s in a legal way
 */
public boolean legalAcceptsToJoinToAlpha(EventSequence s, Element el, boolean inParallel)
{
	boolean first = inParallel && (1==s.length());
	if (inParallel) {
		ItemSet set = new ItemSet(s.elementAtIndex(s.length()-1).getSet());
		if (el.isGreaterThan(set.elementAt(set.size()-1))) {
			set.addElement(el);
			return (-1!=legalAcceptsSetAfterState(m_stateAchievedByPrefix, set, first));
		}
		else return false;
	}
	else if (1==s.length()){
		int ind = legalAcceptsSetAfterState(m_stateAchievedByPrefix, s.elementAtIndex(0).getSet(), true);
		if (-1!=ind){
			return (-1!=legalAcceptsSetAfterState(ind, new ItemSet(el), first));
		}
		else
			return false;
	}
	else {
		int ind = getState(m_stateAchievedByPrefix).stateAchievedBy(s.elementAtIndex(s.length()-1).getSet());
		if (-1!=ind){
			return (-1!=legalAcceptsSetAfterState(ind, new ItemSet(el), first));
		}
		else
			return false;
	}
}

/**
 * 
 * @param stId
 * @param sk
 * @param first
 * @return ????
 */
protected int legalAcceptsSetAfterState(int stId, ItemSet sk, boolean first)
{
	Vector<Vector<ItemSet>> transStates = getState(stId).getTransitions();
	for (int j=0; j<transStates.size(); j++){
		Vector<ItemSet> transV = transStates.elementAt(j);
		for (int k=0; k<transV.size(); k++) {
			ItemSet transSet = transV.elementAt(k);
			if (first) {
				if (sk.isSuffixOf(transSet))
					return j;
				else if (sk.isSubsetOf(transSet))
					return stId;
			}
			else {
				if (sk.equals(transSet))
					return j;
				else if (sk.isPrefixOf(transSet))
					return stId;
			}
		}
	}
    return -1;
}
//________________________________________________________________
//							SIMULATE
//________________________________________________________________
/**
 * Procedure to simulate the path achieved by the first events of the pattern s.
 * It does not allow any error in the sequence (the pattern has to exactly 
 * follows the automata.
 * @param alpha The pattern to simulate
 * @throws DFASimulationException
 */
public void simulateSequence(EventSequence alpha) throws DFASimulationException
{
	int state = achievesStateWithSequence(alpha,
				     					  m_startStateId, 
										  alpha.length()-2);
	if (-1!=state)
		m_stateAchievedByPrefix = state;
}

/**
 * Procedure to simulate the path achieved by a subset of events of the pattern s.
 * The pattern has to be legal to some automata state.
 * @param s The pattern to simulate
 */
public void simulateSubSequence(EventSequence s)
{	
	int nrOfStates = m_states.size();
	ItemSet set = s.elementAtIndex(0).getSet();
	if (1==s.length()){
		for (int j=0; j<nrOfStates; j++){
			int newState = getState(j).legalStateAchievedByPrefix(set);
			if (-1!=newState){
				m_stateAchievedByPrefix = j;
				break;
			}
		}		
	}
	else {
		for (int j=0; j<nrOfStates; j++){
			int newState = getState(j).legalStateAchievedBy(set, true);
			if (-1!=newState){
				m_stateAchievedByPrefix = newState;
				break;
			}
		}
		int current = m_stateAchievedByPrefix;
		int ind = 1;
		while (ind<s.length()-1) {
			int newState = getState(current).stateAchievedBy(s.elementAtIndex(ind).getSet());
			if (-1==newState) break;
			current = newState;
			ind++;
		}
		m_stateAchievedByPrefix = current;	
	}
}

@Override
public String toString()
{	return "DFA";	}

/*public static void main (String[] args)
{
	try{
		EventSequencesReader reader = new EventSequencesReader("D://Adinha//ID//cap5//seqs//ist//curriculAMsUntil2001.txt", 
													new TimeUnit(TimeUnit.YEAR));
		Database db = reader.getSequences();
		DFA dfa = new DFA("D://Adinha//ID//cap5//seqs//ist//dfaAMsBefore95_6semesters.txt",
							db.getAlphabet());
		// Accepted in 4 semesters
System.out.println("Accepted in 4 semesters");
		EventSequence s = new EventSequence("<[(am1) 0],[(am2) 1],[(am3) 2],[(am4) 3]>", 
											db.getAlphabet(), new TimeUnit(TimeUnit.YEAR));
		System.out.println(s.toString()+"->"+String.valueOf(dfa.accepts(s)));
		s = new EventSequence("<[(am1) 0],[(~am2) 1],[(am3) 2],[(am2,am4) 3]>", 
											db.getAlphabet(), new TimeUnit(TimeUnit.YEAR));
		System.out.println(s.toString()+"->"+String.valueOf(dfa.accepts(s)));
		s = new EventSequence("<[(~am1) 0],[(am2) 1],[(am1,am3) 2],[(am4) 3]>", 
											db.getAlphabet(), new TimeUnit(TimeUnit.YEAR));
		System.out.println(s.toString()+"->"+String.valueOf(dfa.accepts(s)));
		s = new EventSequence("<[(~am1) 0],[(~am2) 1],[(am1,am3) 2],[(am2,am4) 3]>", 
											db.getAlphabet(), new TimeUnit(TimeUnit.YEAR));
		System.out.println(s.toString()+"->"+String.valueOf(dfa.accepts(s)));
		// Accepted in 5 semesters
System.out.println("Accepted in 5 semesters");
		s = new EventSequence("<[(am1) 0],[(am2) 1],[(~am3) 2],[(am4) 3],[(am3) 4]>", 
											db.getAlphabet(), new TimeUnit(TimeUnit.YEAR));
		System.out.println(s.toString()+"->"+String.valueOf(dfa.accepts(s)));
		s = new EventSequence("<[(am1) 0],[(~am2) 1],[(~am3) 2],[(am2,am4) 3],[(am3) 4]>", 
											db.getAlphabet(), new TimeUnit(TimeUnit.YEAR));
		System.out.println(s.toString()+"->"+String.valueOf(dfa.accepts(s)));
		s = new EventSequence("<[(~am1) 0],[(am2) 1],[(am1,~am3) 2],[(am4) 3],[(am3) 4]>", 
											db.getAlphabet(), new TimeUnit(TimeUnit.YEAR));
		System.out.println(s.toString()+"->"+String.valueOf(dfa.accepts(s)));
		s = new EventSequence("<[(~am1) 0],[(am2) 1],[(~am1,am3) 2],[(am4) 3],[(am1) 4]>", 
											db.getAlphabet(), new TimeUnit(TimeUnit.YEAR));
		System.out.println(s.toString()+"->"+String.valueOf(dfa.accepts(s)));
		s = new EventSequence("<[(~am1) 0],[(am2) 1],[(~am1,~am3) 2],[(am4) 3],[(am1,am3) 4]>", 
											db.getAlphabet(), new TimeUnit(TimeUnit.YEAR));
		System.out.println(s.toString()+"->"+String.valueOf(dfa.accepts(s)));
		s = new EventSequence("<[(~am1) 0],[(~am2) 1],[(am1,~am3) 2],[(am2,am4) 3],[(am3) 4]>", 
											db.getAlphabet(), new TimeUnit(TimeUnit.YEAR));
		System.out.println(s.toString()+"->"+String.valueOf(dfa.accepts(s)));
		s = new EventSequence("<[(~am1) 0],[(~am2) 1],[(~am1,am3) 2],[(am2,am4) 3],[(am1) 4]>", 
											db.getAlphabet(), new TimeUnit(TimeUnit.YEAR));
		System.out.println(s.toString()+"->"+String.valueOf(dfa.accepts(s)));
		s = new EventSequence("<[(~am1) 0],[(~am2) 1],[(~am1,~am3) 2],[(am2,am4) 3],[(am1,am3) 4]>", 
											db.getAlphabet(), new TimeUnit(TimeUnit.YEAR));
		System.out.println(s.toString()+"->"+String.valueOf(dfa.accepts(s)));
		// Accepted in 6 semesters	
System.out.println("Accepted in 6 semesters");
		s = new EventSequence("<[(am1) 0],[(am2) 1],[(~am3) 2],[(~am4) 3],[(am3) 4],[(am4) 5]>", 
											db.getAlphabet(), new TimeUnit(TimeUnit.YEAR));
		System.out.println(s.toString()+"->"+String.valueOf(dfa.accepts(s)));
		s = new EventSequence("<[(am1) 0],[(~am2) 1],[(~am3) 2],[(am2,~am4) 3],[(am3) 4],[(am4) 5]>", 
											db.getAlphabet(), new TimeUnit(TimeUnit.YEAR));
		System.out.println(s.toString()+"->"+String.valueOf(dfa.accepts(s)));
		s = new EventSequence("<[(am1) 0],[(~am2) 1],[(~am3) 2],[(~am2,am4) 3],[(am3) 4],[(am2) 5]>", 
											db.getAlphabet(), new TimeUnit(TimeUnit.YEAR));
		System.out.println(s.toString()+"->"+String.valueOf(dfa.accepts(s)));
		s = new EventSequence("<[(am1) 0],[(~am2) 1],[(~am3) 2],[(~am2,~am4) 3],[(am3) 4],[(am2,am4) 5]>", 
											db.getAlphabet(), new TimeUnit(TimeUnit.YEAR));
		System.out.println(s.toString()+"->"+String.valueOf(dfa.accepts(s)));
		s = new EventSequence("<[(~am1) 0],[(am2) 1],[(am1,~am3) 2],[(~am4) 3],[(am3) 4],[(am4) 5]>", 
											db.getAlphabet(), new TimeUnit(TimeUnit.YEAR));
		System.out.println(s.toString()+"->"+String.valueOf(dfa.accepts(s)));
		s = new EventSequence("<[(~am1) 0],[(am2) 1],[(~am1,~am3) 2],[(~am4) 3],[(am1,am3) 4],[(am4) 5]>", 
											db.getAlphabet(), new TimeUnit(TimeUnit.YEAR));
		System.out.println(s.toString()+"->"+String.valueOf(dfa.accepts(s)));
		s = new EventSequence("<[(~am1) 0],[(~am2) 1],[(am1,~am3) 2],[(am2,~am4) 3],[(am3) 4],[(am4) 5]>", 
											db.getAlphabet(), new TimeUnit(TimeUnit.YEAR));
		System.out.println(s.toString()+"->"+String.valueOf(dfa.accepts(s)));
		s = new EventSequence("<[(~am1) 0],[(~am2) 1],[(am1,~am3) 2],[(~am2,am4) 3],[(am3) 4],[(am2) 5]>", 
											db.getAlphabet(), new TimeUnit(TimeUnit.YEAR));
		System.out.println(s.toString()+"->"+String.valueOf(dfa.accepts(s)));
		s = new EventSequence("<[(~am1) 0],[(~am2) 1],[(am1,~am3) 2],[(~am2,~am4) 3],[(am3) 4],[(am2,am4) 5]>", 
											db.getAlphabet(), new TimeUnit(TimeUnit.YEAR));
		System.out.println(s.toString()+"->"+String.valueOf(dfa.accepts(s)));
		s = new EventSequence("<[(~am1) 0],[(~am2) 1],[(~am1,am3) 2],[(am2,~am4) 3],[(am1) 4],[(am4) 5]>", 
											db.getAlphabet(), new TimeUnit(TimeUnit.YEAR));
		System.out.println(s.toString()+"->"+String.valueOf(dfa.accepts(s)));
		s = new EventSequence("<[(~am1) 0],[(~am2) 1],[(~am1,am3) 2],[(~am2,am4) 3],[(am1) 4],[(am2) 5]>", 
											db.getAlphabet(), new TimeUnit(TimeUnit.YEAR));
		System.out.println(s.toString()+"->"+String.valueOf(dfa.accepts(s)));
		s = new EventSequence("<[(~am1) 0],[(~am2) 1],[(~am1,am3) 2],[(~am2,~am4) 3],[(am1) 4],[(am2,am4) 5]>", 
											db.getAlphabet(), new TimeUnit(TimeUnit.YEAR));
		System.out.println(s.toString()+"->"+String.valueOf(dfa.accepts(s)));
		s = new EventSequence("<[(~am1) 0],[(~am2) 1],[(~am1,~am3) 2],[(am2,~am4) 3],[(am1,am3) 4],[(am4) 5]>", 
											db.getAlphabet(), new TimeUnit(TimeUnit.YEAR));
		System.out.println(s.toString()+"->"+String.valueOf(dfa.accepts(s)));
		s = new EventSequence("<[(~am1) 0],[(~am2) 1],[(~am1,~am3) 2],[(~am2,am4) 3],[(am1,am3) 4],[(am2) 5]>", 
											db.getAlphabet(), new TimeUnit(TimeUnit.YEAR));
		System.out.println(s.toString()+"->"+String.valueOf(dfa.accepts(s)));
		s = new EventSequence("<[(~am1) 0],[(~am2) 1],[(~am1,~am3) 2],[(~am2,~am4) 3],[(am1,am3) 4],[(am2,am4) 5]>", 
											db.getAlphabet(), new TimeUnit(TimeUnit.YEAR));
		System.out.println(s.toString()+"->"+String.valueOf(dfa.accepts(s)));

	}catch (Exception ex)
	{	ex.printStackTrace();	}
}*/

}
