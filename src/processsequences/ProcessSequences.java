package processsequences;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;

import logicprocess.FilesLocation;
import logicprocess.OntologyHolder;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

public class ProcessSequences {

	ArrayList<ArrayList<String>> sequences;

	// PRIVATE 
	private final Path fFilePath;
	private final static Charset ENCODING = StandardCharsets.UTF_8;

	public ProcessSequences() {
		sequences = new ArrayList<ArrayList<String>>();
		fFilePath = Paths.get(FilesLocation.SEQUENCES);
	}

	public void execute(OntologyHolder ontologyHolder) {
		try {
			processLineByLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		log("Done sequence read.");

		//Get individuals from class
		OWLOntology ont = ontologyHolder.getOWLOntology();
		OWLDataFactory factory = ontologyHolder.getOWLDataFactory();
		OWLOntologyManager manager = ontologyHolder.getOWLOntologyManager();

		OWLClass item = factory.getOWLClass(IRI.create(ont.getOntologyID()
				.getOntologyIRI().toString() + "#Itemset"));

		Set<OWLIndividual> individuals = item.getIndividuals(ont);

		//Get relations envolving each individual
		for (OWLIndividual owlIndividual : individuals) {
			System.out.println(owlIndividual.toStringID());
			Map<OWLObjectPropertyExpression, Set<OWLIndividual>> properties = owlIndividual.getObjectPropertyValues(ont);

			Set<Entry<OWLObjectPropertyExpression, Set<OWLIndividual>>> test = properties.entrySet();
			for (Entry<OWLObjectPropertyExpression, Set<OWLIndividual>> entry : test) {
				System.out.println("Chave: " + entry.getKey());
				if(entry.getKey().toString().contains("#")) {
					System.out.println("relation: " + entry.getKey().toString().split("#|>")[1]);
				}
				System.out.println("Valor: " + entry.getValue());
				if(entry.getValue().toString().contains("#")) {
					System.out.println("Item: " + entry.getValue().toString().split("#|>")[1]);
				}
			}
		}

		for(int i = 0; i < sequences.size() -1 ; i++) {
			//TODO
			//Para cada restricao mapeada na ontologia
			//Detetar as relacoes
			//Verificar se a sequence verifica a restricao
		}

	}

	public final void processLineByLine() throws IOException {
		try (Scanner scanner =  new Scanner(fFilePath, ENCODING.name())){
			while (scanner.hasNextLine()){
				processLine(scanner.nextLine());
			}      
		}
	}

	protected void processLine(String aLine){
		//use a second Scanner to parse the content of each line 
		Scanner scanner = new Scanner(aLine);
		scanner.useDelimiter("=");
		if (scanner.hasNext()){
			//assumes the line has a certain structure
			String name = scanner.next();
			//String value = scanner.next();
			log("Name is : " + quote(name.trim()));// + ", and Value is : " + quote(value.trim()));

			ArrayList<String> sequence = new ArrayList<String>();
			//To skip the entry and last '<' '>'
			for(int i = 1; i < name.length()-1; i++){
				sequence.add(Character.toString(name.charAt(i)));
			}
			sequences.add(sequence);
			for (String string : sequence) {
				System.out.println("Letter: " + string);
			}
		}
		else {
			log("Empty or invalid line. Unable to process.");
		}
	}

	private static void log(Object aObject){
		System.out.println(String.valueOf(aObject));
	}

	private String quote(String aText){
		String QUOTE = "'";
		return QUOTE + aText + QUOTE;
	}
}
