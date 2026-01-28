package ca.adamschrofel.scrabble;

import java.util.*;
import java.io.*;

/**
 * Core algorithm for finding valid Scrabble words from a rack of tiles.
 * 
 * ARCHITECTURE:
 * - Loads the entire dictionary into memory at startup
 * - Organizes words by length in an array for fast filtering
 * - Uses tile counting logic to determine which words can be formed from a given rack
 * - Handles blank tiles (wildcards) with positioning algorithms
 * 
 *  NOTE:
 * - "Real tiles" = actual letter tiles in the player's rack
 * - "Blank tiles" = wildcards (? or *) that can substitute any letter
 * - A word is "playable" if the player has all the letters (or blanks to cover shortages)
 */
public class WordFinder {
    // Array of 16 ArrayLists (index 0-15), each containing words of that length
    // Index 0-1 are unused (Scrabble min is 2 letters), indices 2-15 contain words
    private final List<String>[] wordsByLength;

    /**
     * Constructor initializes the word finder with dictionary file.
     *
     * @param wordListPath Path to the word list file on the classpath: dictionary/csw19Words.txt
     */
    @SuppressWarnings("unchecked")
    public WordFinder(String wordListPath) {
        // Create an array of 16 ArrayLists (0-15 for word lengths 0-15)
        this.wordsByLength = (List<String>[]) new List[16];
        // Initialize each ArrayList
        for (int i = 0; i < wordsByLength.length; i++) {
            wordsByLength[i] = new ArrayList<>();
        }
        // Load the dictionary file and populate the word lists
        loadWordList(wordListPath);
    }

    /**
     * Loads a word list file from the classpath and populates the wordsByLength array.
     * Each word is added to the list at its corresponding length index.
     * Only words between 2-15 characters are included (standard Scrabble rules).
     *
     * @param path Path to the word list file relative to classpath resources
     * @throws RuntimeException If the file cannot be found on the classpath
     */
    private void loadWordList(String path) {
        // Load the file from classpath resources
        InputStream in = WordFinder.class.getResourceAsStream("/" + path);

        // if the dictionary file isn't found
        if (in == null) {
            throw new RuntimeException(
                    "Could not find word list on classpath: " + path);
        }

        try (Scanner kb = new Scanner(in)) {
            while (kb.hasNextLine()) {
                // Read and trim each word
                String word = kb.nextLine().trim();
                // Skip empty lines
                if (word.isEmpty())
                    continue;
                // Get the word length
                int length = word.length();
                // Only include words that are valid Scrabble lengths (2-15 letters)
                if (length >= 2 && length <= 15) {
                    // Add the word to the appropriate length bucket
                    wordsByLength[length].add(word);
                }
            }
        }
    }

    /**
     * Finds all valid Scrabble words that can be formed from the given tile rack.
     * Words are returned in descending length order (longest first)
     * 
     * ALGORITHM:
     * 1. Parse the input string into a Rack object (counts letters, blanks)
     * 2. Iterate from longest possible word length down to 2
     * 3. For each length, check if each word can be played with available tiles (makesWord check)
     * 5. Sort results within each length alphabetically
     * 6. Return concatenated results (longest words first)
     *
     * @param input Raw tile string 
     * @return List of playable words in descending length order, sorted alphabetically within each length
     */
    public List<String> findPlayableWords(String input) {
        // Parse the input string into a Rack object
        Rack rack = Rack.parseRack(input);

        // Get the total number of tiles to search (max word length)
        int length = rack.getTotalTiles();
        int[] realTileCount = rack.getCounts(); // I should change the name of this
        int blanks = rack.getBlanks();

        // Initialize results list to hold all playable words
        ArrayList<String> results = new ArrayList<>();

        // Iterate from longest possible word down to 2-letter words
        for (int i = length; i >= 2; i--) {
            // Initialize a group for this word length
            ArrayList<String> group = new ArrayList<>();

            // Check each word of this length in the dictionary
            for (String word : wordsByLength[i]) {
                // Test if this word can be formed from the rack
                if (makesWord(word, realTileCount, blanks)) {
                    // If playable, add to the group
                    group.add(word);
                }
            }

            // Sort words within this length group 
            Collections.sort(group);
            // Add all sorted words to the results
            results.addAll(group);
        }
        
        // Return the complete list (longest words first, alphabetically within lengths)
        return results;
    }

