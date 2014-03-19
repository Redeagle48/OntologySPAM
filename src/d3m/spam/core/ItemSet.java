/*------------------------------------------------------------------
--               PACKAGE - SEQUENTIAL PATTERN MINING              --
--                               CORE                             --
--                                                                --
--                          Claudia Antunes                       --
--							  March 2003                          --
--------------------------------------------------------------------
--                           ItemSet.java                         --
--------------------------------------------------------------------*/

package d3m.spam.core;

import java.util.*;
//--------------------------------------------------------------------
//			  CLASS ItemSet
//--------------------------------------------------------------------
/**
 * Class <CODE>ItemSet</CODE> for handling an paralel sequence's elements.
 * @version 1.0
 * @author Cláudia M. Antunes
 */
public class ItemSet extends Object
{
	/** The set of elements */
	protected Vector<Element> m_elems;
	/** The number of element*/
	protected int m_size=0;
	/** The frequency of this itemset, with respect to a db. */
	protected int m_support;
//________________________________________________________________
/**
 * Creates a new empty instance of ItemSet, representing the empty element.
 */
public ItemSet ()
{
    m_elems = new Vector<Element>();
    m_size = 0;
}
//________________________________________________________________
/** Creates a new instance of ItemSet with element e.
 * @param el The new element.
 */
public ItemSet(Element el)
{
    m_elems = new Vector<Element>(1);
    m_elems.addElement(el);
    m_size = 1;
}
//________________________________________________________________
/** Creates a new instance of ItemSet with elements in v.
 * @param v The set of elements.
 */
public ItemSet(Vector<Element> v)
{
    m_size = v.size();
    @SuppressWarnings("unchecked")
    Vector<Element> vc = (Vector<Element>)v.clone();
    m_elems = vc;
}
//________________________________________________________________
/** Creates a new instance of ItemSet with elements in v.
 * @param s The set of elements.
 */
public ItemSet(ItemSet s)
{
    m_size = s.m_elems.size();
    @SuppressWarnings("unchecked")
    Vector<Element> vc = (Vector<Element>)s.m_elems.clone();
    m_elems = vc;
}
//________________________________________________________________
@Override
public Object clone()
{
    ItemSet set = new ItemSet();
    set.m_size = m_size;
    @SuppressWarnings("unchecked")
    Vector<Element> vc = (Vector<Element>)m_elems.clone();
    set.m_elems = vc;
    return set;
}
//________________________________________________________________
/**
 * @param st_set
 * @param alphabet
 */
public ItemSet(String st_set, Vector<Element> alphabet)
{
    char comma = ',';
    int ind_first = st_set.indexOf("(", 0);
    int ind;
    String st_elem;
    m_elems = new Vector<Element>();    
    while (-1!=(ind = st_set.indexOf(comma, ind_first+1)))
    {
        st_elem = st_set.substring(ind_first+1, ind);         
        Element elem = new Element (st_elem);
       	elem = restoreAlphabet(elem, alphabet);
        ind_first = ind;
        addElement(elem);
    }
    st_elem = st_set.substring(ind_first+1, st_set.length());
    Element elem = new Element (st_elem);
    elem = restoreAlphabet(elem, alphabet);
    addElement(elem);   
    m_size = m_elems.size();
}
//________________________________________________________________
/**
 * @param st_set
 * @param alphabet
 * @return the set of itemsets created
 */
public static Vector<ItemSet> createItemSets(String st_set, Vector<Element> alphabet)
{
	Vector<ItemSet> v = new Vector<ItemSet>(0);
	char left='('; char right=')';	char comma = ',';
	int ind=-1;
	while (-1!=(ind = st_set.indexOf(left, ind+1))) {
		String setSt = st_set.substring(ind, st_set.indexOf(right, ind)+1);
		int ind_first = setSt.indexOf("(", 0);
		int ind2;
		String st_elem;
		ItemSet set = new ItemSet();
		while (-1!=(ind2 = setSt.indexOf(comma, ind_first+1))) {
			st_elem = setSt.substring(ind_first+1, ind2).trim();         
			Element elem = Element.getElement(alphabet, st_elem);
			set.addElement(elem);
			ind_first = ind2;
		}
		st_elem = setSt.substring(ind_first+1, setSt.length()-1).trim();
		Element elem = Element.getElement(alphabet, st_elem);
		set.addElement(elem);
		v.addElement(set);
	}
	return v;
}
//________________________________________________________________
/*public ItemSet(int n, Vector alphabet, double z)
{
    RandomZipf zipf = new RandomZipf(z, alphabet.size());
    m_elems = new Vector(n);    
    for (int i=0; i<n; i++)
    {
        int rand = zipf.newNumber();
        Element elem = (Element)alphabet.elementAt(rand);
        if (-1 == m_elems.indexOf(elem))
            addElement(elem);
    }
    m_size = m_elems.size();
}*/
//________________________________________________________________
private Element restoreAlphabet(Element elem, Vector<Element> alphabet)
{
    // If element doesn't exist on m_seqAlphabet
    int exists = elem.isAlreadyDiscovered(alphabet);
    // It is created a new element with a new index
    if (-1==exists)
    {
        elem = new Element(elem.getName(), alphabet.size());
        alphabet.addElement(elem);
    }	
    // It is created a new element with the same index
    else
        elem = new Element(elem.getName(), exists);
    return elem;
}
//________________________________________________________________
/**
 * @param i the index of the item to return
 * @return the ith element in the itemset
 */
public Element elementAt(int i)
{
    return m_elems.elementAt(i);
}
//________________________________________________________________
/**
 * @param el the element to add
 */
public void addElement(Element el)
{
    if (-1 == m_elems.indexOf(el))
    {
        int i=0;
        while ((i<m_size) && (el.isGreaterThan(m_elems.elementAt(i))))
            i++;
        m_elems.add(i, el);
        m_size ++;
    }
}
//________________________________________________________________
/**
 * Checks if two itemsets are equal.
 * @param set The other set to be compared with
 * @return true if the all elements in one set, are also in the other
 */
@Override
public boolean equals(Object set)
{	
    if (m_size != ((ItemSet)set).m_size) return false;
    boolean equal = true;
    int i = 0;
    while (	(i<m_size) && (true==(equal=(elementAt(i).equals(((ItemSet)set).elementAt(i))))) )
        i++;
    return equal;	
}
//________________________________________________________________
/**
 * @param t
 * @return true, if this is a subset of t
 */
public boolean isSubsetOf(ItemSet t)
{
	if (m_size > t.m_size) return false;
	int ind = t.indexOf(elementAt(0));
	boolean ok = (-1!=ind);
	int i = 1;
	while (ok && i<m_size && ind+i<t.m_size) {
		ok = elementAt(i).equals(t.elementAt(ind+i));
		i++;
	}
	return ok;	
}
//________________________________________________________________
/**
 * @param t
 * @return true, if this is a prefix of t
 */
public boolean isPrefixOf(ItemSet t)
{
	if (m_size > t.m_size) 
		return false;
    int i=0;
    while (i<m_size) {
        if (!(m_elems.elementAt(i)).equals(t.m_elems.elementAt(i)))
            return false;
		i++;
	}
    return true;
}
//________________________________________________________________
/**
 * @param t
 * @return true, if this is a sufix of t
 */
public boolean isSuffixOf(ItemSet t)
{
    if (t.m_size<m_size) return false;
	int dif = t.m_size - m_size;
    for (int i=0; i<m_size; i++)
		if (!(m_elems.elementAt(i)).equals(t.m_elems.elementAt(i+dif)))
            return false;
    return true;
}
//________________________________________________________________
/** 
 * Verifies if it is an empty set.
 * @return true, if it is the empty set
 */
public boolean isEmpty()
{   return (0==m_size);    }
//________________________________________________________________
/**
 * Returns the element as a string.
 * @return the element as a string
 */
@Override
public String toString ()
{	
    String st;
    if (0==m_size) return "";
    st = "(" + (elementAt(0)).toString();
    for (int i=1; i<m_size; i++)
        st+=","+(elementAt(i)).toString();
    st += ")";
    return st;	
}
//________________________________________________________________
/**
 * @return Returns the number of elements in the set.
 */
public int size()
{
    return m_elems.size();
}
//________________________________________________________________
/**
 * Returns the itemset as a vector
 * @return the itemset as a vector
 */
public Vector<Element> toVector()
{	
    return m_elems;	
}
//________________________________________________________________
/**
 * Verifies if this itemset contains an element.
 * @param el The itemset to look for.
 * @return true, if it contains el
 */
public boolean contains(Element el)
{
    if (-1==indexOf(el)) 
        return false;
    else 
        return true;
}
//________________________________________________________________
/**
 * Verifies if this itemset contains another.
 * @param set The itemset to look for.
 * @return true, if it contains set
 */
public boolean contains(ItemSet set)
{
    if (m_size<set.m_size) return false;
    int ind = 0;
    int i = 0;
    while ((i<set.m_size) && (-1!=ind))
    {
        ind = indexOf(set.elementAt(i));
        i++;
    }
    if (-1==ind) return false;
    else return true;
}
//________________________________________________________________
/**
 * @param set
 * @return true, if the suffix of this itemset is the same as the prefix of set
 */
public boolean sharesSuffixWithPrefixOf(ItemSet set)
{
    boolean ok = true;
    int i = 1;
    while (i<m_size && ok)
    {
        ok = m_elems.elementAt(i).equals(set.m_elems.elementAt(i-1));
        i++;
    }
    return ok;
}
//________________________________________________________________
/**
 * Verifies if this set is contained in another set.
 * @param set
 * @return true, if this is contained in set
 */
public boolean isContainedIn(ItemSet set)
{
    return set.contains(this);	
}
//________________________________________________________________
/**
 * @param el
 * @return Returns the array index where element occurs. Returns -1 if it not occur.
 */
private int indexOf(Element el)
{
    int i = 0;
    boolean ok = true;
    while ((i<m_size) && (ok))
    {
        if (el.isPreviousThan(elementAt(i))) return -1;
        if (el.equals(elementAt(i))) return i;
        i++;
    }
    return -1;	
}
//________________________________________________________________
/**
 * @param set the set to unify with
 * @return the union of this set with another one.
 */
public ItemSet union(ItemSet set)
{
    ItemSet newSet = new ItemSet(this);
    for (int i=0; i<set.m_size; i++)
    {
        Element el = set.elementAt(i);
        int ind = indexOf(el);
        if (-1== ind)
        newSet.addElement(el);
    }
    newSet.m_size = newSet.m_elems.size();
    return newSet;
}
//________________________________________________________________
/**
 * @param i the element to remove
 * @return an itemset without the ith element
 */
public ItemSet without(int i)
{
    ItemSet set = new ItemSet(this);
    set.m_elems.remove(i);
    --set.m_size;
    return set;
}
//________________________________________________________________
/**
 * @param init
 * @param end
 * @return returns a subset between init, inclusive, and end, exclusive.
 */
public ItemSet subset(int init, int end)
{
    Vector<Element> elems = new Vector<Element>();
    for (int i=init; i<end; i++)
        elems.addElement(m_elems.elementAt(i));
    return new ItemSet(elems);
}
//________________________________________________________________
/**
 * @return Returns all its k-1-subsets, with its length equal to k
 */
private Vector<ItemSet> allK_1Subsets()
{
    int size = m_size;
    Vector<ItemSet> subSets = new Vector<ItemSet>();
    for (int i=0; i<size; i++)
    {
        ItemSet newSub = subset(0,i).union(subset(i+1, size));
        // If the new subsequence doesn't exists on the vector we insert it
        if (!newSub.existsIn(subSets))
            subSets.addElement(newSub);
    }
    subSets.trimToSize();
    return subSets;
}
//________________________________________________________________
/**
 * allK_1SubsetsAreContainedIn: Vector x int -> boolean
 * Verifies if all maximal subsets are contained in a vector of sets.
 * @param Lk_1 The vector of sets to be inspectioned.
 * @return True if all maximal subsets are contained in the vector.
 */
public boolean allK_1SubsetsAreContainedIn(Vector<ItemSet> Lk_1)
{
    Vector<ItemSet> allSubset = allK_1Subsets();
    for (int i=0; i<allSubset.size(); i++)
    {
        int j=0;
        boolean isContained = false;
        ItemSet subset = allSubset.elementAt(i);
        while ((j<Lk_1.size())&&(!isContained))
        {
            isContained = subset.isContainedIn(Lk_1.elementAt(j));
            j++;
        }
        if (!isContained)
            return false;
    }
    return true;
}
//________________________________________________________________
/**
 * Verifies if the itemset exists in the received Vector
 * @param vec
 * @return TRUE if itemset exists in a Vector of Itemsets
 */
public boolean existsIn (Vector<ItemSet> vec)
{
    int n=0;
    boolean ok = false;
    while ((n<vec.size()) &&!ok)
        ok = equals(vec.elementAt(n++));
    return ok;
}
//________________________________________________________________
/**
 * @return the support for this itemset
 */
public int getSupport()
{   return m_support;   }
//________________________________________________________________
/**
 * Updates the support
 * @param sup
 */
public void setSupport(int sup)
{   m_support = sup;    }
//________________________________________________________________
/**
 * @param set
 * @return the number of common elements in this set and another one received
 */
public int nrOfCommon(ItemSet set)
{
	int n=0;
	if (m_size < set.m_size) {
		for (int i=0; i<m_size; i++)
			if (set.contains(elementAt(i)))
				n++;
	}
	else
		for (int i=0; i<set.m_size; i++)
			if (contains(set.elementAt(i)))
				n++;
	return n;
}
//________________________________________________________________
/**
 * @param si
 * @return an itemset corresponding to the prefix of this itemset, until it finds si
 */
public ItemSet getPrefixUntil(ItemSet si)
{
	Element last = si.elementAt(si.size()-1);
	Vector<Element> v = new Vector<Element>();
	int i = 0;
    while (i<m_size) {
		Element el = elementAt(i++);
        v.addElement(el);
		if (last.equals(el))
			break;
    }
    return new ItemSet(v);	
}
//________________________________________________________________
}
