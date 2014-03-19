/*------------------------------------------------------------------
--               PACKAGE - SEQUENTIAL PATTERN MINING              --
--                               CORE                             --
--                                                                --
--                       Claudia M. Antunes                       --
--	                          March 2003                          --
--------------------------------------------------------------------
--                          Database.java                         --
--------------------------------------------------------------------*/
package d3m.spam.core;

import java.util.*;
import java.io.*;  

/**
 * Class Database
 * @author Claudia Antunes
 *
 */
public class Database extends Object
{
    /** The set of sequences. */
    Vector<EventSequence> m_sequences;
    /** The alphabet. */
    Vector<Element> m_alphabet;
	/* The sequence maximal length. */
	private int m_maxLength;
	private int m_lengthMode;
	/** The distribution of the length of sequences. */
	int[] m_distSeqLengthFreq;
	/** The distribution of the size of itemsets. */
	int[] m_distSetSizeFreq; 
	static private int MAX_SEQ_LEN = 1000;
	static private int MAX_SET_SIZE = 100;
    //________________________________________________________________
    /** Creates a new instance of Database */
    public Database()
    {
    	m_sequences = new Vector<EventSequence>(0);
		m_alphabet = new Vector<Element>(0);
		m_maxLength = 0;
		m_distSeqLengthFreq = new int[MAX_SEQ_LEN];
		m_distSetSizeFreq = new int[MAX_SET_SIZE];
		EventSequence.reset();
    }
    /**
     * 
     * @param filename
     * @throws Exception
     */
	public void write(String filename) throws Exception
	{
		FileWriter out = new FileWriter(filename, false);
		for (int i=0; i<m_sequences.size(); i++)
			out.write((m_sequences.elementAt(i)).toString()+"\n");
		out.close();
	}
	/**
	 * 
	 * @param s
	 */
	public void addElement(EventSequence s)
    {
    	m_sequences.addElement(s);
		m_maxLength = Math.max(m_maxLength, s.length());
		m_distSeqLengthFreq[s.length()] ++;
		s.updateSetSizeDistribution(m_distSetSizeFreq);
    }
	/**
	 * 
	 * @param i
	 * @return the ith event sequence
	 */
	public EventSequence elementAt(int i)
    {	return m_sequences.elementAt(i);	}
	/**
	 * 
	 * @return the number of sequences in the db
	 */
    public int getSize()
    {	return m_sequences.size();	}
    /**
     * 
     * @return the alphabet in the db
     */
    public Vector<Element> getAlphabet()
    {	return m_alphabet;	}
    /**
     * Updates the alphabet found
     * @param alphabet
     */
    public void setAlphabet(Vector<Element> alphabet)
    {	m_alphabet = alphabet;	}
    /**
     * 
     * @param v1
     * @param v2
     * @return a new vector with elements from both received vectors
     */
    public static Vector<Object> joinVectors(Vector<Object> v1, Vector<Object> v2)
    {
		for (int i=0; i<v2.size(); i++) {
            Object obj = v2.elementAt(i);
            if (-1==v1.indexOf(obj))
                v1.addElement(obj);
		}
		return v1;
    }
    @Override
	public String toString()
    {	return m_sequences.toString();	}
    
    /**
     * 
     * @return m_maxLength
     */
	public int getMaxLength()
	{	return m_maxLength;	}
	/**
	 * 
	 * @return ???
	 */
	public int getLengthMode()
	{	return m_lengthMode;	}
	
/*	private String getDistributions()
	{
		String st="distribution of length of sequences={";
		String list="\n[";
		for (int i=0; i<MAX_SEQ_LEN; i++){
			st+=" "+String.valueOf(i)+"="+String.valueOf(m_distSeqLengthFreq[i]);
			for (int j=0; j<m_distSeqLengthFreq[i]; j++)
				list+=String.valueOf(i)+",";
		}
		list += "]\n";
		System.out.println("\n\n\n");
		st+="}\ndistribution of baskets size={";
		for (int i=0; i<MAX_SET_SIZE; i++){
			st+=" "+String.valueOf(i)+"="+String.valueOf(m_distSetSizeFreq[i]);
			for (int j=0; j<m_distSetSizeFreq[i]; j++)
				list+=String.valueOf(i)+",";
		}
		list += "]\n";
		return st+"}"+list;
	}*/

	/**
	 * 
	 * @return a string with a description of this db
	 */
	public String getStatistics()
	{
		String st = "|ALPHABET|="+String.valueOf(m_alphabet.size())+" ALPHABET="+m_alphabet.toString()+"\n"        
				  + " |DB|="+String.valueOf(getSize())       
				  + " |SeqMaxLength|="+String.valueOf(m_maxLength);
		int maxLen = 0; int maxSeqLen = 0; int countBask = 0;
		for (int i=0; i<MAX_SEQ_LEN; i++){
			countBask += (m_distSeqLengthFreq[i]*i);
			if (maxLen<m_distSeqLengthFreq[i]){
				maxSeqLen = i;
				maxLen = m_distSeqLengthFreq[i];
			}		
		}
		st  +=" |SeqMeanLength|="+String.valueOf(Math.ceil(countBask/m_sequences.size()))
			+ " |SeqModeLength|="+String.valueOf(maxSeqLen)+"\n";  
		m_lengthMode = maxSeqLen;
		maxLen = 0; maxSeqLen = 0; int count = 0; int maxSize = 0;
		for (int i=0; i<MAX_SET_SIZE; i++){
			if (0!=m_distSetSizeFreq[i])
				maxSize = i;
			count += (m_distSetSizeFreq[i]*i);
			if (maxLen<m_distSetSizeFreq[i]){
				maxSeqLen = i;
				maxLen = m_distSetSizeFreq[i];
			}		
		}
		st += "|BaskMaxSize|="+String.valueOf(maxSize)
			+ " |BaskMeanSize|="+String.valueOf(Math.ceil(count/countBask))
			+ " |BaskModeSize|="+String.valueOf(maxSeqLen)+"\n";  
		return st;
	}
    //________________________________________________________________
}