    /**
     * Groups a list of words by their length.
     * Converts a flat list of words into LengthGroup objects for JSON serialization.
     * 
     * ASSUMPTION: The input list is already sorted by length (longest first).
     * This method simply collects consecutive words of the same length.
     *
     * @param playableWords A list of words (assumed sorted by descending length)
     * @return A list of LengthGroup objects, each containing words of a specific length
     */
    public List<LengthGroup> groupPlayableWords(List<String> playableWords) {
        // Initialize the result list
        ArrayList<LengthGroup> groups = new ArrayList<>();

        // Pointer to track position in the playableWords list
        int i = 0;
        // Process all words in the list
        while (i < playableWords.size()) {
            // Get the length of the current word (start of a new group)
            int length = playableWords.get(i).length();
            // Initialize a bucket for this length
            ArrayList<String> bucket = new ArrayList<>();

            // Collect all consecutive words of the same length
            while (i < playableWords.size() && playableWords.get(i).length() == length) {
                // Add word to the current bucket
                bucket.add(playableWords.get(i));
                i++;
            }
            // Create a LengthGroup for this length and add to results
            groups.add(new LengthGroup(length, bucket));
        }
        
        // Return the grouped results
        return groups;
    }

    /**
     * Determines if a word can be formed from the given rack of tiles.
     * This is the core logic: checks if the player has (or can blank) all needed letters.
     * 
     * LOGIC:
     * 1. Compute what tiles are needed for the word
     * 2. Calculate how many blanks would be required to cover shortages
     * 3. Return true if needed blanks <= available blanks
     *
     * @param word The word to check (should be uppercase)
     * @param realTileCount Array of tile counts (index 0=A, 1=B, ..., 25=Z)
     * @param blanks Number of blank/wildcard tiles available
     * @return true if the word can be formed with available tiles, false otherwise
     */
    public static boolean makesWord(String word, int[] realTileCount, int blanks) {
        // Calculate what tiles are needed for this word and how many blanks would be required
        RackUsage usage = computeRackUsage(word, realTileCount);
        // The word is playable if we have enough blanks to cover all shortages
        return usage.blanksUsed <= blanks;
    }

    /**
     * Immutable data class representing words grouped by length.
     * Used to structure the JSON response to the API.
     */
    public static class LengthGroup {
        // The word length that all words in this group share
        public final int length;
        // List of words of this length, already sorted alphabetically
        public final List<String> words;

        /**
         * Creates a LengthGroup with words of a specific length.
         *
         * @param length The length of all words in the group
         * @param words List of words of this length (should be sorted)
         */
        public LengthGroup(int length, List<String> words) {
            this.length = length;
            this.words = words;
        }
    }

    /**
     * Immutable data class that tracks what tiles are needed to form a specific word.
     * Used internally by the word-matching algorithm.
     */
    public static class RackUsage {
        // Array of how many of each letter (A-Z) the word needs
        final int[] letterNeeds;
        // Array of how many blanks are needed for each letter due to shortages
        final int[] blankRequirements;
        // Total number of blanks required to form this word
        final int blanksUsed;

        /**
         * Creates a RackUsage with tile requirement information.
         *
         * @param letterNeeds Array where index i = how many of letter (A+i) are needed
         * @param blankRequirements Array where index i = how many blanks needed for letter (A+i)
         * @param blanksUsed Total blanks needed (sum of blankRequirements)
         */
        RackUsage(int[] letterNeeds, int[] blankRequirements, int blanksUsed) {
            this.letterNeeds = letterNeeds;
            this.blankRequirements = blankRequirements;
            this.blanksUsed = blanksUsed;
        }
    }

