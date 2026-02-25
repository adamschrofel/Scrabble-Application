package ca.adamschrofel.scrabble.solver;

import java.util.BitSet;
import java.util.List;

import ca.adamschrofel.scrabble.board.Board;
import ca.adamschrofel.scrabble.board.Direction;
import ca.adamschrofel.scrabble.dictionary.TrieNode;
import ca.adamschrofel.scrabble.dto.Placement;
import ca.adamschrofel.scrabble.rack.Rack;

/**
 * Trie-driven candidate generation.
 *
 * Traverses the dictionary trie while walking the board span, consuming rack
 * letters for empty squares. This avoids scanning all words of each length and
 * prunes impossible prefixes early.
 */
final class TriePlacementGenerator {

    private static final int ALPHABET_SIZE = 26;
    private static final int MAX_WORD_LENGTH = 15;

    /**
     * Generates candidate placements that pass through {@code anchor} in the given direction.
     *
     * <p>The generator explores all possible start positions that could still reach the anchor
     * (bounded by the first blocking tile or board edge), then performs a trie DFS forward.
     */
    void generateFromAnchor(
            Board board,
            TrieNode trieRoot,
            Rack rack,
            AnchorFinder.Anchor anchor,
            Direction dir,
            BitSet[][] crossChecks,
            List<Placement> out
    ) {
        int anchorRow = anchor.row();
        int anchorCol = anchor.column();

        if (dir == Direction.ACROSS) {
            int minStartCol = anchorCol;
            while (minStartCol > 0 && board.getTile(anchorRow, minStartCol - 1) == '.') {
                minStartCol--;
            }
            for (int startCol = minStartCol; startCol <= anchorCol; startCol++) {
                int maxLenByBounds = Math.min(MAX_WORD_LENGTH, Board.SIZE - startCol);
                int anchorIndex = anchorCol - startCol;
                dfsTrie(board, trieRoot, anchorRow, startCol, dir, 0, anchorIndex, maxLenByBounds,
                        new char[maxLenByBounds],
                        copyCounts(rack.getCounts()),
                        rack.getBlanks(),
                        false,
                        crossChecks,
                        out);
            }
        } else { // DOWN
            int minStartRow = anchorRow;
            while (minStartRow > 0 && board.getTile(minStartRow - 1, anchorCol) == '.') {
                minStartRow--;
            }
            for (int startRow = minStartRow; startRow <= anchorRow; startRow++) {
                int maxLenByBounds = Math.min(MAX_WORD_LENGTH, Board.SIZE - startRow);
                int anchorIndex = anchorRow - startRow;
                dfsTrie(board, trieRoot, startRow, anchorCol, dir, 0, anchorIndex, maxLenByBounds,
                        new char[maxLenByBounds],
                        copyCounts(rack.getCounts()),
                        rack.getBlanks(),
                        false,
                        crossChecks,
                        out);
            }
        }
    }

    private void dfsTrie(
            Board board,
            TrieNode node,
            int row,
            int col,
            Direction dir,
            int pos,
            int anchorIndex,
            int maxLen,
            char[] wordBuffer,
            int[] letterCounts,
            int blanks,
            boolean usedRackTile,
            BitSet[][] crossChecks,
            List<Placement> out
    ) {
        if (pos >= maxLen) {
            return;
        }
        if (row < 0 || row >= Board.SIZE || col < 0 || col >= Board.SIZE) {
            return;
        }

        int dr = dir.directionRow;
        int dc = dir.directionColumn;
        char existing = board.getTile(row, col);

        if (existing != '.') {
            TrieNode child = node.child(existing);
            if (child == null) {
                return;
            }
            wordBuffer[pos] = existing;
            maybeEmit(out, usedRackTile, child, wordBuffer, pos, anchorIndex, row, col, dr, dc, dir);
            dfsTrie(board, child, row + dr, col + dc, dir, pos + 1, anchorIndex, maxLen, wordBuffer, letterCounts, blanks,
                    usedRackTile, crossChecks, out);
            return;
        }

        TrieNode[] children = node.children();
        for (int i = 0; i < ALPHABET_SIZE; i++) {
            TrieNode child = children[i];
            if (child == null) {
                continue;
            }

            if (crossChecks != null) {
                BitSet allowedHere = crossChecks[row][col];
                if (allowedHere != null && !allowedHere.get(i)) {
                    continue;
                }
            }

            char ch = (char) ('A' + i);

            if (letterCounts[i] > 0) {
                letterCounts[i]--;
                wordBuffer[pos] = ch;

                maybeEmit(out, true, child, wordBuffer, pos, anchorIndex, row, col, dr, dc, dir);
                dfsTrie(board, child, row + dr, col + dc, dir, pos + 1, anchorIndex, maxLen, wordBuffer, letterCounts, blanks,
                        true, crossChecks, out);

                letterCounts[i]++;
            } else if (blanks > 0) {
                // Use a blank as this letter. Blank assignment/scoring is handled later by MoveEvaluator.
                wordBuffer[pos] = ch;

                maybeEmit(out, true, child, wordBuffer, pos, anchorIndex, row, col, dr, dc, dir);
                dfsTrie(board, child, row + dr, col + dc, dir, pos + 1, anchorIndex, maxLen, wordBuffer, letterCounts,
                        blanks - 1, true, crossChecks, out);
            }
        }
    }

    private void maybeEmit(
            List<Placement> out,
            boolean usedRackTile,
            TrieNode node,
            char[] wordBuffer,
            int pos,
            int anchorIndex,
            int row,
            int col,
            int dr,
            int dc,
            Direction dir
    ) {
        int length = pos + 1;
        if (!usedRackTile || length < 2 || pos < anchorIndex || !node.isWord()) {
            return;
        }
        out.add(new Placement(new String(wordBuffer, 0, length), row - dr * pos, col - dc * pos, dir));
    }

    private static int[] copyCounts(int[] counts) {
        int[] copy = new int[ALPHABET_SIZE];
        System.arraycopy(counts, 0, copy, 0, ALPHABET_SIZE);
        return copy;
    }
}
