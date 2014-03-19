/*-------------------------------------------------------------
--             PACKAGE - SEQUENTIAL PATTERN MINING           --
--                       PATTERN GROWTH                      --
--                                                           --
--                       Claudia M. Antunes                  --
--                          October 2002                     --
---------------------------------------------------------------
--                       ProjectedDB.java                    --
--------------------------------------------------------------*/
package d3m.spam.core;

import java.util.*;

//--------------------------------------------------------------------
//                         CLASS ProjectedDB
//--------------------------------------------------------------------
/** Class <CODE>ProjectedDB</CODE> implements projected Databases described 
 * in <B>"PrefixSpan: Mining Sequential Patterns Efficiently by Prefix-Projected 
 * Pattern Growth"</B> by Jian Pei, Jiawei Han et al [ICDE 2001].
 * @version 1.0
 * @author Claudia Antunes
 */
public class ProjectedDB extends Object
{
    /** An array with the indexes of sequences in the original DB. */
    int m_pDB[];
    /** An array with the indexes of the start of each subsequence. */
    int m_pSeq[];
    /** The number of sequences in the projected db. */
    int m_size;
	/** The size of the array. */
	int m_arraySize;

	/** Creates a new instance of ProjectedDB */
public ProjectedDB() 
{
 	m_size = 0;
	m_arraySize = m_size;
}

/**
 * 
 * @param pBD
 * @param pSeq
 */
public ProjectedDB(Vector<Integer> pBD, Vector<Integer> pSeq) 
{
	m_size = pBD.size();
	m_arraySize = m_size;
	m_pDB = new int[m_size];
	m_pSeq = new int[m_size];
	for (int i=0;i<m_size; i++) {	
		m_pDB[i] = (pBD.elementAt(i)).intValue();
		m_pSeq[i] = (pSeq.elementAt(i)).intValue();		
	}
}

/**
 * @param size
 */
public ProjectedDB(int size) 
{
	m_arraySize = size;
	m_size = 0;
	m_pDB = new int[m_arraySize];
	m_pSeq = new int[m_arraySize];
}

/**
 * @param pDB
 * @param pSeq
 */
public void set(int pDB, int pSeq)
{
	m_pDB[m_size] = pDB;
	m_pSeq[m_size] = pSeq;
	m_size++;
}

/**
 * @param pDB
 * @return true, if 
 */
public boolean contains(int pDB)
{	return (m_size>0 && m_pDB[m_size-1] == pDB);	}

/** 
 * Returns the index of the sequence (in the original database) at position i. 
 * @param i The sequence index at m_pBD.
 * @return The index of the sequence at position i.
 */
public int indexOfSequenceAt(int i)
{	return m_pDB[i];	}

/** 
 * Returns the index of the first symbol of subsequence at position i. 
 * @param i The sequence index at m_pSeq.
 * @return The index of the first symbol of subsequence at position i.
 */	
public int indexOfStartSubsequenceAt(int i)
{	return m_pSeq[i];	}

/**
 * @return Return the number of sequences in the projected db. 
 */
public int size()
{	return m_size;   }

@Override
public String toString()
{
 	String st = "[";
   	for (int i=0; i<m_size; i++)
   		st += "("+ String.valueOf(m_pDB[i]) + "," + String.valueOf(m_pSeq[i])+ ")";
   	st += "]";
   	return st;
}
}
