package ca.adamschrofel.scrabble.dictionary;

import java.util.List;
/**
 * Dictionary abstraction used by the solver.
 *
 * <p>Implementations provide fast membership checks and optional support for listing words.
 */
public interface Dictionary {
    boolean contains(String word);

    List<String> wordsOfLength(int len);
}
