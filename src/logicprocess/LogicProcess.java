package logicprocess;

import java.io.File;

import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.reasoner.ConsoleProgressMonitor;
import org.semanticweb.owlapi.reasoner.OWLReasonerConfiguration;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.SimpleConfiguration;

import processsequences.ProcessSequences;
import processxml.ProcessXML;

public class LogicProcess {

	ProcessXML processxml;
	OntologyHolder ontologyHolder;
	ProcessSequences processSequences;

	public LogicProcess(){}

	public void init() {
		processxml = new ProcessXML();
		ontologyHolder = new OntologyHolder();
		processSequences = new ProcessSequences();
		
		GlobalVariables.restrictonManager = new RestrictionManager();

		// The OWLOntologyManager is at the heart of the OWL API, we can create
		// an instance of this using the OWLManager class, which will set up
		// commonly used options (such as which parsers are registered etc.
		// etc.)
		ontologyHolder.setOWLOntologyManager(OWLManager.createOWLOntologyManager());
		// Load the ontology
		try {
			ontologyHolder.setOWLOntology(ontologyHolder.getOWLOntologyManager().loadOntologyFromOntologyDocument(
					new File(FilesLocation.ONTOLOGY)));
		} catch (OWLOntologyCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ontologyHolder.setOWLDataFactory(ontologyHolder.getOWLOntologyManager().getOWLDataFactory());
		
		//OWLReasonerFactory reasonerFactory = new StructuralReasonerFactory(); //Standard reasoner
		OWLReasonerFactory reasonerFactory = new Reasoner.ReasonerFactory(); //Hermit reasoner
		//ConsoleProgressMonitor progressMonitor = new ConsoleProgressMonitor();
		//OWLReasonerConfiguration config = new SimpleConfiguration(
		//		progressMonitor);
		ontologyHolder.setOWLReasoner(reasonerFactory.createNonBufferingReasoner(ontologyHolder.getOWLOntology()/*,config*/));
		
		ontologyHolder.setPrefixOWLOntologyFormat(ontologyHolder.getOWLOntology().getOntologyID().getOntologyIRI().toString());
		System.out.println("Ontology IRI: " + ontologyHolder.getOWLOntology().getOntologyID().getOntologyIRI().toString());
	}

	public void execute() {
		init();
		processxml.execute(ontologyHolder);
		//processSequences.execute(ontologyHolder);
	}

}
