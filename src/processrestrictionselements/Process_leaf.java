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

public class Process_leaf extends ProcessRestrictionElements {

	public Process_leaf(OntologyHolder ontologyHolder) {
		// TODO Auto-generated constructor stub
		super.ontologyHolder = ontologyHolder;
	}

	@Override
	public void proceed(Node node, RestrictionSequence restrictionSequence) {
		System.out.println("========> Processing Leaf restriction");
		NodeList nodeElements = node.getChildNodes();
		System.out.println("Item name to be inserted: " + nodeElements.item(0).getNodeValue());

		String itemValue = nodeElements.item(0).getNodeValue();

		if(!restrictionSequence.existsLeaf()){

			OWLOntology ont = ontologyHolder.getOWLOntology();
			OWLDataFactory factory = ontologyHolder.getOWLDataFactory();
			OWLOntologyManager manager = ontologyHolder.getOWLOntologyManager();

			/* Get some new classes. */
			OWLClass item = factory.getOWLClass(IRI.create(ont.getOntologyID()
					.getOntologyIRI().toString() + "#Leaf"));

			// Add individual
			//OWLIndividual itemIndividual = factory.getOWLNamedIndividual(IRI.create(ont.getOntologyID()
			//		.getOntologyIRI().toString() + "#itemA"));
			
			String individualName = "Leaf_" + restrictionSequence.getSequenceName();

			// Add individual
			OWLIndividual relationIndividual = factory.getOWLNamedIndividual(":"+individualName, ontologyHolder.getPrefixOWLOntologyFormat());

			//Create an individual of Root
			OWLClassAssertionAxiom classAssertionAx = factory.getOWLClassAssertionAxiom(
					item, relationIndividual);

			manager.addAxiom(ont, classAssertionAx);
			
			OWLIndividual itemIndividual = factory.getOWLNamedIndividual(":"+itemValue, ontologyHolder.getPrefixOWLOntologyFormat());

			OWLClass itemClass = factory.getOWLClass(IRI.create(ont.getOntologyID()
					.getOntologyIRI().toString() + "#Item"));
			
			//Create an individual of Item
			OWLClassAssertionAxiom classAssertionAx2 = factory.getOWLClassAssertionAxiom(itemClass,
					itemIndividual);
			
			manager.addAxiom(ont, classAssertionAx2);

			//Get the instance of the present restriction
			/* Get some new classes. */
			OWLClass restriction = factory.getOWLClass(IRI.create(ont.getOntologyID()
					.getOntologyIRI().toString() + "#Restriction"));
			// Add individual
			//OWLIndividual itemsetIndividual = factory.getOWLNamedIndividual(IRI.create(ont.getOntologyID()
			//		.getOntologyIRI().toString() + "#itemSetA"));
			
			OWLIndividual restrictionIndividual = factory.getOWLNamedIndividual(":"+restrictionSequence.getSequenceName(),
					ontologyHolder.getPrefixOWLOntologyFormat());

			OWLClassAssertionAxiom classAssertionBx = factory.getOWLClassAssertionAxiom(
					restriction, restrictionIndividual);

			manager.addAxiom(ont, classAssertionBx);


			/*OWLObjectProperty hasFirstItem = factory.getOWLObjectProperty(IRI.create(ont.getOntologyID()
				.getOntologyIRI().toString()
				+ "#hasFirstItem"));*/
			
			OWLObjectProperty hasRelation = factory.getOWLObjectProperty(":hasRelation", ontologyHolder.getPrefixOWLOntologyFormat());

			OWLObjectProperty hasRoot = factory.getOWLObjectProperty(":hasLeaf",ontologyHolder.getPrefixOWLOntologyFormat());
			

			OWLObjectPropertyAssertionAxiom axiom1 = factory
					.getOWLObjectPropertyAssertionAxiom(hasRelation, restrictionIndividual, relationIndividual);
			
			OWLObjectPropertyAssertionAxiom axiom2 = factory
					.getOWLObjectPropertyAssertionAxiom(hasRoot, relationIndividual, itemIndividual);

			AddAxiom addAxiom1 = new AddAxiom(ont, axiom1);
			AddAxiom addAxiom2 = new AddAxiom(ont,axiom2);
			// Now we apply the change using the manager.
			manager.applyChange(addAxiom1);
			manager.applyChange(addAxiom2);

			//ontologyHolder.printProperties(item);

			//ontologyHolder.listIndividualsFromClass(itemset);
			//ontologyHolder.listIndividualsFromClass(item);
			
			ontologyHolder.processReasoner();

			try {
				manager.saveOntology(ont);
				restrictionSequence.insertItem(itemValue);
				restrictionSequence.insertLeaf();
			} catch (OWLOntologyStorageException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
