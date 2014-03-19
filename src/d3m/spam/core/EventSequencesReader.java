/*------------------------------------------------------------------
--               PACKAGE - SEQUENTIAL PATTERN MINING              --
--                               CORE                             --
--                                                                --
--                       Claudia M. Antunes                       --
--	                          March 2003                          --
--------------------------------------------------------------------
--                   EventSequencesReader.java                    --
--------------------------------------------------------------------*/

package d3m.spam.core;
import ontologies.timeOntology.*;
import java.io.*;
import java.util.*;

//--------------------------------------------------------------------
//		       CLASS EventSequencesReader
//--------------------------------------------------------------------
/**
 * Class for read sequences from a file.
 * @author Claudia M. Antunes (claudia.antunes@dei.ist.utl.pt)
 * @version $Revision: 0.1 $
 */

public class EventSequencesReader extends Object
{
    /** The dataset */
    protected Database m_db;
    //________________________________________________________________
    /**
     * Creates a new instance of SequencesReader.
     */
    public EventSequencesReader()
    {
        m_db = new Database();
    }
    /**
     * Reads a file into a Vector of Sequences.
     * @param filename the filename
     * @param unit the time granularity of data
     * @exception Exception if the file is not read successfully
     */
    public EventSequencesReader(String filename, TimeUnit unit) throws Exception
    {
        FileReader in = new FileReader(filename);
        BufferedReader dataIn = new BufferedReader(in);
        m_db = new Database();
        String line;
        Vector<Element> alphabet = new Vector<Element>();
        while (null != (line=dataIn.readLine())){
			EventSequence s = new EventSequence(line, alphabet, unit);
//			if (s.length()>1)
				m_db.addElement(s);
//System.out.println(s);				
		}
        m_db.setAlphabet(alphabet);
        in.close();
        dataIn.close();
		
		System.out.println(m_db.getStatistics());
	}
    //________________________________________________________________
    /**
     * Returns a vector with read sequences.
     * @return the database
     */
    public Database getSequences()
	{   return m_db;	}
    //________________________________________________________________
}
