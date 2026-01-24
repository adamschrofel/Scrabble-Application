package ca.adamschrofel.scrabble;

import java.util.*;
import java.io.*;

public class WordFinder {
    private final List<String>[] wordsByLength;

    @SuppressWarnings("unchecked")
    public WordFinder(String wordListPath) {
        this.wordsByLength = (List<String>[]) new List[16];
        for (int i = 0; i < wordsByLength.length; i++) {
            wordsByLength[i] = new ArrayList<>();
        }
        loadWordList(wordListPath);
    }
    //TODO change name 
    private void loadWordList(String path) {
        InputStream in = WordFinder.class.getResourceAsStream("/" + path);

        if (in == null) {
            throw new RuntimeException(
                    "Could not find word list on classpath: " + path);
        }

        try (Scanner kb = new Scanner(in)) {
            while (kb.hasNextLine()) {
                String line = kb.nextLine().trim();
                if (line.isEmpty())
                    continue;

                String word = line.toUpperCase();
                int length = word.length();
                if (length >= 2 && length <= 15) {
                    wordsByLength[length].add(word);
                }
            }
        }
    }

    public List<String> findPlayableWords(String input) {
        Rack rack = Rack.parseRack(input);

        int length = rack.getTotalTiles();
        int[] rackCounts = rack.getCounts(); // I should change the name of this
        int blanks = rack.getBlanks();

        ArrayList<String> results = new ArrayList<>();

        for (int len = length; len >= 2; len--) {
            ArrayList<String> group = new ArrayList<>();

            for (String word : wordsByLength[len]) {
                if (makesWord(word, rackCounts, blanks)) {
                    group.add(word);
                }
            }

            // Sort within this length so output is clean
            Collections.sort(group);
            results.addAll(group);
        }
        return results;
    }

    public List<LengthGroup> groupPlayableWords(List<String> playableWords) {
        ArrayList<LengthGroup> groups = new ArrayList<>();

        int i = 0;
        while (i < playableWords.size()) {
            int length = playableWords.get(i).length();
            ArrayList<String> bucket = new ArrayList<>();

            while (i < playableWords.size() && playableWords.get(i).length() == length) {
                bucket.add(playableWords.get(i));
                i++;
            }
            groups.add(new LengthGroup(length, bucket));
        }
        return groups;
    }

    public static boolean makesWord(String word, int[] rackCounts, int blanks) {

        int[] need = new int[26];

        for (int i = 0; i < word.length(); i++) {
            char c = word.charAt(i);
            need[c - 'A']++;
        }

        int blanksUsed = 0;
        for (int i = 0; i < 26; i++) {
            int deficit = need[i] - rackCounts[i];
            if (deficit > 0) {
                blanksUsed += deficit;
                if (blanksUsed > blanks)
                    return false;
            }
        }
        return true;
    }

    public static class LengthGroup {
        public final int length;
        public final List<String> words;

        public LengthGroup(int length, List<String> words) {
            this.length = length;
            this.words = words;
        }
    }
}
