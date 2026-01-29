package ca.adamschrofel.scrabble;

import java.util.ArrayList;
import java.util.List;

public class MoveEvaluator {
    public static MoveEvaluation evaluate(Board board, BoardLayout layout, Placement p, WordFinder dictionary) {
        if (!board.isLegalPlacement(p)) {
            return new MoveEvaluation(false, 0, List.of());
        }

        List<PlacedTile> newlyPlaced = board.place(p);

        try {
            if (newlyPlaced.isEmpty()) {
                return new MoveEvaluation(false, 0, List.of());
            }

            String mainWord = p.word().trim();
            if (!dictionary.isWord(mainWord)) {
                return new MoveEvaluation(false, 0, List.of());

            }

            int totalScore = PlacementScorer.scoreMainWord(board, layout, p, newlyPlaced);

            List<String> wordsFormed = new ArrayList<>();

            wordsFormed.add(mainWord);

            for (PlacedTile t : newlyPlaced) {
                String cross = buildCrossWord(board, p.direction(), t.row(), t.column());

                if (cross.length() >= 2) {
                    if (!dictionary.isWord(cross)) {
                        return new MoveEvaluation(false, 0, List.of());
                    }
                    wordsFormed.add(cross);
                    totalScore += scoreCrossWord(board, layout, cross, p.direction(), t.row(), t.column());
                }
            }
            return new MoveEvaluation(true, totalScore, wordsFormed);

        } finally {
            board.unplace(newlyPlaced);
        }
    }

    private static String buildCrossWord(Board board, Direction placedDirection, int row, int column) {
        int dr = (placedDirection == Direction.ACROSS) ? 1 : 0;
        int dc = (placedDirection == Direction.ACROSS) ? 0 : 1;

        while (inBounds(row - dr, column - dc) && board.getTile(row - dr, column - dc) != '.') {
            row -= dr;
            column -= dc;

        }

        StringBuilder sb = new StringBuilder();

        while (inBounds(row, column) && board.getTile(row, column) != '.') {
            sb.append(board.getTile(row, column));
            row += dr;
            column += dc;
        }
        return sb.toString();
    }

    private static int scoreCrossWord(Board board, BoardLayout layout, String crossWord, Direction placedDirection,
            int row, int column) {
        int dr = (placedDirection == Direction.ACROSS) ? 1 : 0;
        int dc = (placedDirection == Direction.ACROSS) ? 0 : 1;

        int r = row;
        int c = column;
        while (inBounds(r - dr, c - dc) && board.getTile(r - dr, c - dc) != '.') {
            r -= dr;
            c -= dc;

        }

        int wordMultiplier = 1;
        int total = 0;

        for (int i = 0; i < crossWord.length(); i++) {
            char letter = board.getTile(r, c);
            int letterScore = ScrabbleScoring.scoreLetter(letter);

            if (row == r && c == column) {
                TileType tt = layout.getTileType(r, c);
                letterScore *= tt.letterMultiplier;
                wordMultiplier *= tt.wordMultiplier;
            }

            total += letterScore;
            r += dr;
            c += dc;
        }
        return total * wordMultiplier;
    }

    private static boolean inBounds(int r, int c) {
        return r >= 0 && r < Board.SIZE && c >= 0 && c < Board.SIZE;
    }
}
