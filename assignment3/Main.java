/* WORD LADDER Main.java
 * EE422C Project 3 submission by
 * Xige Michael Chen xmc75 
 * 16175
 * Jeong Woo Park jp56873 
 * 16165
 * * Slip days used: <0>
 * Git URL: https://github.com/mxchen2001/422-project3-pair41.git
 * Fall 2020
 */


package assignment3;
import java.util.*;

import java.io.*;

public class Main {
	/******* DEBUG flags *******/
	private static final boolean DEBUG = false;
	private static final boolean DEBUG_SHORT = false;
	/***** For BFS and DFS *****/
	private static ArrayList<LinkedList<String>> adjList; //Adjacency Lists 
	public static Set<String> wordSet;
	private static Set<String> visited = new HashSet<String>();

	private static Set<String> cycleWords = new HashSet<String>();
	/**** Final Word Ladder ****/
	private static ArrayList<String> wordladder;	
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
			if (DEBUG) { System.out.println("Starting Word: " + words.get(0)); System.out.println("Ending Word: " + words.get(1)); }
			printLadder(getWordLadderDFS(words.get(0), words.get(1)));
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
		initializeAdj();
		initializeCycle();
		if (DEBUG_SHORT) System.out.println(wordSet);
		if (DEBUG) printAdjList();
	}
	
	/**
	 * @param keyboard Scanner connected to System.in
	 * @return ArrayList of Strings containing start word and end word. 
	 * If command is /quit, return empty ArrayList. 
	 */
	public static ArrayList<String> parse(Scanner keyboard) {
		// TO DO
		String words = keyboard.nextLine();
		if(words.length() != 11) return new ArrayList<String>();
		String start = words.substring(0, 5);
		String end = words.substring(6, 11);
		start = start.toLowerCase();
		end = end.toLowerCase();
		// Condition that the either input is "/quit"
		if (start.toLowerCase().equals("/quit") || end.toLowerCase().equals("/quit")) {
			return new ArrayList<String>();
		}
		ArrayList<String> input = new ArrayList<String>();
		input.add(start);
		input.add(end);
		if (DEBUG) { System.out.println("Starting Word: " + start); System.out.println("Ending Word: " + end); }

		return input;
	}

	/******************* Helper Functions ********************/
	/*														 */

	// Clear the WordLadder for new output
	public static void clearWordLadder() {
		wordladder.clear();
		visited.clear();
	}

	// Get the index of 'start' inside adjacency array
	public static int getWordIndex(String start) {
		// COULD BE UPDATED WITH BINARY SEARCH FOR OPTIMIZATION

		for (int i = 0; i < adjList.size(); i++) {
			String currentHead = (adjList.get(i)).get(0);
			if (currentHead.equals(start)) {
				if (DEBUG) System.out.println(start + " is indexed at: " + i);
				return i;
			}
		}
		return -1;
	}

	// For BFS, reverse the word ladder to show top: start and bottom: end
	public static void reverse() {
		ArrayList<String> temp = new ArrayList<String>();
		for(int i = wordladder.size() - 1; i >= 0; i--) {
			temp.add(wordladder.get(i));
		}
		wordladder = temp;
	}

	// Check the entryPoint index is within bounds
	public static void checkEntryPoint(int entryPoint) {
		try {
			adjList.get(entryPoint);
		} catch (Exception e) {
			throw new ArrayIndexOutOfBoundsException(entryPoint);
		}
	}

	// Returns ArrayList of neighbors of 'Node'
	public static ArrayList<String> getNeighbors(String Node) {
		int entryPoint = getWordIndex(Node);
		ArrayList<String> result = new ArrayList<String>();
		for(int i = 0; i < adjList.get(entryPoint).size(); i++) {
			result.add(adjList.get(entryPoint).get(i));
		}
		return result;
	}

	// Helper function that initializes a Queue of inital neighors to 'start'
	public static Queue<String> populateNeighbor(int index) {
		Queue<String> neighbors = new LinkedList<String>();
		for (String element : adjList.get(index))
			neighbors.add(element);
		if(neighbors.isEmpty()) return neighbors;
		neighbors.remove();
		return neighbors;
	}

	// Recursive Algorithm of traversing the graph via Depth First Search with optimization
	public static boolean DFSHelperOpt(String start, String end) {
		int entryPoint = getWordIndex(start);
		int endPoint = getWordIndex(end);
		if ((adjList.get(endPoint)).size() == 1 && visited.isEmpty()) 
			return false;
		wordladder.add(start);
		visited.add(start);
		if ((adjList.get(entryPoint)).peek().equals(end)) 
			return true;
		boolean result = false;
		// This checks for works with similar letters compared to the start parameter
		for (int i = 0; i < start.length(); i++) {
			String optimizedWord = start.substring(0, i) + end.charAt(i);
			if(optimizedWord.length() < start.length()) optimizedWord += start.substring(i + 1, start.length());
			if (visited.contains(optimizedWord)) 
				continue;
			if ((adjList.get(entryPoint)).contains(optimizedWord)) {
				visited.add(optimizedWord);
				result = DFSHelperOpt(optimizedWord, end);
			}
			if (result)
				return true;
		}
		LinkedList<String> currentWordList = new LinkedList<String>(adjList.get(entryPoint));
		boolean firstReorder = true;
		boolean wordsWrapped = false;
		String firstReorderedWord = new String();
		int stringDiffAmt = stringDiff(start, end);
		for (int i = 1; i < currentWordList.size(); i++) {
			String nextWord = currentWordList.get(i);
			if (visited.contains(nextWord) || stringDiff(nextWord, end) > stringDiffAmt)
				continue;
			if (stringDiff(nextWord, end) > stringDiffAmt && !wordsWrapped) {
				if (nextWord.equals(firstReorderedWord)) {
					wordsWrapped = true;
				}
				if (firstReorder) {
					firstReorderedWord = nextWord;
					firstReorder = false;
				}
				currentWordList.addLast(nextWord);
			} 
			// else 
			result = DFSHelperOpt(nextWord, end);
			stringDiffAmt = stringDiff(nextWord, end);
			if (result)
				return true;
		}
		if (!result) 
			wordladder.remove(wordladder.size() - 1);
		return false;
	}

	public static int stringDiff(String word1, String word2) {
		int counter = 0;
		for (int i = 0; i < word1.length(); i++) {
			if(word1.charAt(i) != word2.charAt(i)) {
				counter++;
			}
		}
		return counter;
	}

	// Recursive Algorithm of traversing the graph via Depth First Search
	public static boolean DFSHelperRec(String start, String end) {
		int entryPoint = getWordIndex(start);
		int endPoint = getWordIndex(end);
		if ((adjList.get(endPoint)).size() == 1 && visited.isEmpty()) 
			return false;
		wordladder.add(start);
		visited.add(start);
		if ((adjList.get(entryPoint)).peek().equals(end)) 
			return true;
		boolean result = false;
		for (int i = 1; i < (adjList.get(entryPoint)).size(); i++) {
			String nextWord = adjList.get(entryPoint).get(i);
			if (visited.contains(nextWord))
				continue;
			result = DFSHelperRec(nextWord, end);
			if (result)
				return true;
		}
		if (!result) 
			wordladder.remove(wordladder.size() - 1);
		return false;
	}

	public static void addNeighbors(String Node, Queue<String> neighbors) {
		int entryPoint = getWordIndex(Node);
		for(int i = 0; i < adjList.get(entryPoint).size(); i++) {
			if(!visited.contains(adjList.get(entryPoint).get(i))) 
				neighbors.add(adjList.get(entryPoint).get(i));
		}
	}

	public static boolean BFSHelperIt(String start, String end) {
		Queue<String> neighbors = new LinkedList<String>();
		addNeighbors(start, neighbors);
		ArrayList<LinkedList<String>> wordStack = new ArrayList<LinkedList<String>>(); 
		while(!neighbors.isEmpty()) {
			String currentWord = neighbors.poll();
			if(visited.contains(currentWord))
				continue;
			visited.add(currentWord);
			LinkedList<String> currentNode = new LinkedList<String>();
			currentNode.add(currentWord);
			if(currentWord.equals(end)){
				String top = currentWord;
				wordladder.add(currentWord);
				while (!top.equals(start)) {
					for (int i = 0; i < wordStack.size(); i++) {
						if((wordStack.get(i)).contains(top)) {
							wordladder.add((wordStack.get(i)).getFirst());
							break;
						}
					}
					top = wordladder.get(wordladder.size() - 1);
				}
				return true;
			} 
			addNeighbors(currentWord, neighbors);
			addNeighbors(currentWord, currentNode);
			wordStack.add(currentNode);
		}
		return false;
	}

	// Recursive Algorithm of traversing the graph via Breadth First Search
	public static boolean BFSHelperRec(String start, String end, Queue<String> neighbors) {
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
		boolean result = (false || BFSHelperRec(start, end, neighbors));

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
	/*														 */
	/*				END OF HELPER FUNCTIONS					 */
	/*********************************************************/


	public static ArrayList<String> getWordLadderDFS(String start, String end) {
		// Returned list should be ordered start to end.  Include start and end.
		// If ladder is empty, return list with just start and end.
		clearWordLadder();
		start = start.toUpperCase();
		end = end.toUpperCase();
		// return wordladder;
		// if ((cycleWords.contains(start) || cycleWords.contains(end)) && !oneLetterDiff(start, end, 5)) {
		// 	ArrayList<String> empty = new ArrayList<String>();
		// 	empty.add(start); 
		// 	empty.add(end);
		// 	return empty;
		// } else 
		if (DFSHelperOpt(start, end)) {
		// if (DFSHelperRec(start, end)) {
		return new ArrayList<String>(wordladder);
		}
		ArrayList<String> empty = new ArrayList<String>();
		empty.add(start); 
		empty.add(end);
		return empty;
	}


	
    public static ArrayList<String> getWordLadderBFS(String start, String end) {
		clearWordLadder();
		start = start.toUpperCase();
		end = end.toUpperCase();
		if (BFSHelperIt(start, end)) {
			reverse();
			// return wordladder;
			return new ArrayList<String>(wordladder);
		}
		ArrayList<String> empty = new ArrayList<String>();
		empty.add(start); 
		empty.add(end);
		return empty;
	}
    
	// Other private static methods here
	/********************* For Debugging *********************/
	/*														 */
	public static void printLadder(ArrayList<String> ladder) {
		if(ladder.size() == 2) {
			if(!oneLetterDiff(ladder.get(0), ladder.get(1), ladder.get(0).length())) {
				System.out.println("no word ladder can be found between " + ladder.get(0).toLowerCase() + " and " + ladder.get(1).toLowerCase() + ".");
				return;
			}
		}
		String size = new String();
		if (ladder.get(0).equals(ladder.get(ladder.size() - 1))) {
			size = "0";
		} else {
			size = Integer.toString(ladder.size() - 2);
		}

		System.out.println("a " + size + "-rung word ladder exists between " + ladder.get(0).toLowerCase() + " and " + ladder.get(ladder.size() - 1).toLowerCase() + ".");
		for (int i = 0; i < ladder.size(); i++) {
			System.out.println(ladder.get(i).toLowerCase());
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
	/*														 */
	/*********************************************************/


	/**(**************** Initialize BFS/DFS ******************/
	/*									   					 */
	public static void initializeCycle() {
		for (String element: wordSet) {
			int entryPoint = getWordIndex(element);
			if (adjList.get(entryPoint).size() == 2) {
				int cycleLink = getWordIndex(adjList.get(entryPoint).get(1));
				if (adjList.get(cycleLink).size() == 2) {
					cycleWords.add(element);

				}
			}
		}
	}

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
