package logicprocess;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.util.InferredAxiomGenerator;
import org.semanticweb.owlapi.util.InferredClassAssertionAxiomGenerator;
import org.semanticweb.owlapi.util.InferredOntologyGenerator;
import org.semanticweb.owlapi.util.InferredPropertyAssertionGenerator;
import org.semanticweb.owlapi.util.InferredSubClassAxiomGenerator;
import org.semanticweb.owlapi.vocab.PrefixOWLOntologyFormat;

import uk.ac.manchester.cs.owlapi.dlsyntax.DLSyntaxObjectRenderer;

public class OntologyHolder {
	OWLOntologyManager owlManager;
	OWLOntology ontology;
	OWLDataFactory factory;
	OWLReasoner reasoner;
	
	PrefixOWLOntologyFormat pm;
	
	public OntologyHolder() {}

	public OWLOntologyManager getOWLOntologyManager() {
		return owlManager;
	}

	public void setOWLOntologyManager(OWLOntologyManager manager) {
		owlManager = manager;
	}

	public OWLOntology getOWLOntology() {
		return ontology;
	}

	public void setOWLOntology(OWLOntology ontology) {
		this.ontology = ontology;
	}

	public OWLDataFactory getOWLDataFactory() {
		return factory;
	}

	public void setOWLDataFactory(OWLDataFactory factory) {
		this.factory = factory;
	}
	
	public OWLReasoner getOWLReasoner() {
		return reasoner;
	}

	public void setOWLReasoner(OWLReasoner reasoner) {
		this.reasoner = reasoner;
	}
	
	public PrefixOWLOntologyFormat getPrefixOWLOntologyFormat() {
		return pm;
	}

	public void setPrefixOWLOntologyFormat(String prefix) {
		pm = (PrefixOWLOntologyFormat) owlManager.getOntologyFormat(ontology);
		pm.setDefaultPrefix(prefix + "#");
	}

	/** Prints the IRI of an ontology and its document IRI.
	 *
	 * @param manager
	 * The manager that manages the ontology
	 * @param ontology
	 * The ontology */
	public void printOntology() {
		IRI ontologyIRI = ontology.getOntologyID().getOntologyIRI();
		IRI documentIRI = owlManager.getOntologyDocumentIRI(ontology);
		System.out.println(ontologyIRI == null ? "anonymous" : ontologyIRI
				.toQuotedString());
		System.out.println(" from " + documentIRI.toQuotedString());
	}
	
	/** Prints out the properties that instances of a class expression must have.
	 *
	 * @param man
	 * The manager
	 * @param ont
	 * The ontology
	 * @param reasoner
	 * The reasoner
	 * @param cls
	 * The class expression */
	public void printProperties(OWLClass cls) {
		if (!ontology.containsClassInSignature(cls.getIRI())) {
			throw new RuntimeException("Class not in signature of the ontology");
		}
		// Note that the following code could be optimised... if we find that
		// instances of the specified class do not have a property, then we
		// don't need to check the sub properties of this property
		System.out.println("Properties of " + cls);
		for (OWLObjectPropertyExpression prop : ontology.getObjectPropertiesInSignature()) {
			boolean sat = hasProperty(owlManager, reasoner, cls, prop);
			if (sat) {
				System.out.println("Instances of " + cls
						+ " necessarily have the property " + prop);
			}
		}
	}
	
	private boolean hasProperty(OWLOntologyManager man, OWLReasoner reasoner,
			OWLClass cls, OWLObjectPropertyExpression prop) {
		// To test whether the instances of a class must have a property we
		// create a some values from restriction and then ask for the
		// satisfiability of the class intersected with the complement of this
		// some values from restriction. If the intersection is satisfiable then
		// the instances of the class don't have to have the property,
		// otherwise, they do.
		OWLDataFactory dataFactory = man.getOWLDataFactory();
		OWLClassExpression restriction = dataFactory.getOWLObjectSomeValuesFrom(prop,
				dataFactory.getOWLThing());
		// Now we see if the intersection of the class and the complement of
		// this restriction is satisfiable
		OWLClassExpression complement = dataFactory.getOWLObjectComplementOf(restriction);
		OWLClassExpression intersection = dataFactory.getOWLObjectIntersectionOf(cls,
				complement);
		return !reasoner.isSatisfiable(intersection);
	}
	
	public void listIndividualsFromClass(OWLClass cls){
		Set<OWLIndividual> individuals = cls.getIndividuals(getOWLOntology());
		System.out.println("The individuals from class " + cls + " are:");
		for (OWLIndividual owlIndividual : individuals) {
			System.out.println(owlIndividual.toString());
		}
	}
	
	public void individual_listClasses (OWLNamedIndividual individual) {
		//find to which classes the individual belongs 
		DLSyntaxObjectRenderer renderer = new DLSyntaxObjectRenderer(); 
        Set<OWLClassExpression> assertedClasses = individual.getTypes(ontology); 
        for (OWLClass c : reasoner.getTypes(individual, false).getFlattened()) { 
            boolean asserted = assertedClasses.contains(c); 
            System.out.println((asserted ? "asserted" : "inferred") + " class for individual: " + renderer.render(c)); 
        } 
	}
	
	public void processReasoner() {
		
		List<InferredAxiomGenerator<? extends OWLAxiom>> generators=new ArrayList<InferredAxiomGenerator<? extends OWLAxiom>>();
		generators.add(new InferredSubClassAxiomGenerator()); //infere classes
		generators.add(new InferredClassAssertionAxiomGenerator());
		//generators.add(new InferredInverseObjectPropertiesAxiomGenerator());
		//generators.add(new InferredObjectPropertyCharacteristicAxiomGenerator());
		//generators.add(new InferredEquivalentObjectPropertyAxiomGenerator());
		//generators.add(new InferredSubObjectPropertyAxiomGenerator());
		generators.add(new InferredPropertyAssertionGenerator()); //infere propriedades

		InferredOntologyGenerator ioghermit  =new InferredOntologyGenerator(reasoner,generators);
		ioghermit.fillOntology(owlManager, ontology);
		
	}
}
