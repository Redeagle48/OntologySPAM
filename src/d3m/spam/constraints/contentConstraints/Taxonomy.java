/*------------------------------------------------------------------
--               PACKAGE - SEQUENTIAL PATTERN MINING              --
--                           CONSTRAINT                           --
--                                                                --
--                       Claudia M. Antunes                       --
--                         November 2003                          --
--------------------------------------------------------------------
--                         Taxonomy.java                          --
--------------------------------------------------------------------*/
package d3m.spam.constraints.contentConstraints;

import d3m.spam.core.Element;
import java.util.*;
import java.io.*;
import javax.swing.tree.*;
/**
 * The taxonomy is represented as a Tree, each concept only descends from
 * one parent.
 */
public class Taxonomy 
{
	/** The concepts. */
	protected Vector<Element> m_elems;
	/** The corresponding parents. */
	protected Vector<Integer> m_parents;
	/** The corresponding depths. */
	protected Vector<Byte> m_depths;
	/** The maximum depth. */
	protected byte m_maxDepth;
	/** m_nrBaseElements	 */
	protected int m_nrBaseElems;
//________________________________________________________________
/** Creates a new instance of Taxonomy */
public Taxonomy()
{
	m_elems = new Vector<Element>(0);
	m_parents = new Vector<Integer>(0);
	m_depths = new Vector<Byte>(0);
	m_maxDepth = 0;
	m_nrBaseElems = 0;
}
//________________________________________________________________
/**
 * @param model
 * @param dbalphabet
 */
public Taxonomy(DefaultTreeModel model, Vector<Element> dbalphabet)
{	
	m_elems = dbalphabet;
	m_nrBaseElems = m_elems.size();
	m_parents = new Vector<Integer>(m_nrBaseElems);
	m_depths = new Vector<Byte>(m_nrBaseElems);	
	for (int i=0; i<m_nrBaseElems; i++) {
		m_parents.addElement(new Integer(0));
		m_depths.addElement(new Byte((byte)0));
	}
	m_maxDepth = 0;
	addTree((DefaultMutableTreeNode)model.getRoot(), -1, (byte)0, dbalphabet);	
}
//________________________________________________________________
/**
 * @param treeModel
 * @param filename
 * @param dbalphabet
 * @return the taxonomy
 */
public static Taxonomy readTaxonomy(DefaultTreeModel treeModel, String filename, 
															Vector<Element> dbalphabet)
{
	String LEFT = "<"; String RIGHT = ">"; 
	String END = "/>"; String CLOSE = "</";
//	int n = dbalphabet.size();
	DefaultMutableTreeNode childNode;
	Stack<DefaultMutableTreeNode> stack = new Stack<DefaultMutableTreeNode>();
	stack.push((DefaultMutableTreeNode)treeModel.getRoot());
	try {
		FileReader in = new FileReader(filename);
        BufferedReader dataIn = new BufferedReader(in);
        String line;
        while (null != (line=dataIn.readLine())) {
			int first = line.indexOf(LEFT)+1;
			int last = line.indexOf(RIGHT);
			int end = line.indexOf(END);
			int close =  line.indexOf(CLOSE);
			// It found an element
			if (-1!=end){
				String newItem = line.substring(first, end);
				childNode = new DefaultMutableTreeNode(newItem);
				treeModel.insertNodeInto(childNode, 
							stack.peek(), 
							(stack.peek()).getChildCount());	
			}
			// Finishes a category
			else if (-1!=close)
				stack.pop();
			// It found a new category
			else if (-1!=last){
				String newItem = line.substring(first, last);
				childNode = new DefaultMutableTreeNode(newItem);
				treeModel.insertNodeInto(childNode, 
							stack.peek(),
							(stack.peek()).getChildCount());
				stack.push(childNode);
			}
		}
        dataIn.close();
		in.close();
	} catch (Exception ex)
	{ ex.printStackTrace();	}	
	return new Taxonomy(treeModel, dbalphabet);
}
//________________________________________________________________
/**
 * @param node
 * @param parent
 * @param dbalphabet
 */
private void addTree(DefaultMutableTreeNode node, int parent, byte depth, Vector<Element> dbalphabet)
{
	if (node.isLeaf()) {
		String name = ((String)node.getUserObject()).trim();
//		int i=0; boolean found = false;
		Element el = new Element(name);
		int index = el.isAlreadyDiscovered(m_elems);
		if (-1!=index)	{
			m_parents.set(index, new Integer(parent));
			m_depths.set(index, new Byte(depth));
			m_maxDepth = (byte)Math.max(depth, m_maxDepth);
		}
	}
	else {
		int id = m_elems.size();
		Element el = new Element((String)node.getUserObject(), id);
		m_elems.addElement(el);
		m_parents.addElement(new Integer(parent));
		m_depths.addElement(new Byte(depth));
		for (int i=0; i<node.getChildCount(); i++){
			DefaultMutableTreeNode child = (DefaultMutableTreeNode)node.getChildAt(i);
			addTree(child, id, (byte)(depth+1), dbalphabet);
		}
	}
}
//________________________________________________________________
/** 
 * Return the index of the parent of element, on the alphabet.
 * If the taxonomy only has items and do not contain item categories, 
 * the parent of every item is the root (-1). So the element is returned.
 * @param el
 * @param alphabet
 * @return the parent of the element el
 */
public int getParentOf(Element el, Vector<Element> alphabet)
{
	if (1==m_maxDepth) {
		int ind = alphabet.indexOf(el);
		if (-1!=ind)
			return ind;
		return -1;
	}
	else {
		int id = m_elems.indexOf(el);
		int parent;
		while ((-1!=id) && 
			   (-1!=(parent=(m_parents.elementAt(id)).intValue()))) {
			int alphInd = alphabet.indexOf(m_elems.elementAt(parent));
			if (-1!=alphInd) return alphInd;
			id = parent;
		}
		return id;
	}
}
//________________________________________________________________
/**
 * Return the set of items at the same level.
 * @param level
 * @return a vector with the alphabet
 */
public Vector<Element> getAlphabet(byte level)
{
	Vector<Element> tax = new Vector<Element>(0);
	for (int i=0; i<m_depths.size(); i++)
		if (level == (m_depths.elementAt(i)).byteValue())
			tax.addElement(m_elems.elementAt(i));
	return tax;
}
//________________________________________________________________
}
