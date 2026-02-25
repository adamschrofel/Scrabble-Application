package ca.adamschrofel.scrabble.solver;

import java.util.ArrayList;
import java.util.List;

import ca.adamschrofel.scrabble.board.Board;
import ca.adamschrofel.scrabble.board.BoardLayout;
import ca.adamschrofel.scrabble.board.Direction;
import ca.adamschrofel.scrabble.dictionary.Dictionary;
import ca.adamschrofel.scrabble.dto.MoveEvaluation;
import ca.adamschrofel.scrabble.dto.PlacedTile;
import ca.adamschrofel.scrabble.dto.Placement;
import ca.adamschrofel.scrabble.dto.WordSpan;
import ca.adamschrofel.scrabble.rack.BlankAssigner;
import ca.adamschrofel.scrabble.rack.Rack;
import ca.adamschrofel.scrabble.scoring.WordScorer;

/**
 * Validates and scores a single {@link Placement} against the current
 * {@link Board}.
 *
 * <p>
 * This is the authoritative legality check for the solver:
 * it applies tiles temporarily, validates all words formed against the
 * dictionary,
 * and computes the Scrabble score including bonuses and bingo.
 */
public class MoveEvaluator {

    public static MoveEvaluation evaluate(
            Board board,
            BoardLayout layout,
            Placement placement,
            Dictionary dictionary,
            Rack rack) {
        if (!board.isLegalPlacement(placement)) {
            return new MoveEvaluation(false, 0, List.of(), List.of());
        }

        // Compute newly placed tiles, including which are blanks.
        List<PlacedTile> newlyPlaced = BlankAssigner.computePlacedTiles(board, layout, placement, rack);
        if (newlyPlaced.isEmpty()) {
            return new MoveEvaluation(false, 0, List.of(), List.of());
        }

        board.applyTiles(newlyPlaced);

        try {
            // Build the main word using an anchor tile.
            PlacedTile anchor = newlyPlaced.get(0);
            WordSpan main = buildWordSpan(board, anchor.row(), anchor.column(), placement.direction());

            String mainWord = main.word();
            if (mainWord.length() < 2 || !dictionary.contains(mainWord)) {
                return new MoveEvaluation(false, 0, List.of(), List.of());
            }

            // Lookup tables for scoring
            boolean[] isNewSquare = new boolean[Board.SIZE * Board.SIZE];
            boolean[] isBlankOnSquare = new boolean[Board.SIZE * Board.SIZE];
            for (PlacedTile t : newlyPlaced) {
                int idx = t.row() * Board.SIZE + t.column();
                isNewSquare[idx] = true;
                if (t.isBlank()) {
                    isBlankOnSquare[idx] = true;
                }
            }

            int totalScore = WordScorer.scoreWord(board, layout, main, isNewSquare, isBlankOnSquare);

            List<String> wordsFormed = new ArrayList<>(1 + newlyPlaced.size());
            wordsFormed.add(mainWord);

            Direction crossDirection = perpendicular(placement.direction());

            for (PlacedTile t : newlyPlaced) {
                // Fast reject: if no adjacent tiles in cross direction, cross word length is 1.
                if (!hasNeighborInDirection(board, t.row(), t.column(), crossDirection)) {
                    continue;
                }

                WordSpan cross = buildWordSpan(board, t.row(), t.column(), crossDirection);
                String crossWord = cross.word();

                if (crossWord.length() >= 2) {
                    if (!dictionary.contains(crossWord)) {
                        return new MoveEvaluation(false, 0, List.of(), List.of());
                    }
                    wordsFormed.add(crossWord);
                    totalScore += WordScorer.scoreWord(board, layout, cross, isNewSquare, isBlankOnSquare);
                }
            }

            // Bingo bonus (exactly 7 tiles placed)
            if (newlyPlaced.size() == 7) {
                totalScore += 50;
            }

            return new MoveEvaluation(true, totalScore, wordsFormed, newlyPlaced);

        } finally {
            board.unapplyTiles(newlyPlaced);
        }
    }

    private static boolean hasNeighborInDirection(Board board, int row, int col, Direction dir) {
        int dr = dir.directionRow;
        int dc = dir.directionColumn;

        // look one step backward and forward along cross direction
        int r1 = row - dr, c1 = col - dc;
        if (inBounds(r1, c1) && board.getTile(r1, c1) != '.') {
            return true;
        }

        int r2 = row + dr, c2 = col + dc;
        if (inBounds(r2, c2) && board.getTile(r2, c2) != '.') {
            return true;
        }

        return false;
    }

    private static WordSpan buildWordSpan(Board board, int row, int column, Direction direction) {
        int dr = direction.directionRow;
        int dc = direction.directionColumn;

        int r = row;
        int c = column;

        while (inBounds(r - dr, c - dc) && board.getTile(r - dr, c - dc) != '.') {
            r -= dr;
            c -= dc;
        }

        StringBuilder sb = new StringBuilder();
        int r2 = r;
        int c2 = c;

        while (inBounds(r2, c2) && board.getTile(r2, c2) != '.') {
            sb.append(board.getTile(r2, c2));
            r2 += dr;
            c2 += dc;
        }

        return new WordSpan(sb.toString(), r, c, direction);
    }

    private static Direction perpendicular(Direction direction) {
        return (direction == Direction.ACROSS) ? Direction.DOWN : Direction.ACROSS;
    }

    private static boolean inBounds(int r, int c) {
        return r >= 0 && r < Board.SIZE && c >= 0 && c < Board.SIZE;
    }
}