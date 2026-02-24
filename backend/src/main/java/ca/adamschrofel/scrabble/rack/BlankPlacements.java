package ca.adamschrofel.scrabble.rack;

import java.util.ArrayList;
import java.util.List;

/**
 * Utilities for rack-only (non-board) word feasibility and blank placement visualization.
 *
 * This is intentionally separate from the board solver's blank logic (BlankAssigner).
 */
public final class BlankPlacements {

    private BlankPlacements() {}

    /**
     * Immutable data class that tracks what tiles are needed to form a specific word.
     */
    public static final class RackUsage {
        public final int[] letterNeeds;        // A-Z counts required by the word
        public final int[] blankRequirements;  // A-Z counts that must be covered by blanks
        public final int blanksUsed;           // sum(blankRequirements)

        RackUsage(int[] letterNeeds, int[] blankRequirements, int blanksUsed) {
            this.letterNeeds = letterNeeds;
            this.blankRequirements = blankRequirements;
            this.blanksUsed = blanksUsed;
        }
    }

    /**
     * Returns true if the word can be formed using the given real tile counts plus blanks.
     */
    public static boolean makesWord(String word, int[] realTileCount, int blanks) {
        RackUsage usage = computeRackUsage(word, realTileCount);
        return usage.blanksUsed <= blanks;
    }

    /**
     * Computes what tiles are needed to form a word given the player's real tiles.
     */
    public static RackUsage computeRackUsage(String word, int[] realTileCount) {
        int[] letterNeeds = new int[26];
        for (int i = 0; i < word.length(); i++) {
            char c = word.charAt(i);
            letterNeeds[c - 'A']++;
        }

        int[] blankRequirements = new int[26];
        int blanksUsed = 0;

        for (int i = 0; i < 26; i++) {
            int shortage = letterNeeds[i] - realTileCount[i];
            if (shortage > 0) {
                blankRequirements[i] = shortage;
                blanksUsed += shortage;
                // Fast exit: Scrabble max blanks is 2; for rack-word features you can still pass a higher
                // number, but this keeps it cheap.
                if (blanksUsed > 2) {
                    // Keep counting irrelevant; we can return early.
                    return new RackUsage(letterNeeds, blankRequirements, blanksUsed);
                }
            }
        }

        return new RackUsage(letterNeeds, blankRequirements, blanksUsed);
    }

    /**
     * Generates all possible placements of blank tiles within a word.
     * Returns a list of boolean arrays where true indicates the position uses a blank.
     */
    public static List<boolean[]> blanksTracker(String word, int[] realTileCount, int blanks) {
        RackUsage usage = computeRackUsage(word, realTileCount);
        int[] blankRequirements = usage.blankRequirements;
        int blanksUsed = usage.blanksUsed;

        if (blanksUsed > blanks || blanksUsed > 2) {
            return List.of();
        }

        if (blanksUsed == 0) {
            return List.of(new boolean[word.length()]);
        }

        ArrayList<boolean[]> blankMarkers = new ArrayList<>();

        // CASE 1: Exactly 1 blank is needed
        if (blanksUsed == 1) {
            for (int i = 0; i < word.length(); i++) {
                int j = word.charAt(i) - 'A';
                if (blankRequirements[j] > 0) {
                    boolean[] singleBlank = new boolean[word.length()];
                    singleBlank[i] = true;
                    blankMarkers.add(singleBlank);
                }
            }
            return blankMarkers;
        }

        // CASE 2: Exactly 2 blanks are needed
        int firstLetter = -1;
        int secondLetter = -1;

        for (int i = 0; i < 26; i++) {
            if (blankRequirements[i] == 0) continue;
            if (firstLetter == -1) firstLetter = i;
            else secondLetter = i;
        }

        // CASE 2A: both blanks for same letter
        if (secondLetter == -1) {
            char c = (char) ('A' + firstLetter);
            ArrayList<Integer> positions = new ArrayList<>();
            for (int i = 0; i < word.length(); i++) {
                if (word.charAt(i) == c) positions.add(i);
            }
            for (int i = 0; i < positions.size(); i++) {
                for (int j = i + 1; j < positions.size(); j++) {
                    boolean[] blankNeeded = new boolean[word.length()];
                    blankNeeded[positions.get(i)] = true;
                    blankNeeded[positions.get(j)] = true;
                    blankMarkers.add(blankNeeded);
                }
            }
            return blankMarkers;
        }

        // CASE 2B: blanks for two different letters
        char l1 = (char) ('A' + firstLetter);
        char l2 = (char) ('A' + secondLetter);

        ArrayList<Integer> positions1 = new ArrayList<>();
        ArrayList<Integer> positions2 = new ArrayList<>();

        for (int i = 0; i < word.length(); i++) {
            char c = word.charAt(i);
            if (c == l1) positions1.add(i);
            else if (c == l2) positions2.add(i);
        }

        for (int i : positions1) {
            for (int j : positions2) {
                boolean[] blankNeeded = new boolean[word.length()];
                blankNeeded[i] = true;
                blankNeeded[j] = true;
                blankMarkers.add(blankNeeded);
            }
        }

        return blankMarkers;
    }

    /**
     * Visualizes a word with '_' for blank positions.
     */
    public static String blankVisualizer(String word, boolean[] blankNeeded) {
        StringBuilder sb = new StringBuilder(word.length());
        for (int i = 0; i < word.length(); i++) {
            sb.append(blankNeeded[i] ? '_' : word.charAt(i));
        }
        return sb.toString();
    }
}