    /**
     * Computes what tiles are needed to form a word given the player's real tiles.
     * 
     * ALGORITHM:
     * 1. Count how many of each letter the word requires
     * 2. For each letter, calculate the "shortage" (needed - available)
     * 3. Any shortage must be covered by blank tiles
     * 4. Return the requirements and total blanks needed
     *
     * EXAMPLE:
     * - Word: "TRADE"
     * - Real tiles: A=2, D=1, E=1, R=1, T=1
     * - Needed: T=1, R=1, A=1, D=1, E=1
     * - Shortages: A has 0 shortage (have 2, need 1), all others need 0
     * - Result: blanksUsed = 0 (can form without blanks)
     *
     * @param word The word to analyze (should be uppercase)
     * @param realTileCount Array of tile counts (index 0=A count, 1=B count, etc.)
     * @return A RackUsage object with detailed requirement information
     */
    private static RackUsage computeRackUsage(String word, int[] realTileCount) {
        // Create array to count how many of each letter the word needs
        int[] letterNeeds = new int[26];

        // Count letter frequencies in the word
        for (int i = 0; i < word.length(); i++) {
            char c = word.charAt(i);
            letterNeeds[c - 'A']++;
        }

        // Create array to track blank requirements per letter
        int[] blankRequirements = new int[26];
        int blanksUsed = 0;

        // For each letter, calculate if we have a shortage and need a blank
        for (int i = 0; i < word.length(); i++) {
            // Calculate the shortage: how many more of this letter we need
            int shortage = letterNeeds[i] - realTileCount[i];
            // If we have a shortage, we need blank tiles to cover it
            if (shortage > 0) {
                blankRequirements[i] = shortage;
                blanksUsed += shortage;
            }
        }
        
        // Return the computed requirements
        return new RackUsage(letterNeeds, blankRequirements, blanksUsed);
    }

