package processsequences;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
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
import logicprocess.GlobalVariables;
import logicprocess.OntologyHolder;
import logicprocess.RestrictionSequence;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import semanticRestrictions.RestrictionSemantic;

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

		//Get relations evolving each individual restriction
		for (OWLIndividual owlIndividual : individuals) {
			RestrictionSequence restrictionSequence = new RestrictionSequence(owlIndividual.toStringID());
			System.out.println("Restriciton Name: " + owlIndividual.toStringID());
			Map<OWLObjectPropertyExpression, Set<OWLIndividual>> properties = owlIndividual.getObjectPropertyValues(ont);

			Set<Entry<OWLObjectPropertyExpression, Set<OWLIndividual>>> test = properties.entrySet();
			for (Entry<OWLObjectPropertyExpression, Set<OWLIndividual>> entry : test) {
				System.out.println("Chave: " + entry.getKey());
				if(entry.getKey().toString().contains("#")) {
					System.out.println("relation: " + entry.getKey().toString().split("#|>")[1]);
					String relation = entry.getKey().toString().split("#|>")[1];

					System.out.println("Valor: " + entry.getValue());
					String element = "";
					if(entry.getValue().toString().contains("#")) {
						System.out.println("Item: " + entry.getValue().toString().split("#|>")[1]);
						element = entry.getValue().toString().split("#|>")[1];
					}

					try {
						Constructor c;
						RestrictionSemantic restrictionSemantic = null;
						try {
							c = Class.forName("semanticRestrictions.Restriction_"+relation).getConstructor(String.class, RestrictionSequence.class);
							try {
								restrictionSemantic = (RestrictionSemantic) c.newInstance(element, restrictionSequence);
								System.out.println("The relation: " + restrictionSemantic.getRestrictionSequence().getSequenceName());
								System.out.println("The item: " + restrictionSemantic.getItem());
							} catch (IllegalArgumentException
									| InvocationTargetException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
						} catch (NoSuchMethodException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (SecurityException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						restrictionSequence.addRelation(restrictionSemantic);

					} catch (InstantiationException | IllegalAccessException
							| ClassNotFoundException e) {
						System.out.println("******EXCEPTION: The Class \"semanticRestrictions.Restriction_"+relation+"\" was not found.");
					}
				}



			}
			GlobalVariables.restrictonManager.addRestriction(restrictionSequence);
		}

		//for each input sequence return which respect some restriction
		verifySequences();


	}

	/**
	 * Check if the sequences respect the restrictions specified
	 */
	public void verifySequences(){

		boolean[] sequencesRespectRestrictions = new boolean[sequences.size()];
		for (int k = 0 ; k < sequencesRespectRestrictions.length; k++){
			sequencesRespectRestrictions[k] = true;
		}

		System.out.println("\n*********VERIFYING SEQUENCES***************");

		//for each input sequence return which respect some restriction
		for(int i = 0; i < sequences.size() ; i++) {
			//Verificar se a sequence verifica a restricao
			System.out.println("\nSequence here: " + sequences.get(i));
			ArrayList<RestrictionSequence> restrictionSet= GlobalVariables.restrictonManager.getRestrictionSet();
			//For each restriction
			for (int j = 0; j < restrictionSet.size(); j++) {
				RestrictionSequence restrictionSequence = restrictionSet.get(j);
				System.out.println("===> Handling restriction: " + restrictionSequence.getSequenceName());
				ArrayList<RestrictionSemantic> relations = restrictionSequence.getRelations();
				//For each relation in the restriction
				for (RestrictionSemantic restrictionSemantic : relations) {
					System.out.println("=======> Handling relation: " + restrictionSemantic.getRelationName());
					//if a sequence does not respect the relation => restriction is violated
					if(!restrictionSemantic.execute(sequences.get(i))){
						System.out.println("=============>Sequence " + sequences.get(i) 
								+ " doesn't respect the restriction " + restrictionSequence.getSequenceName()
								+ " in the relation " + restrictionSemantic.getRelationName() +"\n");
						sequencesRespectRestrictions[i] = false;
						break;
					}
				}
				System.out.println();
				if(sequencesRespectRestrictions[i])
					System.out.println("The Sequence " + sequences.get(i) + " respects the restriction "
							+ restrictionSequence.getSequenceName()+"\n");
				sequencesRespectRestrictions[i] = true;
			}
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
