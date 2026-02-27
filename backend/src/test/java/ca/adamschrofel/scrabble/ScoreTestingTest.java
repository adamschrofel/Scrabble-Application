package ca.adamschrofel.scrabble;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import ca.adamschrofel.scrabble.board.Board;
import ca.adamschrofel.scrabble.board.BoardLayout;
import ca.adamschrofel.scrabble.board.Direction;
import ca.adamschrofel.scrabble.dictionary.Dictionary;
import ca.adamschrofel.scrabble.dto.MoveEvaluation;
import ca.adamschrofel.scrabble.dto.Placement;
import ca.adamschrofel.scrabble.rack.Rack;
import ca.adamschrofel.scrabble.solver.MoveEvaluator;

class ScoreTestingTest {

    private static Dictionary dictOf(String... words) {
        Set<String> set = Set.of(words);
        return new Dictionary() {
            @Override public boolean contains(String word) { return set.contains(word); }
            @Override public List<String> wordsOfLength(int len) { return List.of(); }
        };
    }

    @Test
    void extendingExistingWordBuildsFullMainWord_noMultipliers() {
        Board board = new Board();
        BoardLayout layout = new BoardLayout(); // all NORMAL

        // Place TRY manually on the board (existing tiles)
        board.setTile(7, 7, 'T');
        board.setTile(7, 8, 'R');
        board.setTile(7, 9, 'Y');

        // Extend to TRYING by placing "ING" at col 10
        Placement p = new Placement("ING", 7, 10, Direction.ACROSS);
        Rack rack = Rack.parseRack("ING");

        MoveEvaluation eval = MoveEvaluator.evaluate(board, layout, p, dictOf("TRYING"), rack);

        assertTrue(eval.legal());
        assertTrue(eval.wordsFormed().contains("TRYING"));

        // T(1)+R(1)+Y(4)+I(1)+N(1)+G(2)=10 with no multipliers
        assertEquals(10, eval.score());
    }
}