    /**
     * Generates all possible placements of blank tiles within a word.
     * 
     * COMPLEXITY: This handles edge cases where multiple blank tiles are used:
     * - 0 blanks: Word can be formed without any blanks
     * - 1 blank: Multiple positions where the blank could be placed
     * - 2 blanks: Complex logic for same-letter or different-letter blanks
     * 
     * RETURNS: A list of boolean arrays, where each array represents one valid placement.
     * Each boolean array marks which positions use blank tiles (true) vs. real tiles (false).
     * 
     * EXAMPLE:
     * - Word: "TRADE" with one blank for 'R'
     * - Returns: [false, true, false, false, false] (blank at position 1)
     *
     * @param word The word to track blank placements for
     * @param realTileCount Array of available tile counts
     * @param blanks Maximum number of blanks available
     * @return List of boolean arrays, each representing one valid blank placement configuration
     */
    public static List<boolean[]> blanksTracker(String word, int[] realTileCount, int blanks) {
        // Compute what tiles are needed for this word
        RackUsage usage = computeRackUsage(word, realTileCount);
        int[] blankRequirements = usage.blankRequirements;
        int blanksUsed = usage.blanksUsed;

        // If the word needs more blanks than available (or more than 2), it's impossible
        if (blanksUsed > blanks || blanksUsed > 2) {
            return List.of(); // Return empty list (no valid configurations)
        }

        // If no blanks needed, the word can be formed with all real tiles
        if (blanksUsed == 0) {
            return List.of(new boolean[word.length()]); // Return array of all false
        }

        // Initialize list to store all valid blank placement configurations
        ArrayList<boolean[]> blankMarkers = new ArrayList<>();
        boolean[] blankNeeded = new boolean[word.length()];

        // CASE 1: Exactly 1 blank is needed
        if (blanksUsed == 1) {
            // Find the position where the blank tile should go
            for (int i = 0; i < word.length(); i++) {
                int j = word.charAt(i) - 'A'; // Convert character to letter index
                // If this letter has a blank requirement, this is a valid blank position
                if (blankRequirements[j] > 0) {
                    blankNeeded[i] = true; // Mark this position as needing a blank
                    blankMarkers.add(blankNeeded); // Add this configuration to results
                }
            }
            return blankMarkers;
        }

        // CASE 2: Exactly 2 blanks are needed
        // Find which letters need blanks (up to 2 different letters)
        int firstLetter = -1;
        int secondLetter = -1;

        for (int i = 0; i < 26; i++) {
            // Skip letters that don't need blanks
            if (blankRequirements[i] == 0) {
                continue;
            }
            // Track first and second letters needing blanks
            if (firstLetter == -1) {
                firstLetter = i;
            } else {
                secondLetter = i;
            }
        }

        // CASE 2A: Both blanks are for the SAME letter (e.g., "LETTER" needs 2 blanks for 'E')
        if (secondLetter == -1) {
            // Convert letter index back to character
            char c = (char) ('A' + firstLetter);

            // Find all positions of this letter in the word
            ArrayList<Integer> positions = new ArrayList<>();
            for (int i = 0; i < word.length(); i++) {
                if (word.charAt(i) == c) {
                    positions.add(i);
                }
            }
            
            // Generate all pairs of positions (all ways to place 2 blanks for the same letter)
            for (int i = 0; i < positions.size(); i++) {
                for (int j = i + 1; j < positions.size(); j++) {
                    blankNeeded = new boolean[word.length()];
                    blankNeeded[positions.get(i)] = true; // First blank position
                    blankNeeded[positions.get(j)] = true; // Second blank position (note: original code has a bug here)
                    blankMarkers.add(blankNeeded);
                }
            }
            return blankMarkers;
        }

        // CASE 2B: Blanks are for TWO DIFFERENT letters (e.g., one blank for 'R', one for 'Q')
        // Convert letter indices back to characters
        char l1 = (char) ('A' + firstLetter);
        char l2 = (char) ('A' + secondLetter);

        // Find all positions of each letter in the word
        ArrayList<Integer> positions1 = new ArrayList<>();
        ArrayList<Integer> positions2 = new ArrayList<>();

        for (int i = 0; i < word.length(); i++) {
            char c = word.charAt(i);
            if (c == l1) {
                positions1.add(i);
            } else if (c == l2) {
                positions2.add(i);
            }
        }

        // Generate all combinations of positions (one from each letter)
        for (int i : positions1){
            for(int j : positions2){
                blankNeeded = new boolean[word.length()];
                blankNeeded[i] = true;  // Blank for first letter
                blankNeeded[j] = true;  // Blank for second letter
                blankMarkers.add(blankNeeded);
            }
        }
        
        return blankMarkers;
    }

    /**
     * Creates a visual representation of a word with blank tile placements.
     * Blank tiles (that use wildcards) are shown as underscores, real tiles as letters.
     * 
     * EXAMPLE:
     * - Word: "TRADE"
     * - blankNeeded: [false, true, false, false, false] (blank at position 1)
     * - Result: "T_ADE" (underscore shows where the blank is)
     * 
     * This is useful for UI display to show the user which letters are being played as blanks.
     *
     * @param word The word to visualize (should be uppercase)
     * @param blankNeeded Boolean array indicating which positions use blanks
     * @return Visual string with '_' for blanks and letters for real tiles
     */
    public static String blankVisualizer(String word, boolean[] blankNeeded){
        StringBuilder sb = new StringBuilder();
        // For each position in the word
        for (int i = 0; i<word.length(); i++){
            // If this position uses a blank, append underscore; otherwise append the letter
            sb.append(blankNeeded[i]? '_': word.charAt(i));
        }
        return sb.toString();
    }
}
