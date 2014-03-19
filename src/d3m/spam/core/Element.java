/*-------------------------------------------------------------
--             PACKAGE - SEQUENTIAL PATTERN MINING           --
--                             CORE                          --
--                                                           --
--                       Claudia M. Antunes                  --
--	                       November 2001                     --
---------------------------------------------------------------
--                          Element.java                     --
--------------------------------------------------------------*/

package d3m.spam.core;

import java.util.*;

//--------------------------------------------------------------------
//			  CLASS Element
//--------------------------------------------------------------------
/**
 * Class <CODE>Element</CODE> for handling an sequence's element.
 * @version 1.0
 * @author Cláudia M. Antunes
 */
public class Element extends Object
{
    /** The element id. */
    protected String m_elem;
    /** The element index in the alphabet. */
    protected int m_index=-1;
    //________________________________________________________________
    /**
    * Creates a new empty instance of Element, representing the empty element
    */
    public Element ()
    {
        m_elem = new String();
        m_index = -2;
    }
    //________________________________________________________________
    /** Creates a new instance of Element
    * @param el A Character representing an element.
    * @param ind The index of this element in its alphabet.
    */
    public Element (Character el, int ind)
    {
        m_elem = String.valueOf(Character.toLowerCase(el.charValue()));
        m_index = ind;
    }
    //________________________________________________________________
    /** Creates a new instance of Element
     * @param el A String representing an element.
     * @param ind The index of this element in its alphabet.
     */
    public Element (String el, int ind)
    {
        m_elem = el.toLowerCase();
        m_index = ind;
    }
    /** Creates a new instance of Element
     * @param el A String representing an element.
     */
    public Element (String el)
    {
        m_elem = el.toLowerCase();
        m_index = -1;
    }
    /** Creates a new instance of Element
     * @param el the element to copy.
     */
    public Element (Element el)
    {
        m_elem = el.m_elem;
        m_index = el.m_index;
    }
    /** Creates a new instance of Element
     * @param el the element to copy.
     * @param ind The index of this element in its alphabet.
     */
    public Element (Element el, int ind)
    {
        m_elem = el.m_elem;
        m_index = ind;
    }
    //________________________________________________________________
    /**
     * @return the name for the element
     */
    public String getName()
    {
    	return m_elem;
    }
    /**
     * @return the element's index
     */
    public int getIndex()
    {   return m_index; }
    //________________________________________________________________
    /**
     * @return the hashcode for the element
     */
    @Override
	public int hashCode()
    {   return m_index; }
    //________________________________________________________________
    /**
     * @param alphabet the already discovered elements
     * @return the index of the element if already discovered, -1 otherwise
     */
    public int isAlreadyDiscovered(Vector<Element> alphabet)
    {
        int i = 0;
        while (i<alphabet.size() && 
             !(0==(alphabet.elementAt(i)).m_elem.compareToIgnoreCase(this.m_elem)))
            i++;
        if (i<alphabet.size())
            return (alphabet.elementAt(i)).getIndex();
        else 
            return -1;
    }
    //________________________________________________________________
    @Override
	public boolean equals(Object el)
    {   return (m_index==((Element)el).m_index);	}
    //________________________________________________________________
    /**
     * @param el the element to compare to
     * @return true, if el is greater than this element
     */
    public boolean isGreaterThan(Element el)
    {   return (m_index>el.m_index);    }
    //________________________________________________________________
    /**
     * @param el the element to compare to
     * @return true, if el is less than this element
     */
   public boolean isPreviousThan(Element el)
    {   return (m_index < el.m_index);  }
    //________________________________________________________________
    /**
     * Returns the element as a string.
     * @return the element as a string
     */
    @Override
	public String toString ()
    {   return m_elem;//+"="+String.valueOf(m_index);	
	}
    //________________________________________________________________
    /**
     * Verifies if the element belongs to the received array, after position n
     * @param array The array
     * @param size The size of array
     * @param n The first position to look for
     * @return -1 if Element does not belong to an array of Elements
     */
    public int belongsTo (Element array[], int size, int n)
    {
        while ((n<size) && (!equals(array[n])))
            n++;
        if (n<size)
            return n;
        else
            return -1;
    }
    //________________________________________________________________
    /**
     * Verifies if the element belongs to the received Vector.
     * @param vec The vector to search for the element.
     * @return TRUE if Element belongs to a Vector of Elements
     */
    public boolean belongsTo (Vector<Element> vec)
    {
        int i=0;
        boolean ok = false;
        while (i<vec.size() &&!ok)
            ok = equals(vec.elementAt(i++));
        return ok;
    }
    //________________________________________________________________
    /**
     * @param v the vector with the known elements
     * @param el the element to find
     * @return the element found
     */
	public static Element getElement(Vector<Element> v, String el)
	{
		for(int i=0; i<v.size(); i++){
			Element elem = v.elementAt(i);
			if (0==el.compareToIgnoreCase(elem.m_elem))
				return elem;
		}
		return null;
	}
    //________________________________________________________________
}

