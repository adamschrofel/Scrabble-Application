package ca.adamschrofel.scrabble.solver;

import java.util.ArrayList;
import java.util.List;

import ca.adamschrofel.scrabble.board.Board;

/**
 * Finds anchor squares used for move generation.
 *
 * An anchor is an empty square adjacent (N/S/E/W) to an existing tile.
 * For an empty board, the only anchor is the center square.
 */
final class AnchorFinder {

    /**
     * An empty square adjacent (N/S/E/W) to an existing tile.
     */
    static final record Anchor(int row, int column) {
    }

    List<Anchor> findAnchors(Board board) {
        List<Anchor> anchors = new ArrayList<>();

        if (board.isEmpty()) {
            // Standard Scrabble start square.
            anchors.add(new Anchor(7, 7));
            return anchors;
        }

        for (int r = 0; r < Board.SIZE; r++) {
            for (int c = 0; c < Board.SIZE; c++) {
                if (board.getTile(r, c) != '.') {
                    continue;
                }
                if (hasNeighborTile(board, r, c)) {
                    anchors.add(new Anchor(r, c));
                }
            }
        }

        return anchors;
    }

    private boolean hasNeighborTile(Board board, int row, int col) {
        return hasTileAt(board, row - 1, col)
                || hasTileAt(board, row + 1, col)
                || hasTileAt(board, row, col - 1)
                || hasTileAt(board, row, col + 1);
    }

    private boolean hasTileAt(Board board, int row, int col) {
        if (row < 0 || row >= Board.SIZE || col < 0 || col >= Board.SIZE) {
            return false;
        }
        return board.getTile(row, col) != '.';
    }
}
