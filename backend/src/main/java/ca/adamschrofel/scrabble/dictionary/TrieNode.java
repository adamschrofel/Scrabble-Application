package ca.adamschrofel.scrabble.dictionary;

/**
 * Simple A-Z trie node for fast prefix pruning in board move generation.
 * Uses a fixed 26-way child array for speed.
 */
public final class TrieNode {

    private static final int ALPHABET_SIZE = 26;

    /** Child pointers for 'A'..'Z'. Index 0 = 'A', 25 = 'Z'. */
/** Child pointers for 'A'..'Z'. Index 0 = 'A', 25 = 'Z'. */
/** Child pointers for 'A'..'Z'. Index 0 = 'A', 25 = 'Z'. */
/** Child pointers for 'A'..'Z'. Index 0 = 'A', 25 = 'Z'. */
    private final TrieNode[] next = new TrieNode[ALPHABET_SIZE];
    private boolean terminal;

    TrieNode childOrCreate(char c) {
        int idx = c - 'A';
        TrieNode n = next[idx];
        if (n == null) {
            n = new TrieNode();
            next[idx] = n;
        }
        return n;
    }

    public TrieNode child(char c) {
        if (c < 'A' || c > 'Z') {
            return null;
        }
        return next[c - 'A'];
    }

    /**
     * Exposes the backing children array for performance in tight solver loops.
     * Treat as read-only.
     */
    public TrieNode[] children() {
        return next;
    }

    public boolean isWord() {
        return terminal;
    }

    void setTerminal() {
        this.terminal = true;
    }
}
