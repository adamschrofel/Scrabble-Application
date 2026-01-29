package ca.adamschrofel.scrabble;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MoveEvaluator {
    public static MoveEvaluation evaluate(Board board, BoardLayout layout, Placement placement, WordFinder dictionary) {
        if (!board.isLegalPlacement(placement)) {
            return new MoveEvaluation(false, 0, List.of());
        }

        List<PlacedTile> newlyPlaced = board.place(placement);

        try {
            if (newlyPlaced.isEmpty()) {
                return new MoveEvaluation(false, 0, List.of());
            }

            PlacedTile anchor = newlyPlaced.get(0);
            WordSpan main = buildWordSpan(board, anchor.row(), anchor.column(), placement.direction());

            if (main.word().length() < 2 || !dictionary.isWord(main.word())){
                return new MoveEvaluation(false, 0, List.of());
            }

            int totalScore = PlacementScorer.scoreMainWord(board, layout, main, newlyPlaced);

            List<String> wordsFormed = new ArrayList<>();
            wordsFormed.add(main.word());

            Direction crossDirection = perpendicular(placement.direction());

            Set<String> seen = new HashSet<>();

            for (PlacedTile t : newlyPlaced) {
                WordSpan cross = buildWordSpan(board, t.row(), t.column(), crossDirection);

                if (cross.word().length() >= 2) {
                    if (!dictionary.isWord(cross.word())) {
                        return new MoveEvaluation(false, 0, List.of());
                    }

                    String key = cross.word() + "@" + cross.startRow() + "," + cross.startColumn() + ":" + cross.direction();
                    
                    if(seen.add(key)){
                        wordsFormed.add(cross.word());
                        totalScore += scoreCrossWord(board, layout, cross, t.row(), t.column());
                    }
                    
                }
            }
            return new MoveEvaluation(true, totalScore, wordsFormed);

        } finally {
            board.unplace(newlyPlaced);
        }
    }

    private static WordSpan buildWordSpan(Board board, int row, int column, Direction direction){
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

    private static int scoreCrossWord(Board board, BoardLayout layout, WordSpan crossWord, int newRow, int newColumn){
            int dr = crossWord.direction().directionRow;
            int dc = crossWord.direction().directionColumn;
       

        int r = crossWord.startRow();
        int c = crossWord.startColumn();

        int wordMultiplier = 1;
        int total = 0;

        for (int i = 0; i < crossWord.word().length(); i++) {
            char letter = board.getTile(r, c);
            int letterScore = ScrabbleScoring.scoreLetter(letter);

            if (newRow == r && c == newColumn) {
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

    private static Direction perpendicular(Direction direction){
        return (direction == Direction.ACROSS)? Direction.DOWN : Direction.ACROSS;
    }

    private static boolean inBounds(int r, int c) {
        return r >= 0 && r < Board.SIZE && c >= 0 && c < Board.SIZE;
    }
}
