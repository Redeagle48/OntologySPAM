package processrestrictionselements;

import logicprocess.OntologyHolder;
import logicprocess.RestrictionSequence;

import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Process_minPrecedes extends ProcessRestrictionElements{

	public Process_minPrecedes(OntologyHolder ontologyHolder) {
		// TODO Auto-generated constructor stub
		super.ontologyHolder = ontologyHolder;
	}

	@Override
	public void proceed(Node node, RestrictionSequence restrictionSequence) {
		//		// TODO Auto-generated method stub
		//		System.out.println("=========== Estou no minPrecedes ============");
		//		
		//		OWLClass item;
		//		
		//		NodeList nodeElements = node.getChildNodes();
		//		//handle restriction node
		//		for(int j = 0; j < nodeElements.getLength(); j++) {
		//			Node element = nodeElements.item(j);
		//			if (element instanceof Element) 
		//				
		//				System.out.println(element.getNodeName());
		//				
		//				/* Get some new classes. */
		//				item = GlobalVariables.factory.getOWLClass(IRI.create("http://www.d2pm.com/ontologies/ontology.owl#Item"));
		//				//OWLClass item = factory.getOWLClass(IRI.create("http://www.d2pm.com/ontologies/ontology.owl#Item"));
		//				
		//				System.out.println("Here: " + GlobalVariables.ontology.getOntologyID().getOntologyIRI().toString());
		//				// Add individual
		//				OWLIndividual john = 
		//						GlobalVariables.factory.getOWLNamedIndividual(IRI.create(GlobalVariables.ontology.getOntologyID()
		//						.getOntologyIRI().toString() + "#John"));
		//
		//				OWLClassAssertionAxiom classAssertionAx = GlobalVariables.factory.getOWLClassAssertionAxiom(
		//						item, john);
		//				// Add the axiom directly using the addAxiom convenience method on
		//				// OWLOntologyManager
		//				GlobalVariables.owlManager.addAxiom(GlobalVariables.ontology, classAssertionAx);
		//				
		//				Set<OWLIndividual> individuals = item.getIndividuals(GlobalVariables.ontology);
		//				for (OWLIndividual owlIndividual : individuals) {
		//					System.out.println(owlIndividual.toString());
		//				}
		//		}

		System.out.println("========> Processing MinPrecedes restriction");
		NodeList nodeElements = node.getChildNodes();

		String precedesValue, procedesValue;

		Element eElement = (Element)node;

		Element precedesElement = (Element) eElement.getElementsByTagName("precedes").item(0);
		Element procedesElement = (Element) eElement.getElementsByTagName("procedes").item(0);

		System.out.println("Precedes: " + precedesElement.getTextContent());
		System.out.println("Procedes: " + procedesElement.getTextContent());

		precedesValue = precedesElement.getTextContent();
		procedesValue = procedesElement.getTextContent();

		OWLOntology ont = ontologyHolder.getOWLOntology();
		OWLDataFactory factory = ontologyHolder.getOWLDataFactory();
		OWLOntologyManager manager = ontologyHolder.getOWLOntologyManager();

		/* Get some new classes. */
		OWLClass item = factory.getOWLClass(IRI.create(ont.getOntologyID()
				.getOntologyIRI().toString() + "#Item"));

		// Add individual
		//OWLIndividual itemIndividual = factory.getOWLNamedIndividual(IRI.create(ont.getOntologyID()
		//		.getOntologyIRI().toString() + "#itemA"));

		// Add individual
		OWLIndividual itemPrecedesIndividual = factory.getOWLNamedIndividual(":"+precedesValue, ontologyHolder.getPrefixOWLOntologyFormat());


		OWLClassAssertionAxiom classAssertionAx = factory.getOWLClassAssertionAxiom(
				item, itemPrecedesIndividual);
		
		//Sequence from which this items belong
		OWLIndividual itemsetIndividual = factory.getOWLNamedIndividual(":"+restrictionSequence.getSequenceName(),
				ontologyHolder.getPrefixOWLOntologyFormat());
		
		OWLObjectProperty hasItem = factory.getOWLObjectProperty(":hasItem",ontologyHolder.getPrefixOWLOntologyFormat());

		OWLObjectPropertyAssertionAxiom addaxiom1 = factory
				.getOWLObjectPropertyAssertionAxiom(hasItem, itemsetIndividual, itemPrecedesIndividual);
		
		// Add individual
		OWLIndividual itemProcedesIndividual = factory.getOWLNamedIndividual(":"+procedesValue, ontologyHolder.getPrefixOWLOntologyFormat());

		OWLObjectPropertyAssertionAxiom addaxiom2 = factory
				.getOWLObjectPropertyAssertionAxiom(hasItem, itemsetIndividual, itemProcedesIndividual);
		
		OWLClassAssertionAxiom classAssertionAx2 = factory.getOWLClassAssertionAxiom(
				item, itemProcedesIndividual);
		
		//TODO Fazer array de axiomas
		manager.addAxiom(ont,addaxiom1);
		manager.addAxiom(ont,addaxiom2);
		manager.addAxiom(ont, classAssertionAx);
		manager.addAxiom(ont, classAssertionAx2);
		
		OWLObjectProperty hasPrecedent = factory.getOWLObjectProperty(":hasPrecedent",ontologyHolder.getPrefixOWLOntologyFormat());

		OWLObjectPropertyAssertionAxiom axiom1 = factory
				.getOWLObjectPropertyAssertionAxiom(hasPrecedent, itemProcedesIndividual, itemPrecedesIndividual);

		AddAxiom addAxiom1 = new AddAxiom(ont, axiom1);
		// Now we apply the change using the manager.
		manager.applyChange(addAxiom1);

		ontologyHolder.processReasoner();
		
		try {
			manager.saveOntology(ont);
			//restrictionSequence.insertItem(itemValue);
			//restrictionSequence.insertRoot();
		} catch (OWLOntologyStorageException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
