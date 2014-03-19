package processsequences;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.ArrayList;

import logicprocess.FilesLocation;

public class ProcessSequences {

	ArrayList<ArrayList<String>> sequences;

	// PRIVATE 
	private final Path fFilePath;
	private final static Charset ENCODING = StandardCharsets.UTF_8;

	public ProcessSequences() {
		sequences = new ArrayList<ArrayList<String>>();
		fFilePath = Paths.get(FilesLocation.SEQUENCES);
	}

	public void execute() {
		try {
			processLineByLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		log("Done sequence read.");
		
		
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
