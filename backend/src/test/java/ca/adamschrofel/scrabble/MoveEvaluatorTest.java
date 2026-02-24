package ca.adamschrofel.scrabble;

import org.junit.jupiter.api.Test;

import ca.adamschrofel.scrabble.dto.MoveEvaluation;
import ca.adamschrofel.scrabble.dto.Placement;
import ca.adamschrofel.scrabble.solver.MoveEvaluator;

import static org.junit.jupiter.api.Assertions.*;

public class MoveEvaluatorTest {

    @Test
    void evaluatesMainAndCrossWordScoring() {
        Board board = new Board();

        // Existing vertical letters that will form A T E when we place T
        board.setTile(6, 8, 'A');
        board.setTile(8, 8, 'E');

        BoardLayout layout = new BoardLayout();
        layout.setTileType(7, 7, TileType.DOUBLE_WORD);     // center DW
        layout.setTileType(7, 8, TileType.DOUBLE_LETTER);   // make cross tile interesting

        // Dictionary stub using a WordFinder instance would be overkill in unit test,
        // but since your evaluate() takes WordFinder, simplest is:
        WordFinder dict = new WordFinder("dictionary/csw19Words.txt");

        // Place "IT" across so T lands at (7,8) making "ATE" down
        Placement p = new Placement("IT", 7, 7, Direction.ACROSS);

        MoveEvaluation eval = MoveEvaluator.evaluate(board, layout, p, dict, null);

        assertTrue(eval.legal());
        assertTrue(eval.wordsFormed().contains("IT"));
        assertTrue(eval.wordsFormed().contains("ATE"));

        // Scoring expectation:
        // Main word "IT": I=1 (center DW), T=1 with DL => 2; sum=3; DW => 6
        // Cross word "ATE": A=1 + T(1 with DL =>2) + E=1 => 4 (no word mult here)
        // Total => 10
        assertEquals(10, eval.score());
    }
}
