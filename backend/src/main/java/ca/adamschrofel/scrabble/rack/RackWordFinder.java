package ca.adamschrofel.scrabble.rack;

import ca.adamschrofel.scrabble.dictionary.Dictionary;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Rack-only word finder: given a rack (letters + up to two blanks), returns all
 * dictionary words that can be formed.
 * This does NOT consider board constraints or multipliers.
 */
public final class RackWordFinder {

    /**
     * Returns a flat list of playable words sorted by descending length, then
     * alphabetically within a length.
     */
    public List<String> findPlayableWords(String rackInput, Dictionary dict) {
        Rack rack = Rack.parseRack(rackInput);

        int maxLen = rack.getTotalTiles();
        int[] counts = rack.getCounts();
        int blanks = rack.getBlanks();

        ArrayList<String> results = new ArrayList<>();

        for (int len = maxLen; len >= 2; len--) {
            ArrayList<String> group = new ArrayList<>();
            for (String word : dict.wordsOfLength(len)) {
                if (BlankPlacements.makesWord(word, counts, blanks)) {
                    group.add(word);
                }
            }
            Collections.sort(group);
            results.addAll(group);
        }

        return results;
    }

    /**
     * Groups a sorted list of playable words (descending length) into LengthGroup
     * objects for JSON.
     */
    public List<LengthGroup> groupPlayableWords(List<String> playableWords) {
        ArrayList<LengthGroup> groups = new ArrayList<>();

        int i = 0;
        while (i < playableWords.size()) {
            int length = playableWords.get(i).length();
            ArrayList<String> words = new ArrayList<>();

            while (i < playableWords.size() && playableWords.get(i).length() == length) {
                words.add(playableWords.get(i));
                i++;
            }

            groups.add(new LengthGroup(length, words));
        }

        return groups;
    }
}
