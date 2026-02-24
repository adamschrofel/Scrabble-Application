package ca.adamschrofel.scrabble.dictionary;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Dictionary backed by a flat word list resource (e.g., CSW19).
 * Loads all words into a HashSet for O(1) membership checks and buckets by length for fast length filtering.
 */
public final class WordListDictionary implements Dictionary {

    private final List<String>[] wordsByLength; // indices 0..15
    private final Set<String> allWords = new HashSet<>();

    @SuppressWarnings("unchecked")
    public WordListDictionary(String wordListPath) {
        this.wordsByLength = (List<String>[]) new List[16];
        for (int i = 0; i < wordsByLength.length; i++) {
            wordsByLength[i] = new ArrayList<>();
        }
        loadWordList(wordListPath);
    }

    private void loadWordList(String path) {
        InputStream in = WordListDictionary.class.getResourceAsStream("/" + path);
        if (in == null) {
            throw new RuntimeException("Dictionary file not found on classpath: " + path);
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String word = line.trim().toUpperCase();
                int len = word.length();
                if (len < 2 || len > 15) continue;
                wordsByLength[len].add(word);
                allWords.add(word);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reading dictionary file: " + path, e);
        }
    }

    @Override
    public boolean contains(String word) {
        if (word == null) return false;
        return allWords.contains(word.toUpperCase());
    }

    @Override
    public List<String> wordsOfLength(int len) {
        if (len < 0 || len >= wordsByLength.length) return List.of();
        return wordsByLength[len];
    }
}
