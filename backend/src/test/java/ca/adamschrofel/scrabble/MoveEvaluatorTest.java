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
import ca.adamschrofel.scrabble.board.TileType;

class MoveEvaluatorTest {

    private static Dictionary dictOf(String... words) {
        Set<String> set = Set.of(words);
        return new Dictionary() {
            @Override public boolean contains(String word) { return set.contains(word); }
            @Override public List<String> wordsOfLength(int len) { return List.of(); }
        };
    }

    @Test
    void firstMoveScoresDoubleWordOnCenter() {
        Board board = new Board();
        BoardLayout layout = new BoardLayout();
        layout.setTileType(7, 7, TileType.DOUBLE_WORD);

        Placement p = new Placement("HI", 7, 7, Direction.ACROSS);
        Rack rack = Rack.parseRack("HI");

        MoveEvaluation eval = MoveEvaluator.evaluate(board, layout, p, dictOf("HI"), rack);

        assertTrue(eval.legal());
        // H(4) + I(1) = 5, center DW => 10
        assertEquals(10, eval.score());
        assertTrue(eval.wordsFormed().contains("HI"));
    }

    @Test
    void rejectsWordNotInDictionary() {
        Board board = new Board();
        BoardLayout layout = new BoardLayout();
        layout.setTileType(7, 7, TileType.DOUBLE_WORD);

        Placement p = new Placement("ZZ", 7, 7, Direction.ACROSS);
        Rack rack = Rack.parseRack("ZZ");

        MoveEvaluation eval = MoveEvaluator.evaluate(board, layout, p, dictOf("HI"), rack);

        assertFalse(eval.legal());
        assertEquals(0, eval.score());
    }
}
