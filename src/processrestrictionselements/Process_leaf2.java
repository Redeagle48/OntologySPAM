package processrestrictionselements;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import logicprocess.OntologyHolder;
import logicprocess.RestrictionSequence;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.util.InferredAxiomGenerator;
import org.semanticweb.owlapi.util.InferredClassAssertionAxiomGenerator;
import org.semanticweb.owlapi.util.InferredOntologyGenerator;
import org.semanticweb.owlapi.util.InferredPropertyAssertionGenerator;
import org.semanticweb.owlapi.util.InferredSubClassAxiomGenerator;
import org.w3c.dom.NodeList;

import uk.ac.manchester.cs.owlapi.dlsyntax.DLSyntaxObjectRenderer;


public class Process_leaf2 extends ProcessRestrictionElements {


	public Process_leaf2(OntologyHolder ontologyHolder) {
		// TODO Auto-generated constructor stub
		super.ontologyHolder = ontologyHolder;
	}

	@Override
	public void proceed(org.w3c.dom.Node node, RestrictionSequence restrictionSequence) {
		System.out.println("========> Processing Leaf restriction");
		NodeList nodeElements = node.getChildNodes();
		String itemValue = nodeElements.item(0).getNodeValue();
		System.out.println("Item name to be inserted: " + nodeElements.item(0).getNodeValue());
		
		if(!restrictionSequence.existsLeaf()){

			OWLOntology ont = ontologyHolder.getOWLOntology();
			OWLDataFactory factory = ontologyHolder.getOWLDataFactory();
			OWLOntologyManager manager = ontologyHolder.getOWLOntologyManager();
			OWLReasoner reasoner = ontologyHolder.getOWLReasoner(); 

			//ConsoleProgressMonitor progressMonitor = new ConsoleProgressMonitor();

			//OWLReasonerConfiguration config = new SimpleConfiguration(
			//progressMonitor);

			//OWLReasoner reasoner = reasonerFactory.createReasoner(ont, config);

			//reasoner.precomputeInferences();

			OWLNamedIndividual itemSetIndividual = factory.getOWLNamedIndividual(IRI
					.create(ont.getOntologyID()
							.getOntologyIRI().toString() + "#itemSetA"));

			OWLObjectProperty hasFirstItem = factory.getOWLObjectProperty(IRI
					.create(ont.getOntologyID()
							.getOntologyIRI().toString() + "#hasFirstItem"));

			NodeSet<OWLNamedIndividual> itemSetValuesNodeSet = reasoner
					.getObjectPropertyValues(itemSetIndividual, hasFirstItem);

			Set<OWLNamedIndividual> values = itemSetValuesNodeSet.getFlattened();
			System.out.println("The hasFirstItem property values for itemSetA are: ");
			for (OWLNamedIndividual ind : values) {
				System.out.println("    " + ind);
			}

			DLSyntaxObjectRenderer renderer = new DLSyntaxObjectRenderer(); 
			/*
		//get inverse of a property, i.e. which individuals are in relation with a given individual 
		OWLNamedIndividual university = factory.getOWLNamedIndividual(":MU", pm); 
		OWLObjectPropertyExpression inverse = factory.getOWLObjectInverseOf(isEmployedAtProperty); 
		for (OWLNamedIndividual ind : reasoner.getObjectPropertyValues(university, inverse).getFlattened()) { 
			System.out.println("MU inverseOf(isEmployedAt) -> " + renderer.render(ind)); 
		} 
			 */

			OWLNamedIndividual itemIndividual = factory.getOWLNamedIndividual(IRI
					.create(ont.getOntologyID()
							.getOntologyIRI().toString() + "#itemA"));

			OWLObjectProperty isFirstItem = factory.getOWLObjectProperty(IRI
					.create(ont.getOntologyID()
							.getOntologyIRI().toString() + "#isFirstItem"));

			NodeSet<OWLNamedIndividual> itemValuesNodeSet = reasoner
					.getObjectPropertyValues(itemIndividual, isFirstItem);

			Set<OWLNamedIndividual> values2 = itemValuesNodeSet.getFlattened();
			System.out.println("The isFirstItem property values for itemA are: ");
			for (OWLNamedIndividual ind : values2) {
				System.out.println("    " + ind);
			}

			//ask reasoner whether Martin is employed at MU 
			boolean result = reasoner.isEntailed(factory.getOWLObjectPropertyAssertionAxiom(isFirstItem, itemIndividual, itemSetIndividual)); 
			System.out.println("Is itemA the first item of itemSetA ? : " + result); 



			//InferredOntologyGenerator generator = new InferredOntologyGenerator( reasoner );
			//generator.fillOntology( manager, ont );

			List<InferredAxiomGenerator<? extends OWLAxiom>> generators=new ArrayList<InferredAxiomGenerator<? extends OWLAxiom>>();
			generators.add(new InferredSubClassAxiomGenerator()); //infere classes
			generators.add(new InferredClassAssertionAxiomGenerator());
			//generators.add(new InferredInverseObjectPropertiesAxiomGenerator());
			//generators.add(new InferredObjectPropertyCharacteristicAxiomGenerator());
			//generators.add(new InferredEquivalentObjectPropertyAxiomGenerator());
			//generators.add(new InferredSubObjectPropertyAxiomGenerator());
			generators.add(new InferredPropertyAssertionGenerator()); //infere propriedades

			InferredOntologyGenerator ioghermit  =new InferredOntologyGenerator(reasoner,generators);
			ioghermit.fillOntology(manager, ont);

			try {
				manager.saveOntology(ont);
				restrictionSequence.insertLeaf();
			} catch (OWLOntologyStorageException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			//get inverse of a property, i.e. which individuals are in relation with a given individual 
			OWLNamedIndividual university = factory.getOWLNamedIndividual(":itemA", ontologyHolder.getPrefixOWLOntologyFormat()); 
			OWLObjectPropertyExpression inverse = factory.getOWLObjectInverseOf(hasFirstItem); 
			System.out.println("Inverses:");
			for (OWLNamedIndividual ind : reasoner.getObjectPropertyValues(university, inverse).getFlattened()) { 
				System.out.println("itemSetA inverseOf(hasFirstItem) -> " + renderer.render(ind)); 
			}
		}

	}

}
