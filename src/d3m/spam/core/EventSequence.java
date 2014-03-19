/*------------------------------------------------------------------
--               PACKAGE - SEQUENTIAL PATTERN MINING              --
--                               CORE                             --
--                                                                --
--                       Claudia M. Antunes                       --
--                           March 2003                           --
--------------------------------------------------------------------
--                       EventSequence.java                       --
--------------------------------------------------------------------*/
package d3m.spam.core;

import ontologies.timeOntology.*;
import d3m.spam.constraints.temporalConstraints.*;
import java.util.*;

//--------------------------------------------------------------------
//		         CLASS EventSequence
//--------------------------------------------------------------------
/**
 * Class for handling an ordered sequence of events.
 * @author Claudia M Antunes
 * @version $Revision: 0.1 $
 */
public class EventSequence
{
    /** The sequence elements. */
    protected Event m_events[];
    /** The number of events. */
    protected int m_size;
    /** Sequence id. */
    protected int m_id;
    /** Sequence support in a given database. */
    protected long m_support;
    /**     */
	protected long m_prefixSup;
    /**     */
    protected double m_conf;
    /**     */
	protected double m_lift;
    /**     */
    static protected int m_nrOfSequences = 0;
//________________________________________________________________
//                  CONSTRUCTORS
//________________________________________________________________
/**
 * EventSequence:  -> EventSequence
 * Creates a new empty instance of EventSequence.
 */
public EventSequence()
{
    m_size = 0;
    m_id = -1;
    m_support = 0;
    m_conf = 0.0;
	m_prefixSup = 1;
}
//________________________________________________________________
/**
 * EventSequence: int -> EventSequence
 * Creates a new instance of EventSequence with n null elements.
 * @param n The number of elements to be created.
 */
public EventSequence(int n)
{
    m_events = new Event[n];
    m_size = n;
    m_id = -1;
    m_support = 0;
    m_conf = 0.0;
	m_prefixSup = 1;
}
//________________________________________________________________
/**
 * EventSequence: Event -> EventSequence
 * Creates a new instance of EventSequence, with one element.
 * @param ev The event that composes the EventSequence.
 */
public EventSequence(Event ev)
{
    m_events = new Event[1];
    m_events[0] = ev;
    m_size = 1;
    m_id = -1;
    m_support = 0;
    m_conf = 0.0;
	m_prefixSup = 1;
}
//________________________________________________________________
/**
 * EventSequence: String -> EventSequence
 * Creates a new instance of EventSequence, with several elements created from
 * the received string.
 * @param st The EventSequence of pairs (element, time) that composes the sequence.
 * @param alphabet
 * @param gran
 */
public EventSequence(String st, Vector<Element> alphabet, TimeUnit gran)
{
    char b_init ='[';
    char b_end = ']';
    char i_end = ')';
    Vector<Event> elems = new Vector<Event>();

    String st_event;
    int b_end_ind = 0;
    int b_init_ind = 0;
    // For each instant of time (indicated by [ and ]), there is a basket between ( and )
    while ( (-1!=(b_init_ind=st.indexOf(b_init, b_end_ind)))
          &&(b_end_ind<=st.length()) )
    {
        // Looks for the next event, finding a ] parentesis
        b_end_ind = st.indexOf(b_end, b_init_ind);
        st_event = st.substring(b_init_ind+1, b_end_ind);
        // Looks for the event instant
        int end_ind = st_event.indexOf(i_end, 0);
        String st_time = st_event.substring(end_ind+2, st_event.length());
        TimePoint time = new TimePoint(st_time, gran);
	
        // Creates the itemset for this instant
        ItemSet set = new ItemSet(st_event.substring(0, end_ind), alphabet);
        // Creates the event with the itemset just created
        Event ev = new Event(set, time);
        elems.addElement(ev);
    }
    initializeMembers(elems);
}
//________________________________________________________________
/**
 * EventSequence: EventSequence -> EventSequence
 * Creates a new instance of EventSequence, equal to the received EventSequence.
 * @param s The EventSequence to copy.
 */
public EventSequence(EventSequence s)
{
    m_events = s.m_events;
    m_size = s.m_size;
    m_id = s.m_id;
    m_support = s.m_support;
    m_conf = s.m_conf;
	m_prefixSup = 1;
}
//________________________________________________________________
/**
 * @param events
 */
public EventSequence(Vector<Event> events)
{
    initializeMembers(events);
}
//________________________________________________________________
/**
 * @param elements
 */
protected void initializeMembers(Vector<Event> elements)
{
    // Initialize member arrays
    m_events = new Event[elements.size()];
    m_size = elements.size();
    // Initialize events
    for (int j=0; j<elements.size(); j++)
        m_events[j]= elements.elementAt(j);
    m_id = m_nrOfSequences++;
    m_support = 0;
    m_conf = 0.0;    
	m_prefixSup = 1;
}
//________________________________________________________________
@Override
public Object clone()
{
    EventSequence copy = new EventSequence(m_size);
    for (int i=0; i<m_size; i++)
        copy.m_events[i] = (Event)m_events[i].clone();
    copy.m_size = m_size;
    copy.m_id = m_id;
    copy.m_support = m_support;
    copy.m_conf = m_conf;
    return copy;
}
//________________________________________________________________
//                  SELECTORS
//________________________________________________________________
/** getID: -> long
 * Returns the sequence ID;
 * @return the sequence unique id
 */
public int getID()
{
	return m_id;
}
/**
 * 
 * @return the support for sequence (the rule)
 */
public long getSupport()
{
    return m_support;
}
/**
 * 
 * @return the support for the maximal prefix (the antecedent)
 */
public long getPrefixSupport()
{
    return m_prefixSup;
}
/**
 * 
 * @return the confidence for the sequence
 */
public double getConfidence()
{
    return m_conf;
}
/**
 * @return the next instant after the last event in the sequence
 */
public TimePoint getNextInstant()
{
    if (m_size>0) 
        return (m_events[m_size-1]).getInstant().next();
    else 
        return null;
}
//________________________________________________________________
/**
 * elementAtIndex: int -> Event
 * Returns the element at index i, null if the index doesn't exist.
 * @param i The index.
 * @return The element at index i.
 */
public Event elementAtIndex(int i)
{
    if (i<m_size)
        return m_events[i];
    else
        return null;
}
//________________________________________________________________
/**
 * subsequence: int x int -> Sequence
 * Creates a subsequence with end-init+1 elements.
 * @param init -> the first element's index of the new sequence
 * @param end -> the last element's index of the new sequence
 * Sequence s = Sequence("abcd");
 * s.subsequence(0,2); ==> "abc"
 * @return a subsequence with end-init+1 elements.
 */
public EventSequence subsequence(int init, int end)
{
    int n = end-init+1;
    if ((0==m_size)||(n<1))
	return new EventSequence(0);
    EventSequence s = new EventSequence(n);
    for (int i=0; i<n; i++)
    {
    	Event e = m_events[i+init];
	s.m_events[i] = e;
    }
    s.m_id = m_id;
    return s;
} 
//________________________________________________________________
//                  MODIFIERS
//________________________________________________________________
/**
 * addEvent: Event -> EventSequence
 * Creates a new sequence with a new element at the end of the sequence.
 * @param ev The new element to add.
 * @return The new sequence.
 */
public EventSequence addEvent(Event ev)
{
    EventSequence s = new EventSequence(m_size+1);
  	for (int j=0; j<m_size; j++)
        s.m_events[j]= m_events[j];
    s.m_events[m_size] = ev;	
    s.m_size = m_size+1;	
    return s;
}
/**
 * Appends an element to the last event of the sequence.
 * @param el
 * @return a new sequence created from this and an element
 */
public EventSequence addElement(Element el)
{
    EventSequence s = (EventSequence)clone();
	ItemSet set = s.m_events[m_size-1].getSet();
	set.addElement(el);
	return s;
}
//________________________________________________________________
/**
 * Increments the support 
 */
public void incSupport()
{   m_support ++;	}
/**
 * Updates the support
 * @param sup
 */
public void setSupport(long sup)
{   m_support = sup;	}
/**
 * Updates the support for the prefix
 * @param sup
 */
public void setPrefixSupport(long sup)
{   m_prefixSup = sup;	}
//________________________________________________________________
/**
 * Updates the lift
 * @param n
 */
public void setLift(double n)
{   m_lift = n;	}
//________________________________________________________________
/**
 * Updates the confidence
 * @param n
 */
public void setConfidence(double n)
{   m_conf = n;	}
//________________________________________________________________
/**
 * Resets the number of sequences in the db
 */
public static void reset()
{	m_nrOfSequences = 0;	}
//________________________________________________________________
/**
 * concatenateWith: Sequence -> Sequence
 * Creates a new sequence from two others: the received and the original ones.
 * @param seq The received sequence, which will be the postfix.
 * @return A sequence composed by this|seq.
 */
public EventSequence concatenateWith (EventSequence seq)
{
    EventSequence new_seq = new EventSequence(this.m_size + seq.m_size);
    for (int i=0; i<this.m_size; i++)
        new_seq.m_events[i] = this.elementAtIndex(i);
    for (int i=0; i<seq.m_size; i++)
        new_seq.m_events[i+m_size] = seq.elementAtIndex(i);
    m_id = -1;
    return new_seq;
}
//________________________________________________________________
//                          TESTS
//________________________________________________________________
/**
 * contains: Event -> boolean
 * Verifies if an element is contained on the sequence.
 * @param el The element.
 * @return True if the element is contained in the sequence
 */
/*public short supports(ItemSet set, short n)
{
    while ((n<m_size) && (!(((Event)m_events[n]).getSet()).contains(set)))
        n++;
    if (n<m_size)
        return (short)n;
    else
        return -1;
}*/
//________________________________________________________________
/**
 * Returns the position index of the first occurrence of set in this sequence.
 * @param set the set to find
 * @param n The first position to look for.
 * @return The position index.
 */
public int indexOf(ItemSet set, int n)
{
    while ((n<m_size) && !((m_events[n].getSet()).contains(set)))
        n++;
    if (n>=m_size) 
        return -1;
    else 
        return n;
}
//________________________________________________________________
/**
 * findFrequentElements: ->
 * It discovers the different elements of a sequence and counts their
 * support, when this sequence doesn't already support a particular element.
 * @param alphabet A vector with different elements to look for.
 * @param arDB An array of BitSets to store the occurrence of each element in each sequence
 * @param first The index of the first element to look for (for parallel events)
 * @param tempC the temporal constraint that should be satisfied
 */
public void findFrequentElements(Vector<Element> alphabet, BitSet[] arDB, int first, 
                                 TemporalConstraint tempC)
{
	// Temporal constraint specify the portion to look for
    Event e = elementAtIndex(first);
	TimePoint tpLast = e.getInstant().add(tempC.getMaxDistance());
	int i=first;
	while ((i<m_size) && 
	       (tpLast.after(e.getInstant())||tpLast.equals(e.getInstant())))
	{
		e = elementAtIndex(i++);
		// If e satisfies the temporalConstraint, it may be frequent
		if (tempC.accepts(e))
        {
            ItemSet set = e.getSet();
            for (int j=0; j<set.size(); j++)
            {
                int ind = alphabet.indexOf(set.elementAt(j));
                if (-1!=ind && !arDB[ind].get(m_id))
                    arDB[ind].set(m_id);
            }
        }
	} 
}
/**
 * findFrequentElements: ->
 * It discovers the different elements of a sequence and counts their
 * support, when this sequence doesn't already support a particular element.
 * @param alphabet A vector with different elements to look for.
 * @param arDB An array of BitSets to store the occurrence of each element in each sequence
 * @param first The index of the first element to look for (for parallel events)
 * @param tempC the temporal constraint that should be satisfied
 */
public void findFrequentParallelElements(Vector<Element> alphabet, BitSet[] arDB, int first, 
                                 TemporalConstraint tempC)
{
	// If e satisfies the temporalConstraint, it may be frequent
	Event e = elementAtIndex(first);
	if (tempC.accepts(e))
    {
		int n = alphabet.size();
		ItemSet set = e.getSet();
        for (int j=0; j<set.size(); j++)
        {
			int ind = alphabet.indexOf(set.elementAt(j));
			// Last n positions store the projected database for parallel elements
            if (-1!=ind && !arDB[ind+n].get(m_id))
				arDB[ind+n].set(m_id);
        }
	} 
}

/**
 * 
 * @param db
 * @param alphabet
 * @param arSerialSup
 * @param arLastSeq
 * @param first
 * @param last
 */
public void oldFindFrequentSerialElements(Database db, Vector<Element> alphabet, 
		long[] arSerialSup, long[] arLastSeq, int first, long last)
{
    // Look for serial elements
    for (int i=first; i<=last; i++) {
        ItemSet set = elementAtIndex(i).getSet();
        for (short j=0; j<set.size(); j++) {
			Element elem = set.elementAt(j);
			int ind = alphabet.indexOf(elem);		
			// Even positions mantains the seq ids for serial events
            if (-1!=ind && m_id!=arLastSeq[2*ind]) {
                (arSerialSup[ind])++;
                arLastSeq[2*ind] = m_id;
            }
        }
    }
}

/**
 * 
 * @param db
 * @param alphabet
 * @param arParalelSup
 * @param arLastSeq
 * @param pos
 */
public void oldFindFrequentParallelElements(Database db, Vector<Element> alphabet, 
		long[] arParalelSup, long[] arLastSeq, int pos)
{
    // Look for parallel elements
    ItemSet set = elementAtIndex(pos).getSet();
    for (short j=0; j<set.size(); j++)
    {
		Element elem = set.elementAt(j);
		int ind = alphabet.indexOf(elem);		
		// Odd positions mantains the seq ids for baskets support
        if (-1!=ind && m_id!=arLastSeq[2*ind+1])
        {
            (arParalelSup[ind])++; 
            arLastSeq[2*ind+1] = m_id;
        }
    }
}
//________________________________________________________________
/**
 * countSupportForEachElement: ->
 * It discovers the different elements of a sequence and counts their
 * support
 * @param alphabet A vector with different elements to look for.
 * @param arSup A vector with the number of occurrences for each different
 *		       element
 * @param first The index of the first element of the subsequence
 * @param last The index of the last element to consider
 */
public void countSupportForEachElement (Vector<Element> alphabet, long[] arSup, int first, int last)
{
    boolean[] found = new boolean[alphabet.size()];
    // Look for serial elements
    for (int i=first; i<last; i++){
        ItemSet set = elementAtIndex(i).getSet();
        for (int j=0; j<set.size(); j++) {
            int ind = alphabet.indexOf(set.elementAt(j));
            if (-1!=ind) {
                if (!found[ind]){
                    found[ind] = true;
                    (arSup[ind])++;     
                }
            }
        }
    }
}
//________________________________________________________________
/**
 * NOVA NOVA NOVA NOVA NOVA * SPaRSe * NOVA NOVA NOVA NOVA NOVA *
 * countSupportForEachElement: ->
 * It discovers the different elements of a sequence, counts their
 * support and 
 * @param vecItems A vector with different elements to look for.
 * @param vecItemsCount A vector with the number of occurrences for each different
 *						element
 * @param ind The index of the first element of the subsequence */
/*public void countSupportForEachElement(Vector alphabet, long[] arSup, Vector dbSup, 
                                       Vector dbPos, int first, int last)
{
    // Look for serial elements
    for (int i=first; i<last; i++)
    {
        ItemSet set = elementAtIndex(i).getSet();
        for (int j=0; j<set.size(); j++)
        {
            int ind = alphabet.indexOf(set.elementAt(j));
            if (-1!=ind)
            {
                BitSet bs = (BitSet)dbSup.elementAt(ind);
                if (!bs.get(m_id))
                {
                    bs.set(m_id, true);
                    (arSup[ind])++;     
                    Vector pos = (Vector)dbPos.elementAt(ind);
                    pos.setElementAt(new Short("0"), m_id);
                }
            }
        }
    }
}*/
//________________________________________________________________
/**
 * equals: Object -> boolean
 * Verifies if this sequence is equal to the received object.
 * @param objSeq A sequence to compare with this sequence.
 * @return True if objSeq is equal to this sequence
 */
@Override
public boolean equals(Object objSeq)
{
    EventSequence seq = (EventSequence) objSeq;
    if (seq.length() == m_size)
        for (int i=0; i<m_size; i++)
        {
            if (!(m_events[i]).equals(seq.m_events[i]))
                return false;
        }
    else
        return false;
    return true;
}
//________________________________________________________________
/**
 * allK_1SubsequencesAreContainedIn: Vector x int -> boolean
 * Verifies if all maximal subsequences are contained in a vector of sequences.
 * @param seqVector The vector of sequences to be inspectioned.
 * @return True if all maximal subsequences are contained on the vector.
 */
/*public boolean allK_1SubsequencesAreContainedIn(Vector seqVector)
{
    Vector allSubseq = allK_1Subsequences();
    for (int i=0; i<allSubseq.size(); i++)
    {
        int j=0;
        int isContained = 0;
	EventSequence subseq = (EventSequence)allSubseq.elementAt(i);
	while ((j<seqVector.size())&&(-1!=isContained))
	{
            // All subsequences have to be frequent ==> gap=0
            EventSequence seq = (EventSequence)seqVector.elementAt(j);
            isContained =((EventSequence)subseq).isContainedIn(seq, 0, 0, seq.length());
            j++;
	}
	if (-1==isContained)
            return false;
    }
    return true;
}*/
//________________________________________________________________
/**
 * allContiguousK_1SubsequencesAreContainedIn: Vector x int -> boolean
 * Verifies if all contiguous maximal subsequences are contained in a vector of sequences.
 * @param seqVector The vector of sequences to be inspectioned.
 * @return True if all maximal subsequences are contained on the vector.
 */
/*public boolean allContiguousK_1SubsequencesAreContainedIn(Vector seqVector, int k)
{
    // For k<=2 all subsequences are frequent, by definition of a new 2-candidate
    if (k<=2)
        return true;
    ItemSet sk = m_events[m_size-1].getSet();
    ItemSet s1 = m_events[0].getSet();
    boolean ok = false;
    if (s1.size()>1)
    {
        int i=0;
        while (i<seqVector.size())
        {
            EventSequence s =(EventSequence)seqVector.elementAt(i++);
            ItemSet ss1 = s.elementAtIndex(0).getSet();
            int j=0;
            // Verifies the first itemset
            if (ss1.isPrefixOf(s1))
            {
                // Verifies interior and last itemsets
                for (j=1; j<m_size; j++)
                    if (!m_events[j].getSet().equals(s.m_events[j].getSet()))
                        break;
                if (j==m_size) return true;
            }
        }
        return false;
    }
    if (sk.size()>1)
    {
        int i=0;
        while (i<seqVector.size())
        {
            EventSequence s =(EventSequence)seqVector.elementAt(i++);
            ItemSet ssk = s.elementAtIndex(s.m_size-1).getSet();
            int j=0;
            // Verifies the first itemset
            if (ssk.isSuffixOf(sk))
            {
                // Verifies interior and last itemsets
                for (j=0; j<m_size-1; j++)
                    if (!m_events[j].getSet().equals(s.m_events[j].getSet()))
                        break;
                if (j==m_size-1) return true;
            }
        }
        return false;    
    } 
    return true;
}*/
//________________________________________________________________
/**
 * allElementsBelongTo: Vector -> boolean
 * Tests if all sequence elements belong to a Vector
 * @param vec A vector of elements.
 * @return True if all elements of this sequence belongs to the received vector.
 */
/*public boolean allElementsBelongTo(Vector vec)
{
    int i=0;
    boolean ok = true;
    while (ok && (i<m_alphabetSize))
        ok = m_seqAlphabet[i++].belongsTo(vec);
    return ok;
}*/
//________________________________________________________________
/**
 * Tests if a sequence already exists in a vector of sequences
 * @param vec The received vector.
 */
/*public boolean existsIn(Vector vec)
{
    int i=0;
    boolean ok = false;
    while ((!ok) && (i<vec.size()))
        ok = equals((EventSequence)vec.elementAt(i++));
    return ok;
}*/
//________________________________________________________________
// 			TRANSFORMERS
//________________________________________________________________
/**
 * Transform this sequence into a string, representing each element separeted by a comma.
 * @return The string representing this sequence
 */
@Override
public String toString()
{
    String st ="<";
    if (0<m_size)
    {
        st+=(m_events[0]).toString();
        for (int i=1; i<m_size; i++)
			st+=","+(m_events[i]).toString();
    }
    st += ">:"+ String.valueOf(m_support)
		   +" c:"+ String.valueOf(Math.round(m_conf*100))+"%";
	//	   +" lf:"+ String.valueOf((m_lift*100))+"%";
    return st;
}
//________________________________________________________________
/**
 * Returns all its k-1-subsequences, with its length equal to k
 */
/*public Vector allK_1Subsequences()
{
    int size = m_size;
    Vector subSeqs = new Vector();
    if (size > 1)
    {
        for (int i=0; i<size; i++)
        {
            EventSequence sub1 = subsequence(0,i-1);
            EventSequence sub2 = subsequence(i+1, size-1);
            EventSequence newSub = sub1.concatenateWith(sub2);
            // If the new subsequence doesn't exists on the vector we insert it
            if (!newSub.existsIn(subSeqs))
                subSeqs.addElement(newSub);
        }
    }
    subSeqs.trimToSize();
    return subSeqs;
}*/
//________________________________________________________________
/**
 * Returns all its contiguous k-1-subsequences, with its length equal k
 * only works for k>2
 * @return A vector with all of its contiguous maximal subsequences
 */
/*public Vector allContiguousK_1Subsequences()
{
    Vector subSeqs = new Vector(0);
    EventSequence s_init = subsequence(0, m_size-2);
    ItemSet sk = m_events[m_size-1].getSet();
    TimePoint tk = m_events[m_size-1].getInstant();
    if (sk.size()>1)
    {
        Element last = sk.elementAt(sk.size()-1);
        ItemSet set = sk.subset(0, sk.size()-2);
        set.addElement(last);
        subSeqs.addElement(s_init.addEvent(new Event(set, tk)));
    }
    return subSeqs;
}*/
//________________________________________________________________
/**
 * length: -> Integer
 * @return The length of this sequence
 */
public int length ()
{   return m_size; 	}
//________________________________________________________________
/**
 * Returns the position index of the first occurrence of el in this sequence.
 * @param el The element to find.
 * @param n
 * @return The position index.
 */
public int indexOf(Element el, int n)
{
    while ((n<m_size) && !((m_events[n].getSet()).contains(el)))
        n++;
    if (n>=m_size) 
        return -1;
    else 
        return n;
}
//________________________________________________________________
/*public boolean sharesSuffixWithPrefixOf(EventSequence t)
{
    ItemSet s1 = m_events[0].getSet();
    ItemSet t1 = t.m_events[0].getSet();
    if (m_size==1 && t.m_size==1)
        return s1.sharesSuffixWithPrefixOf(t1);

    int firstT=0;
    if (s1.size()==1) firstT=1;
    else if (!t.m_events[0].getSet().isSuffixOf(s1))
        return false;
    
    for (int i=1; i<m_size-1; i++)
        if (!m_events[i].getSet().equals(t.m_events[i-firstT].getSet()))
            return false;
    
    if (1==m_size) return true;
    
    ItemSet tk = t.m_events[t.m_size-1].getSet();
    ItemSet sk = m_events[m_size-1].getSet();
    if (tk.size()==1)
        return t.m_events[t.m_size-2].getSet().equals(sk);           
    else 
        return sk.isPrefixOf(tk);
}*/
/**
 * @param s
 * @param maxGap
 * @param init
 * @param end
 * @return the index of the occurrence of this sequence in s, from position init to end, 
 * 				-1 if it is not found 
 */
public int isContainedIn(EventSequence s, int maxGap, int init, int end)
{
    if (m_size>s.m_size) return -1;
    return forwardPhase(s, maxGap, 0, init, end);
}
//________________________________________________________________
/**
 * The algorithm finds successive itemsets of this sequence in s as long as the 
 * gap between two consecutive events is less then the allowed gap.
 * If the existing gap is greater then the allowed, the algorithms switch to the
 * backward phase.
 * @param s The sequence to look in.
 * @param gap The maximal gap value.
 * @param pIndex
 * @param sInit The first index in s to look for
 * @param sEnd The last index in s to look for.
 * @return the position of this sequence in s
 */
protected int forwardPhase(EventSequence s, int gap, int pIndex, int sInit, int sEnd)
{
while (sInit<sEnd)
{
    ItemSet set = elementAtIndex(pIndex).getSet();
    int last = s.indexOf(set, sInit);
    if (-1==last) return -1;
    sInit = last;
    int i = pIndex+1;
    // It has found first pattern element in last position in s
    if ((last+1<s.m_size)&&(i<m_size))
    {
        //Looks for the next pattern element
        set = elementAtIndex(i++).getSet();
        int next = s.indexOf(set, last+1);
        while ((-1!=next)&&(next<s.m_size)&&(i<=m_size))
        {
            if (next-last-1>gap) {
                int newInit = backwardPhase(s, gap, i-2, next, sInit);                
                if (-1==newInit) break;
            }
            last = next;
            if (i<m_size){
                set = elementAtIndex(i++).getSet();
                next = s.indexOf(set, last+1);
            }
            else if ((i==m_size) && (next-last-1<=gap)) return sInit;
        }        
    }
    if (1==m_size && last!=-1) 
		return last;
    else {
        i=0;
        sInit++;
    }
}
return -1;
}
//________________________________________________________________
/**
 * The algorithm backtracks and "pulls up" previous itemsets. If pi is the last 
 * itemset (in the pattern) found and ti the index of its occurrence in s, 
 * the algorithm finds the first occurrence of pi-1th itemset in s after index 
 * ti-gap (ti-1). If (ti-2)-(ti-1)+1<gap it is necessary to backtrack another event. 
 * @param s The sequence to look in.
 * @param gap The maximal gap value.
 * @param pInd The new ith pattern position to look for after encounter at actualPos
 * @param actualPos The last position encountered.
 * @param end The last valid index to look for.
 * @return the position of this sequence in s
 */
protected int backwardPhase(EventSequence s, int gap, int pInd, int actualPos, int end)
{
    int last = actualPos;
    ItemSet set = elementAtIndex(pInd).getSet();
    int prev = s.indexOf(set, Math.max(actualPos-gap-1, end));
    // If the previous element was not found, or found too far away
    if (-1==prev || prev>=actualPos) 
        return -1;
    //Looks for the previous pattern element to verify if it is at a legal distance
    int i = pInd-1;
    while (i>=0 && -1!=prev && prev<last)
    {
        last = prev;
        set = elementAtIndex(i--).getSet();
        prev = s.indexOf(set, Math.max(last-gap-1, end));
    }
    if (-1!=prev && prev<last)  
        return prev;
    return -1;
}
//________________________________________________________________
/**
 * @param distSetSize
 */
public void updateSetSizeDistribution(int[] distSetSize)
{
	for (int i=0; i<m_size; i++)
		distSetSize[m_events[i].getSet().size()] ++;
}
//________________________________________________________________
}