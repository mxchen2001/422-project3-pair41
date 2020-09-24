/* WORD LADDER Main.java
 * EE422C Project 3 submission by
 * Replace <...> with your actual data.
 * <Student1 Name>
 * <Student1 EID>
 * <Student1 5-digit Unique No.>
 * <Student2 Name>
 * <Student2 EID>
 * <Student2 5-digit Unique No.>
 * Slip days used: <0>
 * Git URL:
 * Fall 2020
 */


package assignment3;
import java.util.*;
import java.io.*;

public class Main {
	
	// static variables and constants only here.
	private static final boolean DEBUG = true;
	// private static LinkedList<String> adj[]; 
	private static Set<String> wordSet;
	/***** For BFS and DFS *****/
	private static Queue<LinkedList<String>> adjList; //Adjacency Lists 

	private static ArrayList<String> input;
	
	public static void main(String[] args) throws Exception {
		
		Scanner kb;	// input Scanner for commands
		PrintStream ps;	// output file, for student testing and grading only
		// If arguments are specified, read/write from/to files instead of Std IO.
		if (args.length != 0) {
			kb = new Scanner(new File(args[0]));
			ps = new PrintStream(new File(args[1]));
			System.setOut(ps);			// redirect output to ps
		} else {
			kb = new Scanner(System.in);// default input from Stdin
			ps = System.out;			// default output to Stdout
		}
		initialize();

		
		// TODO methods to read in words, output ladder
	}
	
	public static void initialize() {
		// initialize your static variables or constants here.
		// We will call this method before running our JUNIT tests.  So call it 
		// only once at the start of main.
		wordSet = makeDictionary();
		adjList = new LinkedList<LinkedList<String>>();
		if (DEBUG) System.out.println(wordSet);
		initializeAdj();
		if (DEBUG) printAdjList();
	}
	
	/**
	 * @param keyboard Scanner connected to System.in
	 * @return ArrayList of Strings containing start word and end word. 
	 * If command is /quit, return empty ArrayList. 
	 */
	public static ArrayList<String> parse(Scanner keyboard) {
		// TO DO
		String start = keyboard.nextLine();
		String end = keyboard.nextLine();

		// Condition that the either input is "/quit"
		if (start.equals("/quit") || end.equals("/quit")) {
			return new ArrayList<String>();
		}
		ArrayList<String> input = new ArrayList<String>();
		input.add(start);
		input.add(end);

		return input;
	}

	public static ArrayList<String> getWordLadderDFS(String start, String end) {
		
		// Returned list should be ordered start to end.  Include start and end.
		// If ladder is empty, return list with just start and end.
		// TODO some code
		


		return null; // replace this line later with real return
	}
	
    public static ArrayList<String> getWordLadderBFS(String start, String end) {
		
		// TODO some code
		
		return null; // replace this line later with real return
	}
    
	// TODO
	// Other private static methods here
	/********************* For Debugging *********************/
	/*														 */
	public static void printLadder(ArrayList<String> ladder) {
		for (int i = 0; i < ladder.size(); i++) {
			System.out.println(ladder.get(i));
		}
	}

	public static void printAdjList() {
		int index = 0;
		if(adjList.isEmpty()) {
			System.out.println("The BFS Queue is empty");
			return;
		}
		for (LinkedList<String> list: adjList) {
			System.out.print(index++);
			for(String word: list) {
				System.out.printf(" - %s", word);
			}
			System.out.println();
		}
	}
	/*														 */
	/*********************************************************/


	/**(**************** Initialize BFS/DFS ******************/
	/*									   					 */
	public static void initializeAdj() {
		for (String element: wordSet) {
			LinkedList<String> currentWordList = initializeAdjHelper(element);
			adjList.add(currentWordList);
		}

	}

	public static LinkedList<String> initializeAdjHelper (String word) {
		LinkedList<String> currentlyAdj = new LinkedList<String>();
		currentlyAdj.add(word);
		for(String element: wordSet) {
			if (oneLetterDiff(word, element, word.length())) {
				currentlyAdj.add(element);
			}
		}
		return currentlyAdj;
	}

	public static boolean oneLetterDiff(String original, String compared, int size) {
		int counter = 0;
		for (int i = 0; i < size; i++) {
			if(original.charAt(i) != compared.charAt(i)) {
				counter++;
			}
		}
		if(counter == 1) return true;
		return false;
	}

	/*														 */
	/*********************************************************/



	/* Do not modify makeDictionary */
	public static Set<String>  makeDictionary () {
		Set<String> words = new HashSet<String>();
		Scanner infile = null;
		try {
			/* *** Modified for DEBUGGING purposes *** 
			 * *** MAKE SURE TO REVERT CHANGES ***
			 */
			String fileName = "five_letter_words.txt";
			if (DEBUG) fileName = "short_dict.txt"; // this line is custom
			infile = new Scanner (new File(fileName));
		} catch (FileNotFoundException e) {
			System.out.println("Dictionary File not Found!");
			e.printStackTrace();
			System.exit(1);
		}
		while (infile.hasNext()) {
			words.add(infile.next().toUpperCase());
		}
		return words;
	}
}
