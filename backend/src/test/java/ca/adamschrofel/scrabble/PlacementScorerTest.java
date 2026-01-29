package ca.adamschrofel.scrabble;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class PlacementScorerTest {

    @Test
    void scoresCenterDoubleWord() {
        Board board = new Board();
        BoardLayout layout = BoardLayout.standardLayout(); // has (7,7) DOUBLE_WORD

        Placement p = new Placement("HI", 7, 7, Direction.ACROSS);
        List<PlacedTile> placed = board.place(p);

        int score = PlacementScorer.scoreMainWord(board, layout, p, placed);

        // H=4, I=1 -> 5; center DW -> 10
        assertEquals(10, score);
    }
}
