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
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Process_root extends ProcessRestrictionElements {

	public Process_root(OntologyHolder ontologyHolder) {
		// TODO Auto-generated constructor stub
		super.ontologyHolder = ontologyHolder;
	}

	@Override
	public void proceed(Node node, RestrictionSequence restrictionSequence) {
		System.out.println("========> Processing Root restriction");
		NodeList nodeElements = node.getChildNodes();
		System.out.println("Item name to be inserted: " + nodeElements.item(0).getNodeValue());

		String itemValue = nodeElements.item(0).getNodeValue();

		if(!restrictionSequence.existsRoot()){

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
			OWLIndividual itemIndividual = factory.getOWLNamedIndividual(":"+itemValue, ontologyHolder.getPrefixOWLOntologyFormat());


			OWLClassAssertionAxiom classAssertionAx = factory.getOWLClassAssertionAxiom(
					item, itemIndividual);

			manager.addAxiom(ont, classAssertionAx);

			/* Get some new classes. */
			OWLClass itemset = factory.getOWLClass(IRI.create(ont.getOntologyID()
					.getOntologyIRI().toString() + "#Itemset"));
			// Add individual
			//OWLIndividual itemsetIndividual = factory.getOWLNamedIndividual(IRI.create(ont.getOntologyID()
			//		.getOntologyIRI().toString() + "#itemSetA"));
			
			OWLIndividual itemsetIndividual = factory.getOWLNamedIndividual(":"+restrictionSequence.getSequenceName(),
					ontologyHolder.getPrefixOWLOntologyFormat());

			OWLClassAssertionAxiom classAssertionBx = factory.getOWLClassAssertionAxiom(
					itemset, itemsetIndividual);

			manager.addAxiom(ont, classAssertionBx);


			/*OWLObjectProperty hasFirstItem = factory.getOWLObjectProperty(IRI.create(ont.getOntologyID()
				.getOntologyIRI().toString()
				+ "#hasFirstItem"));*/

			OWLObjectProperty hasFirstItem = factory.getOWLObjectProperty(":hasFirstItem",ontologyHolder.getPrefixOWLOntologyFormat());

			OWLObjectPropertyAssertionAxiom axiom1 = factory
					.getOWLObjectPropertyAssertionAxiom(hasFirstItem, itemsetIndividual, itemIndividual);

			AddAxiom addAxiom1 = new AddAxiom(ont, axiom1);
			// Now we apply the change using the manager.
			manager.applyChange(addAxiom1);

			//ontologyHolder.printProperties(item);

			//ontologyHolder.listIndividualsFromClass(itemset);
			//ontologyHolder.listIndividualsFromClass(item);

			ontologyHolder.processReasoner();
			
			try {
				manager.saveOntology(ont);
				restrictionSequence.insertItem(itemValue);
				restrictionSequence.insertRoot();
			} catch (OWLOntologyStorageException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	
}
