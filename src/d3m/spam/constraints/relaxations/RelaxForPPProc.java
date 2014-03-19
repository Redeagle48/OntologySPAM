/*------------------------------------------------------------------
--               PACKAGE - SEQUENTIAL PATTERN MINING              --
--                           CONSTRAINT                           --
--                                                                --
--                       Claudia M. Antunes                       --
--                         November 2003                          --
--------------------------------------------------------------------
--						  Relaxation.java                         --
--------------------------------------------------------------------*/

package d3m.spam.constraints.relaxations;

import d3m.spam.core.*;
import d3m.spam.constraints.*;
import d3m.spam.constraints.contentConstraints.*;
import ontologies.timeOntology.*;
import java.util.*;
/**
 * Class <CODE>RelaxForPPProc</CODE>
 * @author Claudia Antunes
 */
public class RelaxForPPProc extends Relaxation
{

/** Creates a new instance of Relaxation */
public RelaxForPPProc() 
{	
	super();	
	m_name = "RelaxForPPProc";
}

/**
 * 
 * @param tdm
 * @param timeError
 * @param gap
 */
public RelaxForPPProc(TDMConstraint tdm, TimeQuantity timeError, int gap) 
{	
	super(tdm, timeError, gap);	
	m_name = "RelaxForPPProc";
}

/** 
 * Verifies the support and creates the infrastructure to generate initial 
 * projectedDBs for each element in the database. 
 * @param s The db sequence to verify.
 * @param alphabet The set of elements in the db.
 * @param sup An array to store elements support.
 * @param seqDB An array of vectores with the ids of sequences where each element occur.
 * @param posDB An array of vectores with the positions where each element occur.
 * @param index The first index to look for.
 * @return True if the sequence supports some valid elements.
 */
public boolean verifyGeneralAcceptance(EventSequence s, Vector<Element> alphabet,
		long[] sup, Vector<Integer>[] seqDB, Vector<Integer>[] posDB, int index)
{
	boolean seqSupports = false;
	BitSet bs = new BitSet(alphabet.size());
	bs.clear();
	int from = 0;
	while (from<s.length()){
		seqSupports = true;
		verifyContentGeneralAcceptance(s.elementAtIndex(from), alphabet, bs,
											sup, seqDB, posDB, index, from);
		from++;
	} 
	return seqSupports;
}

/**
 * 
 * @param ev
 * @param alphabet
 * @param bs
 * @param sup
 * @param seqDB
 * @param posDB
 * @param sInd
 * @param eInd
 */
protected void verifyContentGeneralAcceptance(Event ev, Vector<Element> alphabet, 
		BitSet bs, long[] sup, Vector<Integer>[] seqDB, Vector<Integer>[] posDB, 
		int sInd, int eInd)
{
	ItemSet set = ev.getSet();
	for (int i=0; i<set.size(); i++) {
		Element el = set.elementAt(i);
		int found = alphabet.indexOf(el);
		if (-1!=found)	{
			// If current sequence did not contribute to the support of 
			// this element yet, increment its support
			if (!bs.get(found)){
				sup[found]++;
				bs.set(found);
			}
			// In any case, add another object to projecyed db
			seqDB[found].addElement(new Integer(sInd));
			posDB[found].addElement(new Integer(eInd));
		}
	}
}

/** 
 * Verifies the support and creates the infrastructure to generate initial 
 * projectedDBs for each element which respects the temporal constraint. 
 * @param s The db sequence to verify.
 * @param alphabet The set of elements in the db.
 * @param sup An array to store elements support.
 * @param projDBs
 * @param sPos The sequence id.
 * @param eInd The event position in the sequence.
 * @param inParallel A flag to state if we are verifying at the same instant or in the next ones
 */
@Override
public void verifyAcceptance(EventSequence alpha, EventSequence s, 
		Vector<Element> alphabet, int[] sup, ProjectedDB[] projDBs, 
		int indProj, int sPos, int eInd, boolean inParallel, 
		BitSet visited, BitSet accepted)
{
	// While gap is verified
	int from = eInd+1;
	while (from<s.length()&& (from-eInd<=m_maxDist)) {
		verifyContentAcceptance(alpha, s.elementAtIndex(from).getSet(), 
					alphabet, sup, projDBs, indProj, sPos, from, inParallel, visited, accepted);
		from++;
	}
}

@Override
protected void verifyContentAcceptance(EventSequence alpha, ItemSet set, Vector<Element> alphabet, 
		int[] sup, ProjectedDB[] projDBs, int indProj, int sInd, int eInd, 
		boolean inParallel, BitSet visited, BitSet accepted)
{
	for (int i=0; i<set.size(); i++) {
		Element el = set.elementAt(i);
		int found = alphabet.indexOf(el);
		if ((-1!=found)) {
			if (!visited.get(found)){
				visited.set(found);
				accepted.set(found, isAcceptedByRelaxation(alpha, el, inParallel));
			}
			if (accepted.get(found)){
				if (!projDBs[found].contains(sInd))
					sup[found]++;
				projDBs[found].set(sInd, eInd);
			}
		}
	}
}

/**
 * Returns true if the el can be appended to s, in accordance to this relaxation.
 * ******** It is needed to redefine this method, for some subclasses. *******
 * @param s The sequence already accepted.
 * @param el The element to append
 * @param inParallel A flag to state if we are verifying at the same instant or in the next ones
 */
@Override
protected boolean isAcceptedByRelaxation(EventSequence s, Element el, boolean inParallel)
{
	ItemSet sk = s.elementAtIndex(s.length()-1).getSet();
	Element last = sk.elementAt(sk.size()-1);
	return (!inParallel||el.isGreaterThan(last));
}

/**
 * Verifies if a sequence may be accepted by the constraint.
 * @param s The sequence to verify.
 */
@Override
public boolean accepts(EventSequence s)
{	return true;	}

/**
 * 
 * @param s
 * @return ???
 */
public boolean postAccepts(EventSequence s)
{
	return m_tdm.m_contentC.m_dfa.accepts(s);
}

/**
 * 
 * @param set
 * @param alphabet
 * @param tax
 * @return a vector with elements???
 */
protected Vector<Element> getParents(ItemSet set, Vector<Element> alphabet, Taxonomy tax)
{
	Vector<Element> v = new Vector<Element>(0);
	for (int i=0; i<set.size(); i++) {
		Element el = set.elementAt(i);
		int found = m_tdm.m_contentC.m_taxonomy.getParentOf(el, alphabet);
		if (-1!=found) {
			Element p = alphabet.elementAt(found);
			if (-1==v.indexOf(p))
				v.addElement(p);
		}	
	}
	return v;
}

/**
 * 
 * @param db
 * @return a db transformed in a database of events ???
 */
public Database preProcessing(Database db)
{
	Vector<Element> alphabet = m_tdm.m_contentC.m_taxonomy.getAlphabet(m_tdm.m_contentC.m_level);
	Database preprocDB = new Database();
	for (int i=0; i<db.getSize(); i++){
		EventSequence s = db.elementAt(i);
		Vector<Event> events = new Vector<Event>(0);
		for (int k=0; k<s.length(); k++) {
			Event e = s.elementAtIndex(k);
			if (m_tdm.m_temporalC.accepts(e)) {
				Vector<Element> v = getParents(e.getSet(),alphabet, m_tdm.m_contentC.m_taxonomy);
				if (null!=v)
					events.addElement(new Event(new ItemSet(v), e.getInstant()));
			}
		}
		if (0!=events.size()) 
			preprocDB.addElement(new EventSequence(events));
	}
	preprocDB.setAlphabet(alphabet);
	return preprocDB;
}

/**
 * 
 * @param patterns
 * @return ????
 */
public Vector<EventSequence> postProcessing(Vector<EventSequence> patterns)
{
	Vector<EventSequence> vec = new Vector<EventSequence>();
	for (int i=0; i<patterns.size(); i++){
		EventSequence s = patterns.elementAt(i);
		if (postAccepts(s))
			vec.addElement(s);
	}
	return vec;
}

@Override
public String toString()
{
	return m_name; //+"->"+m_tdm.m_contentC.m_dfa.toString();
}

}
