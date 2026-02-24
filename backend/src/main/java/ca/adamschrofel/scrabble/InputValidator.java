package ca.adamschrofel.scrabble;

import java.util.LinkedHashSet;
import java.util.Set;

import ca.adamschrofel.scrabble.Exceptions.InvalidTilesException;

/**
 * Utility class validates and normalizes user input
 * 
 * Handles formatting (uppercase conversion, whitespace removal) and validation
 * (checking for invalid characters, enforcing max tile limit).
 */
public class InputValidator {
    // Max number of tiles allowed in a single rack 
    public static final int MAX_TILES = 15;

    /**
     * Normalizes and validates tile input
     *
     * @param input Raw user input string 
     * @return The normalized tile string ready for word finder
     * @throws InvalidTilesException If input is empty, exceeds 15 tiles, or contains invalid characters
     */
    public static String normalizeTiles(String input) throws InvalidTilesException {

        if (input == null){
            throw new InvalidTilesException("Please enter tiles (2 min)");
        }
        // Trim whitespace and converts to uppercase
        String cleanedInput = input.trim().replaceAll("\\s+", "").toUpperCase();

        // Rejects empty input
        if (cleanedInput.length()< 2) {
            throw new InvalidTilesException("Please enter tiles (min 2)");
        }

        // Enforce maximum tiles
        if (cleanedInput.length() > MAX_TILES) {
            throw new InvalidTilesException("Too many tiles! Please enter between 2 and 15 tiles.");
        }

        // Checks for invalid characters 
        Set<Character> invalidCharacters = new LinkedHashSet<>();
        for (char c : cleanedInput.toCharArray()) {
            // Valid characters are A-Z or blank tile symbols (? and *)
            boolean isLetter = (c >= 'A' && c <= 'Z');
            boolean isBlank = (c == '*' || c == '?');

            if (!isLetter && !isBlank) {
                invalidCharacters.add(c);
            }
        }
        
        // If any invalid characters found, throw exception 
        if (!invalidCharacters.isEmpty()) {
            throw new InvalidTilesException(
                    "Invalid entries: " + invalidCharacters + "Search only accepts A-Z, ?, or *");
        }
        
        return cleanedInput;
    }
}
