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

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.Timeout;
import java.util.*;

import java.io.*;

public class CustomTest {
	private boolean TEST_RANDOM = true;
	// Test config
	private final int TEST_SIZE = 50;
	private ArrayList<LinkedList<String>> adjList = new ArrayList<LinkedList<String>>(); //Adjacency Lists 
    private Set<String> wordSet = new HashSet<String>();
	private Random rand = new Random();
	private static ByteArrayOutputStream outContent;
	// JUNIT config
	private static final int SEARCH_TIMEOUT = 300; // in seconds
	private InputStream stdIn = System.in;
    private PrintStream stdOut = System.out;

	
    // helper methods
    private Set<String>  makeDictionary () {
		Set<String> words = new HashSet<String>();
		Scanner infile = null;
		try {
			infile = new Scanner (new File("five_letter_words.txt"));
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
    
	private void initializeAdj() {
		for (String element: wordSet) {
			LinkedList<String> currentWordList = initializeAdjHelper(element);
			adjList.add(currentWordList);
		}

	}

	private LinkedList<String> initializeAdjHelper (String word) {
		LinkedList<String> currentlyAdj = new LinkedList<String>();
		currentlyAdj.add(word);
		for(String element: wordSet) {
			if (oneLetterDiff(word, element, word.length())) {
				currentlyAdj.add(element);
			}
		}
		return currentlyAdj;
	}

	private boolean oneLetterDiff(String original, String compared, int size) {
		int counter = 0;
		for (int i = 0; i < size; i++) {
			if(original.charAt(i) != compared.charAt(i)) {
				counter++;
			}
		}
		if(counter == 1) return true;
		return false;
	}

	private boolean oneLetterDiffLadder(ArrayList<String> ladder) {
		boolean flag = true;
		if (ladder.size() == 2) {
			return true;
		}
		for (int i = 0; i < ladder.size() - 1; i++) {
			System.out.println(ladder.get(i) + " and "+  ladder.get(i + 1));
			flag = oneLetterDiff(ladder.get(i), ladder.get(i + 1), 5);
		}
		return flag;
	}

	/*** JUNIT TEST CASES ***/
	@Rule // Comment this rule out when debugging to remove timeouts
	public Timeout globalTimeout = new Timeout(SEARCH_TIMEOUT * 1000);
	
	@Before
	public void setUp() throws Exception {
		wordSet = makeDictionary();
		initializeAdj();
		Main.initialize();
		outContent = new ByteArrayOutputStream();
		System.setOut(new PrintStream(outContent));
	}

	@After
    public void teardown() {
        System.setOut(stdOut);
        System.setIn(stdIn);
    }

	
	@Test
	public void similarWord1DFS() {
		String word1 = "great";
		String word2 = "gloat";
		ArrayList<String> DFS = Main.getWordLadderDFS(word1, word2);
		HashSet<String> set = new HashSet<String>(DFS);
		
		assertEquals(set.size(), DFS.size());
		
        outContent.reset();
		Main.printLadder(DFS);
        // String str = outContent.toString().replace("\n", "").replace(".", "").trim();
		// assertEquals("a 1-rung word ladder exists between great and wouldworldwould", str);
	}

	@Test
	public void similarWord1BFS() {
		String word1 = "great";
		String word2 = "gloat";
		ArrayList<String> BFS = Main.getWordLadderBFS(word1, word2);
		HashSet<String> set = new HashSet<String>(BFS);

		assertEquals(set.size(), BFS.size());
		assertEquals(BFS.size(), 3);
		outContent.reset();
		Main.printLadder(BFS);
		// String str = outContent.toString().replace("\n", "").replace(".", "").trim();
		// assertEquals("a 0-rung word ladder exists between world and wouldworldwould", str);
	}
	
	@Test
	public void singleToCommonDFS() {
		String word1 = "sperm";
		String word2 = "cares";
		ArrayList<String> DFS = Main.getWordLadderDFS(word1, word2);
		HashSet<String> set = new HashSet<String>(DFS);

		assertEquals(set.size(), DFS.size());

        outContent.reset();
		Main.printLadder(DFS);
        String str = outContent.toString().replace("\n", "").replace(".", "").trim();
		assertEquals("no word ladder can be found between sperm and cares", str);
	}
	
	@Test
	public void singleToCommonBFS() {
		String word1 = "sperm";
		String word2 = "cares";
		ArrayList<String> BFS = Main.getWordLadderBFS(word1, word2);
		HashSet<String> set = new HashSet<String>(BFS);

		assertEquals(set.size(), BFS.size());

		outContent.reset();
		Main.printLadder(BFS);
		String str = outContent.toString().replace("\n", "").replace(".", "").trim();
		assertEquals("no word ladder can be found between sperm and cares", str);
	}

	@Test
	public void commonToSingleDFS() {
		String word1 = "sperm";
		String word2 = "cares";
		ArrayList<String> DFS = Main.getWordLadderDFS(word1, word2);
		HashSet<String> set = new HashSet<String>(DFS);

		assertEquals(set.size(), DFS.size());

        outContent.reset();
		Main.printLadder(DFS);
        String str = outContent.toString().replace("\n", "").replace(".", "").trim();
		assertEquals("no word ladder can be found between sperm and cares", str);
	}

	@Test
	public void commonToSingleBFS() {
		String word1 = "sperm";
		String word2 = "cares";
		ArrayList<String> BFS = Main.getWordLadderBFS(word1, word2);
		HashSet<String> set = new HashSet<String>(BFS);

		assertEquals(set.size(), BFS.size());

        outContent.reset();
		Main.printLadder(BFS);
        String str = outContent.toString().replace("\n", "").replace(".", "").trim();
		assertEquals("no word ladder can be found between sperm and cares", str);
	}

	@Test
	public void commonToCommonDFS() {
		String word1 = "dines";
		String word2 = "cares";
		ArrayList<String> DFS = Main.getWordLadderDFS(word1, word2);
		HashSet<String> set = new HashSet<String>(DFS);

		assertEquals(set.size(), DFS.size());
		assert(oneLetterDiffLadder(DFS));

		outContent.reset();
		Main.printLadder(DFS);
		// String str = outContent.toString().replace("\n", "").replace(".", "").trim();
		// assertEquals("a 2-rung word ladder exists between dines and caresdinescinescanescares", str);
	}
	
	@Test
	public void commonToCommonBFS() {
		String word1 = "dines";
		String word2 = "cares";
		ArrayList<String> BFS = Main.getWordLadderBFS(word1, word2);
		HashSet<String> set = new HashSet<String>(BFS);

		assertEquals(set.size(), BFS.size());
		assert(oneLetterDiffLadder(BFS));
		assertEquals(BFS.size(), 4);

        outContent.reset();
		Main.printLadder(BFS);
        // String str = outContent.toString().replace("\n", "").replace(".", "").trim();
		// assertEquals("a 2-rung word ladder exists between dines and caresdinescinescirescares", str);
	}

	
	@Test
	public void rareToRareDFS() {
		String word1 = "gadje";
		String word2 = "oorie";
		ArrayList<String> DFS = Main.getWordLadderDFS(word1, word2);
		HashSet<String> set = new HashSet<String>(DFS);
		
		assertEquals(set.size(), DFS.size());
		assert(oneLetterDiffLadder(DFS));
		
		outContent.reset();
		Main.printLadder(DFS);
		String str = outContent.toString().replace("\n", "").replace(".", "").trim();
		assertEquals("no word ladder can be found between gadje and oorie", str);
	}
	
	@Test
	public void rareToRareBFS() {
		String word1 = "gadje";
		String word2 = "oorie";
		ArrayList<String> BFS = Main.getWordLadderBFS(word1, word2);
		HashSet<String> set = new HashSet<String>(BFS);
		
		assertEquals(set.size(), BFS.size());
		assert(oneLetterDiffLadder(BFS));
		
        outContent.reset();
		Main.printLadder(BFS);
        String str = outContent.toString().replace("\n", "").replace(".", "").trim();
		assertEquals("no word ladder can be found between gadje and oorie", str);
	}
	
	@Test
	public void stackOverflowDFS1() {
		String word1 = "oorie";
		String word2 = "gadje";
		ArrayList<String> DFS = Main.getWordLadderDFS(word1, word2);
		HashSet<String> set = new HashSet<String>(DFS);

		assertEquals(set.size(), DFS.size());
		assert(oneLetterDiffLadder(DFS));

		outContent.reset();
		Main.printLadder(DFS);
		String str = outContent.toString().replace("\n", "").replace(".", "").trim();
		assertEquals("no word ladder can be found between oorie and gadje", str);
	}
	
	@Test
	public void stackOverflowDFS2() {
		String word1 = "stirp";
		String word2 = "idles";
		ArrayList<String> DFS = Main.getWordLadderDFS(word1, word2);
		HashSet<String> set = new HashSet<String>(DFS);

		assertEquals(set.size(), DFS.size());
		assert(oneLetterDiffLadder(DFS));

		outContent.reset();
		Main.printLadder(DFS);
		// String str = outContent.toString().replace("\n", "").replace(".", "").trim();
		// assertEquals("no word ladder can be found between stirp and idles", str);
	}
	
	@Test
	public void stackOverflowDFS3() {
		String word1 = "barks";
		String word2 = "unban";
		ArrayList<String> DFS = Main.getWordLadderDFS(word1, word2);
		HashSet<String> set = new HashSet<String>(DFS);

		assertEquals(set.size(), DFS.size());
		assert(oneLetterDiffLadder(DFS));

		outContent.reset();
		Main.printLadder(DFS);
		String str = outContent.toString().replace("\n", "").replace(".", "").trim();
		assertEquals("no word ladder can be found between barks and unban", str);
	}
	
	@Test
	public void stackOverflowDFS4() {
		String word1 = "deash";
		String word2 = "envoy";
		ArrayList<String> DFS = Main.getWordLadderDFS(word1, word2);
		HashSet<String> set = new HashSet<String>(DFS);

		assertEquals(set.size(), DFS.size());
		assert(oneLetterDiffLadder(DFS));

		outContent.reset();
		Main.printLadder(DFS);
		String str = outContent.toString().replace("\n", "").replace(".", "").trim();
		assertEquals("no word ladder can be found between deash and envoy", str);
	}
	
	@Test
	public void stackOverflowDFS5() {
		String word1 = "tuber";
		String word2 = "cocci";
		ArrayList<String> DFS = Main.getWordLadderDFS(word1, word2);
		HashSet<String> set = new HashSet<String>(DFS);

		assertEquals(set.size(), DFS.size());
		assert(oneLetterDiffLadder(DFS));

		outContent.reset();
		Main.printLadder(DFS);
		String str = outContent.toString().replace("\n", "").replace(".", "").trim();
		assertEquals("no word ladder can be found between tuber and cocci", str);
	}

	@Test
	public void stackOverflowDFS6() {
		String word1 = "nears";
		String word2 = "niton";
		ArrayList<String> DFS = Main.getWordLadderDFS(word1, word2);
		HashSet<String> set = new HashSet<String>(DFS);

		assertEquals(set.size(), DFS.size());
		assert(oneLetterDiffLadder(DFS));

		outContent.reset();
		Main.printLadder(DFS);
		String str = outContent.toString().replace("\n", "").replace(".", "").trim();
		assertEquals("no word ladder can be found between nears and niton", str);
	}

	@Test
	public void parseQuit() throws IOException {
		String input = "/quit";
		Scanner keyboadScanner = new Scanner(input);
		ArrayList<String> empty = Main.parse(keyboadScanner);
		assertEquals(empty.isEmpty(), true);
	}

	@Test
	public void parseFull() throws IOException {	
		String input = "nears niton";
		Scanner keyboadScanner = new Scanner(input);
		ArrayList<String> wordArr = Main.parse(keyboadScanner);
		ArrayList<String> correct = new ArrayList<String>();
		correct.add("nears");
		correct.add("niton");
		ArrayList<String> DFS = Main.getWordLadderDFS(wordArr.get(0), wordArr.get(1));
		HashSet<String> set = new HashSet<String>(DFS);


		assertArrayEquals(wordArr.toArray(), correct.toArray());
		assertEquals(set.size(), DFS.size());
		assert(oneLetterDiffLadder(DFS));

		outContent.reset();
		Main.printLadder(DFS);
		String str = outContent.toString().replace("\n", "").replace(".", "").trim();
		assertEquals("no word ladder can be found between nears and niton", str);
	}
	
	@Test
	public void randomTest() {
		if (!TEST_RANDOM) {
			assert(true);
			return;
		}

		ArrayList<String> testedWord = new ArrayList<String>();
		int word1Index = rand.nextInt(wordSet.size()); // In real life, the Random object should be rather more shared than this
        int word2Index = rand.nextInt(wordSet.size());
        while (word1Index == word2Index) {
            word2Index = rand.nextInt(wordSet.size());
		}
        String word1 = new String();
		String word2 = new String();
		for (int i = 0; i < TEST_SIZE; i++) {
			int index = 0;
			for(String word : wordSet) {
				if (index == word1Index) {
					word1 = word;
				}
				if (index == word2Index) {
					word2 = word;
				}
				index++;
			}
			testedWord.add(word1 + " " + word2);
			ArrayList<String> DFS;
			ArrayList<String> BFS;
			try{
				DFS = Main.getWordLadderDFS(word1, word2);
				BFS = Main.getWordLadderBFS(word1, word2);
			}
			catch(StackOverflowError e){
				System.err.println("Uh oh, Stack Overflow!");
				System.err.println("Between " + testedWord.get(testedWord.size() - 1));
				assert(false);
				return;
			}
			HashSet<String> set1 = new HashSet<String>(DFS);
			assertEquals(set1.size(), DFS.size());
			HashSet<String> set2 = new HashSet<String>(BFS);
			assertEquals(set2.size(), BFS.size());
			assert(oneLetterDiffLadder(BFS));
			word1Index = new Random().nextInt(wordSet.size());
			do {
				word2Index = new Random().nextInt(wordSet.size());
			} while (word1Index == word2Index);
		}
		System.out.println(testedWord);
	}    

}
