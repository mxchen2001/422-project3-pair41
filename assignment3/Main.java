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
	private static final boolean DEBUG_SHORT = false;
	// private static LinkedList<String> adj[]; 
	private static Set<String> wordSet;
	/***** For BFS and DFS *****/
	private static ArrayList<LinkedList<String>> adjList; //Adjacency Lists 

	private static ArrayList<String> wordladder;

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
		ArrayList<String> words;
		while(true) {
			words = parse(kb);
			if(words.isEmpty()) break;
			if (DEBUG) print("Starting Word: " + words.get(0)); print("Ending Word: " + words.get(1));
			// printLadder(getWordLadderDFS(words.get(0), words.get(1)));
			printLadder(getWordLadderBFS(words.get(0), words.get(1)));
		}
			
		// TODO methods to read in words, output ladder
	}
	
	public static void initialize() {
		// initialize your static variables or constants here.
		// We will call this method before running our JUNIT tests.  So call it 
		// only once at the start of main.
		wordSet = makeDictionary();
		adjList = new ArrayList<LinkedList<String>>();
		wordladder = new ArrayList<String>();
		if (DEBUG_SHORT) System.out.println(wordSet);
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
		start = start.toUpperCase();
		end = end.toUpperCase();
		// Condition that the either input is "/quit"
		if (start.equals("/quit") || end.equals("/quit")) {
			return new ArrayList<String>();
		}
		ArrayList<String> input = new ArrayList<String>();
		input.add(start);
		input.add(end);
		if (DEBUG) print("Starting Word: " + start); print("Ending Word: " + end);

		return input;
	}

	public static void clearWordLadder() {
		wordladder = new ArrayList<String>();
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

	public static void reverse() {
		ArrayList<String> temp = new ArrayList<String>();
		for(int i = wordladder.size() - 1; i >= 0; i--) {
			temp.add(wordladder.get(i));
		}
		wordladder = temp;
	}

	public static void checkEntryPoint(int entryPoint) {
		try {
			adjList.get(entryPoint);
		} catch (Exception e) {
			throw new ArrayIndexOutOfBoundsException(entryPoint);
		}
	}

	public static boolean DFSHelper(String start, String end, Set<String> visited) {
		int entryPoint = getWordIndex(start);
		// checks if the entry point is a valid index
		visited.add(start);
		wordladder.add(start);
		if((adjList.get(entryPoint)).peek().equals(end)) return true;

		boolean result = false;
		for(int i = 1; i < (adjList.get(entryPoint)).size(); i++) {
			String nextWord = adjList.get(entryPoint).get(i);
			if (visited.contains(nextWord)) 
				continue;
			result = (result || DFSHelper(nextWord, end, visited));
		}
		if (!result && !start.equals(end)) wordladder.remove(wordladder.size() - 1);
		return result;
	}

	public static Queue<String> populateNeighbor(int index) {
		Queue<String> neighbors = new LinkedList<String>();
		for (String element : adjList.get(index))
			neighbors.add(element);
		if(neighbors.isEmpty()) return neighbors;
		neighbors.remove();
		return neighbors;
	}

	public static ArrayList<String> getNeighbors(String Node) {
		int entryPoint = getWordIndex(Node);
		ArrayList<String> result = new ArrayList<String>();
		for(int i = 0; i < adjList.get(entryPoint).size(); i++) {
			result.add(adjList.get(entryPoint).get(i));
		}
		return result;

	}

	public static boolean BFSHelper(String start, String end, Set<String> visited, Queue<String> neighbors) {
		if(neighbors.isEmpty()) return false;
		Queue<String> neighborsCopy = new LinkedList<String>();
		for (String element : neighbors)
			neighborsCopy.add(element);
		ArrayList<LinkedList<String>> wordStack = new ArrayList<LinkedList<String>>(); 

		for (String neighbor : neighborsCopy) {
			if(visited.contains(neighbor))
				continue;
			if(neighbor.equals(end)) {
				wordladder.add(neighbor);
				return true;
			}
			// first element of distantNeighor is the root neighbor
			ArrayList<String> distantNeighbors = getNeighbors(neighbor);
			LinkedList<String> currentNode = new LinkedList<String>();
			currentNode.add(neighbor);
			if (distantNeighbors.size() == 0)
				return false;

			visited.add(neighbor);
			for (int i = 0; i < distantNeighbors.size(); i++) {
				if(visited.contains(distantNeighbors.get(i)))
					continue;
				neighbors.add(distantNeighbors.get(i));
				currentNode.add(distantNeighbors.get(i));
			}
			neighbors.remove();
			wordStack.add(currentNode);
		}
		boolean result = (false || BFSHelper(start, end, visited, neighbors));

		if (result) {
			String top = wordladder.get(wordladder.size() - 1);
			for (int i = 0; i < wordStack.size(); i++) {
				if((wordStack.get(i)).contains(top)) {
					wordladder.add((wordStack.get(i)).getFirst());
					break;
				}
			}
		}
		return result;
	}

	public static ArrayList<String> getWordLadderDFS(String start, String end) {
		// Returned list should be ordered start to end.  Include start and end.
		// If ladder is empty, return list with just start and end.
		// TODO some code
		clearWordLadder();
		if (DFSHelper(start, end, new HashSet<String>())) return wordladder;
		return new ArrayList<String>();
	}
	
    public static ArrayList<String> getWordLadderBFS(String start, String end) {
		// TODO some code
		clearWordLadder();
		int entryPoint = getWordIndex(start);
		Set<String> visited = new HashSet<String>();
		Queue<String> neighbors = populateNeighbor(entryPoint);
		visited.add(start);
		if (BFSHelper(start, end, visited, neighbors)) {
			wordladder.add(start);
			reverse();
			return wordladder;
		}
		return new ArrayList<String>();
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
			for(int i = 0; i < list.size(); i++) {
				System.out.printf(" - %s", list.get(i));
			}
			System.out.println();
		}
	}

	public static void printStatement() {
		System.out.println("LOLXD!!!!");
	}

	public static void print(String in) {
		System.out.println(in);
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
			if (DEBUG_SHORT) fileName = "short_dict.txt"; // this line is custom
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
