package processxml;
import java.io.File;
import java.io.IOException;
import java.util.Set;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import logicprocess.FilesLocation;
import logicprocess.OntologyHolder;
import logicprocess.RestrictionSequence;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import processrestrictionselements.Process_leaf;
import processrestrictionselements.Process_minPrecedes;
import processrestrictionselements.Process_root;

public class ProcessXML
{
	boolean debug_parserXML = true;
	OWLOntologyManager manager;
	OWLOntology ont;

	public ProcessXML() {

	}

	public static void main(String [] args) throws Exception
	{
		ProcessXML processxml = new ProcessXML();
		System.out.println("isValid?: " + processxml.isValidXML());
		//readXML();
		processxml.owlApiExplorer();
	}

	public void execute(OntologyHolder ontologyHolder) {
		System.out.println("Analyzing restriction's XML..........isValid?: " + isValidXML());
		try {
			readXML(ontologyHolder);
		} catch (ParserConfigurationException | SAXException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Check if xml file is valid according to a schema
	 * @return boolean
	 */
	public boolean isValidXML() {

		Source schemaFile = new StreamSource(new File(FilesLocation.RESTRICTIONS_XML_SCHEMA));
		Source xmlFile = new StreamSource(new File(FilesLocation.RESTRICTIONS_XML));

		SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

		boolean isvalid = false;
		try{
			Schema schema = schemaFactory.newSchema(schemaFile);
			Validator validator = schema.newValidator();
			validator.validate(xmlFile);
			isvalid = true;
			//System.out.println(xmlFile.getSystemId() + " is valid");
		}
		catch (SAXException e) 
		{
			System.out.println(xmlFile.getSystemId() + " is NOT valid");
			System.out.println("Reason: " + e.getLocalizedMessage());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return isvalid;
	}

	public void readXML(OntologyHolder ontologyHolder) throws ParserConfigurationException, SAXException, IOException {
		File xmlFile = new File(FilesLocation.RESTRICTIONS_XML);

		//Como passar o XML para instancias da ontologia
		//Get the DOM Builder Factory
		DocumentBuilderFactory factory = 
				DocumentBuilderFactory.newInstance();

		//Get the DOM Builder
		DocumentBuilder dbuilder = factory.newDocumentBuilder();

		//Load and Parse the XML document
		//document contains the complete XML as a Tree.
		Document document = dbuilder.parse(xmlFile);

		//optional, but recommended
		//read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
		document.getDocumentElement().normalize();

		if(debug_parserXML)
			System.out.println("Restrictions' XML -> Root element: " + document.getDocumentElement().getNodeName());

		//Iterating through the nodes and extracting the data.
		NodeList nodeList = document.getDocumentElement().getChildNodes();

		System.out.println("Restrictions' XML -> Length: " + nodeList.getLength());

		for(int i = 0; i < nodeList.getLength(); i++){
			if(nodeList.item(i) instanceof Element && nodeList.item(i).getNodeName().equals("restriction"));
			Node node = nodeList.item(i);

			//Node node = nodeList.item(1); // extrai primeira restricao

			if (node instanceof Element && nodeList.item(1).getNodeName().equals("restriction")) {

				System.out.println("======================= Analyzing a restriction =======================");

				Element restrictionElement = (Element) node;
				System.out.println("Restriction's name: " + restrictionElement.getElementsByTagName("title").item(0).getTextContent());
				
				//Object that represents the present restriction
				//String test = restrictionElement.getElementsByTagName("title").item(0).getTextContent();
				RestrictionSequence restrictionSequence = new RestrictionSequence(restrictionElement.getElementsByTagName("title").item(0).getTextContent());

				NodeList restrictions = node.getChildNodes();
				//handle restriction node

				for(int j = 0; j < restrictions.getLength(); j++) {
					Node restrictionElements = restrictions.item(j);
					if (restrictionElements instanceof Element)  {
						//System.out.println(restrictionElements.getNodeName());
						if(restrictionElements.getNodeName().equals("root")) {
							new Process_root(ontologyHolder).proceed(restrictionElements,restrictionSequence);
						}
						if(restrictionElements.getNodeName().equals("leaf")) {
							new Process_leaf(ontologyHolder).proceed(restrictionElements,restrictionSequence);
						}
						if(restrictionElements.getNodeName().equals("min_precedes")) {
							new Process_minPrecedes(ontologyHolder).proceed(restrictionElements,restrictionSequence);
						}
					}
				}
			}
		} 
	}

	public void owlApiExplorer() throws OWLOntologyCreationException,OWLOntologyStorageException {

		// The OWLOntologyManager is at the heart of the OWL API, we can create
		// an instance of this using the OWLManager class, which will set up
		// commonly used options (such as which parsers are registered etc.
		// etc.)
		manager = OWLManager.createOWLOntologyManager();

		// Load the ontology
		ont = manager.loadOntologyFromOntologyDocument(new File(FilesLocation.ONTOLOGY));
		System.out.println("Loaded: " + ont.getOntologyID());

		// We can use the manager to get a reference to an OWLDataFactory. The
		// data factory provides a point for creating OWL API objects such as
		// classes, properties and individuals.
		OWLDataFactory factory = manager.getOWLDataFactory();

		// Class assertion axioms //We can also explicitly say than an
		// individual is an instance of a given class. To do this we use a Class
		// assertion axiom.

		Set<OWLClass> classes = ont.getClassesInSignature();
		for (OWLClass owlClass : classes) {
			System.out.println(owlClass.getIRI());
		}

		/* Get some new classes. */
		OWLClass item = factory.getOWLClass(IRI.create("http://www.d2pm.com/ontologies/ontology.owl#Item"));
		//OWLClass item = factory.getOWLClass(IRI.create("http://www.d2pm.com/ontologies/ontology.owl#Item"));

		// Add individual
		OWLIndividual john = factory.getOWLNamedIndividual(IRI.create(ont.getOntologyID()
				.getOntologyIRI().toString() + "#John"));

		OWLClassAssertionAxiom classAssertionAx = factory.getOWLClassAssertionAxiom(
				item, john);
		// Add the axiom directly using the addAxiom convenience method on
		// OWLOntologyManager
		manager.addAxiom(ont, classAssertionAx);

		Set<OWLIndividual> individuals = item.getIndividuals(ont);
		for (OWLIndividual owlIndividual : individuals) {
			System.out.println(owlIndividual.toString());
		}
	}
}
