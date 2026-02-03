package ca.adamschrofel.scrabble;

import java.util.*;

import ca.adamschrofel.scrabble.Exceptions.InvalidTilesException;

/**
 * Command-line interface (CLI) for the Scrabble Word Finder application.
 * Allows users to input a rack of tiles and see valid playable words.
 * 
 * This is an alternative entry point to the REST API(mostly just for testing)
 * The main web application uses ScrabbleApplication.java instead.
 */
public class Main {
    /**
     * Main method for running the Scrabble solver from the command line.
     * 
     * Flow:
     * 1. Prompts the user to enter tiles
     * 2. Validates and normalizes the input
     * 3. Uses ScrabbleService to find playable words
     * 4. Groups results by word length
     * 5. Displays results organized by length (longest first)
     *
     * @param args Command-line arguments (not used currently)
     * @throws Exception If the word dictionary cannot be loaded
     */
    public static void main(String[] args) throws Exception {
        // Print the current working directory for debugging file path issues
        System.out.println("Working dir: " + new java.io.File(".").getAbsolutePath());

        Scanner sc = new Scanner(System.in);
        System.out.print("Enter tiles (max 15, ?/* = blank): ");

        String input = sc.nextLine();
        sc.close();

        String tiles = "";
        // Validate the input
        try {
            tiles = InputValidator.normalizeTiles(input);
        } catch (InvalidTilesException e) {
            // If validation fails
            System.out.println(e.getMessage());
        }

        // Create a service instance to solve the rack
        // Loads the dictionary into memory 
        ScrabbleService service = new ScrabbleService();
        // Solve the rack and get words grouped by length
        List<WordFinder.LengthGroup> groups = service.solve(tiles);
        // Display results organized by word length
        for (WordFinder.LengthGroup g : groups) {
            System.out.println("\n" + g.length + "-letter words:");
            // Print each word in the group (already sorted)
            for (String w : g.words) {
                int wordScore = ScrabbleScoring.scoreWord(w);
                System.out.println("  " + w + " "+ wordScore);
            }
        }
        Rack rack = Rack.parseRack(tiles);
        int[] tileCounts = rack.getCounts();
        int availableBlanks = rack.getBlanks();

        // Try a few hand-picked words (must be uppercase to match your indexing)
        List<String> testWords = List.of(
                "AIR",     // 0 blanks
                "ARIEL",   // needs E -> 1 blank
                "LAIR",    // 0 blanks
                "REALER",    // needs E -> 1 blank
                "EEL"      // needs 2 E's -> likely impossible with this rack
        );

        System.out.println("Rack: " + tiles);
        System.out.println("Available blanks: " + availableBlanks);
        System.out.println();

        for (String word : testWords) {
            boolean can = WordFinder.makesWord(word, tileCounts, availableBlanks);
            System.out.println(word + " canFormWord = " + can);

            List<boolean[]> masks = WordFinder.blanksTracker(word, tileCounts, availableBlanks);
            System.out.println("  masks: " + masks.size());

            for (boolean[] mask : masks) {
                System.out.println("   - " + WordFinder.blankVisualizer(word, mask)
                        + "  positions=" + blankPositions(mask));
            }
            System.out.println();
        }
    }

    private static List<Integer> blankPositions(boolean[] mask) {
        ArrayList<Integer> pos = new ArrayList<>();
        for (int i = 0; i < mask.length; i++) {
            if (mask[i]) pos.add(i);
        }
        return pos;
    }
}
