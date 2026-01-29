package ca.adamschrofel.scrabble;

/**
 * Provides Scrabble tile point values.
 * Stores the official Scrabble scoring system where each letter has a fixed
 * point value
 * (e.g., A=1 point, Q=10 points, blank=0 points).
 */
public final class ScrabbleScoring {
    public ScrabbleScoring() {
    }

    // Array of 26 integers storing the point value for each letter A-Z
    private static final int[] SCORE = new int[26];

    /**
     * Static initializer block that sets up the standard Scrabble tile scores.
     */
    static {
        setTileScore("AEILNORSTU", 1);
        setTileScore("DG", 2);
        setTileScore("BCMP", 3);
        setTileScore("FHVWY", 4);
        setTileScore("K", 5);
        setTileScore("JX", 8);
        setTileScore("QZ", 10);
        // Blank tiles are worth 0 points
        setTileScore("*?", 0);
    }

    /**
     * Helper method sets the score for tiles
     */
    private static void setTileScore(String letters, int value) {
        // For each letter in the input string, set its score in the array
        for (int i = 0; i < letters.length(); i++) {
            char c = letters.charAt(i);
            if (c < 'A' || c > 'Z')
                continue;
            // Convert character to array index (A=0, B=1, ..., Z=25)
            SCORE[c - 'A'] = value;
        }
    }

    /**
     * Retrieves the point value of a single tile
     *
     * @param c The tile character (A-Z, *, or ?)
     * @return The point value of the tile (0-10)
     */
    public static int getTileScore(char c) {
        return SCORE[c - 'A'];
    }

    public static int scoreLetter(char c) {
        if (c < 'A' || c > 'Z') {
            return 0;
        }
        return SCORE[c - 'A'];

    }

    /**
     * Calculates the total point value of a word by summing the individual tile
     * scores.
     *
     * @param word The word
     * @return The total point value of all tiles in the word
     */
    public static int scoreWord(String word) {
        int total = 0;
        for (char c : word.toCharArray()) {
            total += getTileScore(c);
        }
        return total;
    }
}