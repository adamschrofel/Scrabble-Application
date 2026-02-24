package ca.adamschrofel.scrabble.solver;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ca.adamschrofel.scrabble.Board;
import ca.adamschrofel.scrabble.BoardLayout;
import ca.adamschrofel.scrabble.Direction;
import ca.adamschrofel.scrabble.WordScorer;
import ca.adamschrofel.scrabble.dictionary.Dictionary;
import ca.adamschrofel.scrabble.dto.MoveEvaluation;
import ca.adamschrofel.scrabble.dto.PlacedTile;
import ca.adamschrofel.scrabble.dto.Placement;
import ca.adamschrofel.scrabble.dto.WordSpan;
import ca.adamschrofel.scrabble.rack.BlankAssigner;
import ca.adamschrofel.scrabble.rack.Rack;

public class MoveEvaluator {
    public static MoveEvaluation evaluate(Board board, BoardLayout layout, Placement placement, Dictionary dictionary, Rack rack) {
        if (!board.isLegalPlacement(placement)) {
            return new MoveEvaluation(false, 0, List.of(), List.of());
        }

        // Compute newly placed tiles, including which are blanks.
        // We apply these tiles temporarily for scoring and cross-word validation.
        List<PlacedTile> newlyPlaced = BlankAssigner.computePlacedTiles(board, layout, placement, rack);
        if (newlyPlaced.isEmpty()) {
            return new MoveEvaluation(false, 0, List.of(), List.of());
        }

        board.applyTiles(newlyPlaced);

        try {
            PlacedTile anchor = newlyPlaced.get(0);
            WordSpan main = buildWordSpan(board, anchor.row(), anchor.column(), placement.direction());

            if (main.word().length() < 2 || !dictionary.contains(main.word())){
                return new MoveEvaluation(false, 0, List.of(), List.of());
            }

            // Build fast lookup tables for scoring
            boolean[] isNewSquare = new boolean[Board.SIZE * Board.SIZE];
            boolean[] isBlankOnSquare = new boolean[Board.SIZE * Board.SIZE];
            for (PlacedTile t : newlyPlaced) {
                int idx = t.row() * Board.SIZE + t.column();
                isNewSquare[idx] = true;
                if (t.isBlank()) {
                    isBlankOnSquare[idx] = true;
                }
            }

            // score the main word and add any valid cross words
            int totalScore = WordScorer.scoreWord(board, layout, main, isNewSquare, isBlankOnSquare);

            List<String> wordsFormed = new ArrayList<>();
            wordsFormed.add(main.word());

            Direction crossDirection = perpendicular(placement.direction());

            Set<String> seen = new HashSet<>();

            for (PlacedTile t : newlyPlaced) {
                WordSpan cross = buildWordSpan(board, t.row(), t.column(), crossDirection);

                if (cross.word().length() >= 2) {
                    if (!dictionary.contains(cross.word())) {
                        return new MoveEvaluation(false, 0, List.of(), List.of());
                    }

                    String key = cross.word() + "@" + cross.startRow() + "," + cross.startColumn() + ":" + cross.direction();
                    
                    if(seen.add(key)){
                        wordsFormed.add(cross.word());
                        totalScore += WordScorer.scoreWord(board, layout, cross, isNewSquare, isBlankOnSquare);
                    }
                    
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

    private static WordSpan buildWordSpan(Board board, int row, int column, Direction direction){
        int dr = direction.directionRow;
        int dc = direction.directionColumn;

        int r = row;
        int c = column;
        // walk backwards to find the start of the word span
        while (inBounds(r - dr, c - dc) && board.getTile(r - dr, c - dc) != '.') {
            r -= dr;
            c -= dc;

        }

        StringBuilder sb = new StringBuilder();
        int r2 = r;
        int c2 = c;
        // collect continuous letters going forward to form the word
        while (inBounds(r2, c2) && board.getTile(r2, c2) != '.') {
            sb.append(board.getTile(r2, c2));
            r2 += dr;
            c2 += dc;
        }
        return new WordSpan(sb.toString(), r, c, direction);
    }

    private static Direction perpendicular(Direction direction){
        return (direction == Direction.ACROSS)? Direction.DOWN : Direction.ACROSS;
    }

    private static boolean inBounds(int r, int c) {
        return r >= 0 && r < Board.SIZE && c >= 0 && c < Board.SIZE;
    }
}
