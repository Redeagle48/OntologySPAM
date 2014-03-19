/*------------------------------------------------------------------
--               PACKAGE - SEQUENTIAL PATTERN MINING              --
--                           CONSTRAINT                           --
--                                                                --
--                       Claudia M. Antunes                       --
--                        September 2003                          --
--------------------------------------------------------------------
--                      SpamConstraint.java                       --
--------------------------------------------------------------------*/

package d3m.spam.constraints;
import d3m.spam.core.*;
import d3m.spam.constraints.contentConstraints.*;
import d3m.spam.constraints.temporalConstraints.*;
//import d3m.ontologies.timeOntology.*;

import java.util.*;
/**
 * <B>TDMConstraint</B> specifies a constraint for the Sequential Pattern Mining 
 * Problem. It consists of a triple (Phi, Theta, sigma) with: <br>
 * - <I>Phi</I> is a ContentConstraint (Alphabet=the set of possible items, <br>
 *       Taxonomy=a taxonomy on the items, <br>
 *       Pi=a finite automata for specifying order constraints)
 * - <I>Theta</I> is a TemporalConstraint (TimeInterval, TimeQuantity=time gap)
 * - <I>sigma</I> is a existential constraint, that is, a support constraint.
 */
public class TDMConstraint extends Object
{
    /** The constraint on contents. */
    public ContentConstraint m_contentC;
    /** The constraint on time. */
    public TemporalConstraint m_temporalC;
    /** The constraint on existence, that is, support. */
    public ExistentialConstraint m_existC;    
//________________________________________________________________
/** Creates an empty instance of Constraint */
public TDMConstraint()
{
}
//________________________________________________________________
/** Creates a new instance of Constraint 
 * @param content
 * @param temporal
 * @param exist
 */
public TDMConstraint(ContentConstraint content, TemporalConstraint temporal, ExistentialConstraint exist)
{
    m_contentC = content;
    m_temporalC = temporal;
	m_existC = exist;
}
//________________________________________________________________
@Override
public String toString()
{
    return getClass().getName();
}
//________________________________________________________________
/**
 * @param vec
 * @param from
 * @param size
 * @return the index of the maximal element of a vector with positive integers, 
 * -1 if it is empty
 */
protected int getIndexMaxElement(int[] vec, int from, int size)
{
    long max = -1;
    int maxIndex = -1;
    for (int i=from; i<size; i++)
        if (vec[i] > max) {
            max = vec[i];
            maxIndex = i;
        }
    return maxIndex;
}
//________________________________________________________________
/** 
 * Sorts the vector of items and their corresponding parameters
 * by descending support.
 */
/*public void getSortedFrequentElements(Vector alphabet, int[] sup, Vector[] seqs, 
																	Vector[] pos)
{
	int n = alphabet.size();
	int from = 0; int count = 0;
	for (int i=0; i<n; i++) {
        int indMax = getIndexMaxElement(sup, from++, n);
		if (-1!=indMax && m_existC.accepts(sup[indMax])) {
			Element elOld = (Element)alphabet.elementAt(i);
			alphabet.setElementAt(alphabet.elementAt(indMax), i);
			alphabet.setElementAt(elOld, indMax);
			int supOld=sup[i]; sup[i]=sup[indMax]; sup[indMax]=supOld;
			Vector seqsOld=seqs[i]; seqs[i]=seqs[indMax]; seqs[indMax]=seqsOld;
			Vector posOld=pos[i]; pos[i]=pos[indMax]; pos[indMax]=posOld;
			count++;
		}
		else
		{
			sup[indMax] = -1;
			seqs[indMax] = null;
			pos[indMax] = null;
			from--;
		}
	}
	// Remove all non-frequent elements
	while (count<alphabet.size())
		alphabet.removeElementAt(alphabet.size()-1);
	alphabet.setSize(count);
}*/
//________________________________________________________________
/** 
 * Sorts the vector of items and their corresponding parameters
 * by descending support.
 * @param alphabet
 * @param sup
 * @param projDBs
 */
public void getSortedFrequentElements(Vector<Element> alphabet, int[] sup, ProjectedDB[] projDBs)
{
	int n = alphabet.size();
	int from = 0; int count = 0;
	for (int i=0; i<n; i++) {
        int indMax = getIndexMaxElement(sup, from++, n);
		if (-1!=indMax && m_existC.accepts(sup[indMax])) {
			Element elOld = alphabet.elementAt(i);
			alphabet.setElementAt(alphabet.elementAt(indMax), i);
			alphabet.setElementAt(elOld, indMax);
			int supOld=sup[i]; sup[i]=sup[indMax]; sup[indMax]=supOld;
			ProjectedDB old = projDBs[i]; projDBs[i]=projDBs[indMax]; projDBs[indMax]=old;
			count++;
		}
		else {
			sup[indMax] = -1;
			projDBs[indMax] = null;
			from--;
		}
	}
	// Remove all non-frequent elements
	while (count<alphabet.size())
		alphabet.removeElementAt(alphabet.size()-1);
	alphabet.setSize(count);
}

//________________________________________________________________
}