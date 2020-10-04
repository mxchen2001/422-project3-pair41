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
	private static final boolean DEBUG = true;		//debugging mode
	// private static LinkedList<String> adj[]; 
	private static Set<String> wordSet;
	/***** For BFS and DFS *****/
	private static ArrayList<LinkedList<String>> adjList; //Adjacency Lists 
	//public static LinkedList<String> adjList;
	private static ArrayList<String> visited = new ArrayList<String>();
	private static ArrayList<String> input;
	static ArrayList<String> DFS_list = new ArrayList<String>();
	
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
		
		parse(kb);	//returns input array
		getInput();
		
		
		//System.out.println(wordSet);
		//System.out.println(adjList);
			
		System.out.println();
		
		getWordLadderDFS(input.get(0),input.get(1));
		// TODO methods to read in words, output ladder
	}
	
	public static void initialize() {
		// initialize your static variables or constants here.
		// We will call this method before running our JUNIT tests.  So call it 
		// only once at the start of main.
		wordSet = makeDictionary();		//wordset is a set of strings
		//adjList = new LinkedList<String>();
		adjList = new ArrayList<LinkedList<String>>();		if (DEBUG) System.out.println(wordSet);
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
		String start;
		String end;
		String userInput[]= keyboard.nextLine().split(" ");
		start = userInput[0];
		end = userInput[1];
		
		// Condition that the either input is "/quit"
		if (start.equals("/quit") || end.equals("/quit")) {
			return new ArrayList<String>();
		}
		ArrayList<String> input = new ArrayList<String>();
		
		setInput(input);
		input.add(start);
		input.add(end);			//recieved input

		return input;
	}

	public static ArrayList<String> getWordLadderDFS(String start, String end) {
		
		// Returned list should be ordered start to end.  Include start and end.
		// If ladder is empty, return list with just start and end.
		// TODO some code
		
		DFS_list.clear();
		visited.clear();
//		if(dfs(start,end,visited).size() == 1) {
//			System.out.println("no word ladder can be found between "+start+ " and " +end+".");
//		
//		}else {
		int counter=0;
			System.out.println(dfs(start, end, visited,counter));
			System.out.println(counter);
		//}

		return null; // replace this line later with real return
	}
	public static ArrayList<String> dfs(String start, String end, ArrayList<String> visited, int counter) {
		counter++;
		start = start.toUpperCase();
		end = end.toUpperCase();
		int startIndex = getWordIndex(start);
		DFS_list.add(start);
		visited.add(start);
		
		if(start.equals(end)) {
			return DFS_list;
		}
		for(int i = 1; i < (adjList.get(startIndex)).size(); i++) {
			
			String nextWord = adjList.get(startIndex).get(i);
			if(visited.contains(nextWord)) {
				continue;
			}

			dfs(nextWord, end, visited,counter);
			if(DFS_list.get(i).equals(end)) {
				return DFS_list;
			}
		}
		
		return DFS_list;	//change to actual return statement
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

	public static LinkedList<String> initializeAdjHelper (String word) {//checks adjadency
		LinkedList<String> currentlyAdj = new LinkedList<String>();
		currentlyAdj.add(word);
		for(String element: wordSet) {
			if (oneLetterDiff(word, element, word.length())) {
				currentlyAdj.add(element);		//add if one letter different from start word
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
	public static int getWordIndex(String start) {
		// COULD BE UPDATED WITH BINARY SEARCH FOR OPTIMIZATION

		for (int i = 0; i < adjList.size(); i++) {
			String currentHead = (adjList.get(i)).get(0);
			if (currentHead.equals(start)) {
				if (DEBUG) print(start + " is indexed at: " + i);
				return i;
			}
		}
		return -1;
	}
	
	public static void print(Object in) {
		System.out.println(in);
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

	public static ArrayList<String> getInput() {
		return input;
	}

	public static void setInput(ArrayList<String> input) {
		Main.input = input;
	}
}
