package ca.adamschrofel.scrabble;

import java.util.*;

public class InputValidator {

    public static final int MAX_TILES = 15;

    public static String normalizeTiles(String input) throws InvalidTilesException {

        String cleanedInput = input.trim().replaceAll("\\s+", "").toUpperCase();

        if (cleanedInput.isEmpty()) {
            throw new InvalidTilesException("Please enter tiles (min 2)");
        }

        if (cleanedInput.length() > MAX_TILES) {
            throw new InvalidTilesException("Too many tiles! Please enter between 2 and 15 tiles.");
        }

        Set<Character> invalidCharacters = new LinkedHashSet<>();
        for (char c : cleanedInput.toCharArray()) {
            boolean isLetter = (c >= 'A' && c <= 'Z');
            boolean isBlank = (c == '*' || c == '?');

            if (!isLetter && !isBlank) {
                invalidCharacters.add(c);
            }
        }
        if (!invalidCharacters.isEmpty()) {
            throw new InvalidTilesException(
                    "Invalid entries: " + invalidCharacters + "Search only accepts A-Z, ?, or *");
        }
        return cleanedInput;
    }

}
