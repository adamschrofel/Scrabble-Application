package ca.adamschrofel.scrabble.dictionary;

import java.util.List;

public interface Dictionary {
    boolean contains(String word);
    List<String> wordsOfLength(int len);
}
