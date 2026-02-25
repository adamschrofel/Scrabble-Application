package ca.adamschrofel.scrabble.solver;

import java.util.BitSet;

import ca.adamschrofel.scrabble.board.Board;
import ca.adamschrofel.scrabble.board.Direction;
import ca.adamschrofel.scrabble.dictionary.TrieNode;

/**
 * Computes cross-check sets for a given main-word direction.
 *
 * For each empty square, determines which letters A-Z may be placed there so
 * that the perpendicular word (if any) is valid in the dictionary trie.
 * If the square has no perpendicular neighbors, all letters are allowed.
 */
final class CrossCheckCalculator {

    private static final int ALPHABET_SIZE = 26;

    @SuppressWarnings("null")
    BitSet[][] compute(Board board, TrieNode root, Direction mainDir) {
        BitSet[][] allowed = new BitSet[Board.SIZE][Board.SIZE];

        Direction crossDir = (mainDir == Direction.ACROSS) ? Direction.DOWN : Direction.ACROSS;
        int deltaRow = crossDir.directionRow;
        int deltaCol = crossDir.directionColumn;

        for (int r = 0; r < Board.SIZE; r++) {
            for (int c = 0; c < Board.SIZE; c++) {
                BitSet bs = new BitSet(ALPHABET_SIZE);
                allowed[r][c] = bs;

                if (board.getTile(r, c) != '.') {
                    // occupied: keep permissive to avoid null checks downstream
                    bs.set(0, ALPHABET_SIZE);
                    continue;
                }

                if (!hasPerpendicularNeighbor(board, r, c, deltaRow, deltaCol)) {
                    bs.set(0, ALPHABET_SIZE);
                    continue;
                }

                // Find start of the perpendicular word span (walk backwards).
                int sr = r;
                int sc = c;
                while (inBounds(sr - deltaRow, sc - deltaCol) && board.getTile(sr - deltaRow, sc - deltaCol) != '.') {
                    sr -= deltaRow;
                    sc -= deltaCol;
                }

                // Traverse trie along fixed prefix letters up to (r,c).
                TrieNode prefixNode = root;
                int tr = sr;
                int tc = sc;
                while (!(tr == r && tc == c)) {
                    char ch = board.getTile(tr, tc);
                    prefixNode = prefixNode.child(ch);
                    if (prefixNode == null) {
                        break;
                    }
                    tr += deltaRow;
                    tc += deltaCol;
                }
                if (prefixNode == null) {
                    // leave bs empty => no allowed letters
                    continue;
                }

                // Pre-scan suffix letters after (r,c).
                char[] suffix = new char[Board.SIZE];
                int suffixLen = 0;
                int tr2 = r + deltaRow;
                int tc2 = c + deltaCol;
                while (inBounds(tr2, tc2) && board.getTile(tr2, tc2) != '.') {
                    suffix[suffixLen++] = board.getTile(tr2, tc2);
                    tr2 += deltaRow;
                    tc2 += deltaCol;
                }

                TrieNode[] children = prefixNode.children();
                for (int i = 0; i < ALPHABET_SIZE; i++) {
                    TrieNode mid = children[i];
                    if (mid == null) {
                        continue;
                    }
                    TrieNode cur = mid;
                    boolean ok = true;
                    for (int k = 0; k < suffixLen; k++) {
                        cur = cur.child(suffix[k]);
                        if (cur == null) {
                            ok = false;
                            break;
                        }
                    }
                    if (ok && cur.isWord()) {
                        bs.set(i);
                    }
                }
            }
        }

        return allowed;
    }

    private boolean hasPerpendicularNeighbor(Board board, int row, int col, int deltaRow, int deltaCol) {
        int rPrev = row - deltaRow, cPrev = col - deltaCol;
        int rNext = row + deltaRow, cNext = col + deltaCol;
        return (inBounds(rPrev, cPrev) && board.getTile(rPrev, cPrev) != '.')
                || (inBounds(rNext, cNext) && board.getTile(rNext, cNext) != '.');
    }

    private static boolean inBounds(int r, int c) {
        return r >= 0 && r < Board.SIZE && c >= 0 && c < Board.SIZE;
    }
}
