package ca.adamschrofel.scrabble;

/**
 * Rack is players possible scrabble tiles
 * Parses a raw tile string and determines letter counts and blank tile count.
 * Uses array to track how many of each letter (A-Z) the player has.
 */
public class Rack {
    // Array tracking tile counts
    private final int[] counts;
    // Number of blank tiles (wildcards) in the rack
    private final int blanks;
    // Total number of tiles (letters + blanks)
    private final int totalTiles;

    /**
     * Creates a Rack with the given tile counts.
     *
     * @param counts     Array of 26 ints representing letter frequencies (A-Z)
     * @param blanks     Number of blank tiles
     * @param totalTiles Total number of tiles (sum of counts + blanks)
     */
    private Rack(int[] counts, int blanks, int totalTiles) {
        this.counts = counts;
        this.blanks = blanks;
        this.totalTiles = totalTiles;
    }

    /**
     * parses a string of tiles and creates a Rack instance.
     * Handles uppercase conversion, validates characters, and counts tiles.
     *
     * Valid tile characters:
     * - A-Z: Individual letter tiles
     * - ? or *: Blank/wildcard tiles that can represent any letter
     *
     * @param tiles A string of tile characters (e.g., "TRDIEEST" or "TRADE?*")
     * @return A new Rack object with parsed tile counts
     * @throws IllegalArgumentException If tiles is null, contains invalid
     *                                  characters, or exceeds 15 tiles
     */
    public static Rack parseRack(String tiles) {
        // Scrabble racks must have at least some tiles
        if (tiles == null) {
            throw new IllegalArgumentException("Rack cannot be empty");
        }

        // Trim whitespace from both ends and converts string to uppercase
        String rack = tiles.trim().toUpperCase();

        // Initialize array to track count of each letter A-Z (26 letters total)
        int[] counts = new int[26];

        // Initialize counter for blank/wildcard tiles
        int blanks = 0;

        // Initialize counter for total tiles (used to enforce 15-tile maximum)
        int totalTiles = 0;

        // Process each character in the tile input string
        for (int i = 0; i < rack.length(); i++) {
            char c = rack.charAt(i);

            // Check if character is a letter tile
            if (c >= 'A' && c <= 'Z') {
                // if it is increment letter & total count
                counts[c - 'A']++;
                totalTiles++;
            }
            // Check if character is a blank(? or *)
            else if (c == '?' || c == '*') {
                // Increment blank & total tile count
                blanks++;
                totalTiles++;
            }
            // Check if character is whitespace
            else if (Character.isWhitespace(c)) {
                // Skip whitespace
                continue;
            }
            // Reject any other character
            else {
                throw new IllegalArgumentException(
                        "Invalid tile entered" + c);
            }

            // Enforce Scrabble rule: maximum 15 tiles (Board is 15x15)
            if (totalTiles > 15) {
                throw new IllegalArgumentException(
                        "15 tiles max");
            }

            if(blanks> 2){
                throw new IllegalArgumentException("Only 2 blanks max");
            }
        }

        // Create and return a new immutable Rack
        return new Rack(counts, blanks, totalTiles);
    }

    /**
     * Returns the array of letter tile counts
     * 
     * @return Array of 26 integers representing tile frequencies (A-Z)
     */
    public int[] getCounts() {
        return counts;
    }

    /**
     * Returns the number of blank tiles
     */
    public int getBlanks() {
        return blanks;
    }

    /**
     * @return Total tile count (sum of all counts + blanks), range 0-15
     */
    public int getTotalTiles() {
        return totalTiles;
    }
}
