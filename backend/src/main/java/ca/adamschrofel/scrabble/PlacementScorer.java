package ca.adamschrofel.scrabble;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PlacementScorer {
    public static int scoreMainWord(
            Board board,
            BoardLayout layout,
            WordSpan main,
            List<PlacedTile> newlyPlaced) {

        int dRow = main.direction().directionRow;
        int dColumn = main.direction().directionColumn;

        // find which squares are new this turn
        Set<String> newSquares = new HashSet<>();
        for (PlacedTile t : newlyPlaced) {
            newSquares.add(t.row() + "," + t.column());
        }

        int wordMultiplier = 1;

        int total = 0;

        for (int i = 0; i < main.word().length(); i++) {
            int row = main.startRow() + dRow * i;
            int column = main.startColumn() + dColumn * i;

            char letter = board.getTile(row, column);
            int letterScore = ScrabbleScoring.scoreLetter(letter);

            boolean isNew = newSquares.contains(row + "," + column);

            if (isNew) {
                TileType tt = layout.getTileType(row, column);
                letterScore *= tt.letterMultiplier;
                wordMultiplier *= tt.wordMultiplier;
            }
            total += letterScore;
        }

        total *= wordMultiplier;
        if (newlyPlaced.size() == 7) {
            total += 50;
        }
        return total;
    }

}
