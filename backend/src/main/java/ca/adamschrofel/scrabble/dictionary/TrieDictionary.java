package ca.adamschrofel.scrabble.dictionary;

/**
 * Dictionary contract used by the board solver.
 *
 * This project assumes the dictionary is always trie-backed, so the solver
 * can perform prefix-pruned generation by walking the trie.
 */
public interface TrieDictionary extends Dictionary {

    /**
     * @return the root node of the trie.
     */
    TrieNode root();
}
