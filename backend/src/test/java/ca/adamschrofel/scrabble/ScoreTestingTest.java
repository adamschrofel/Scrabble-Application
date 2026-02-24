package ca.adamschrofel.scrabble;

import org.junit.jupiter.api.Test;

import ca.adamschrofel.scrabble.board.Board;

//import ca.adamschrofel.scrabble.dto.MoveEvaluation;
//import ca.adamschrofel.scrabble.dto.Placement;
//import ca.adamschrofel.scrabble.rack.Rack;
//import ca.adamschrofel.scrabble.rack.RackWordFinder;
//import ca.adamschrofel.scrabble.solver.MoveEvaluator;

//import static org.junit.jupiter.api.Assertions.*;

public class ScoreTestingTest {
    @Test
    void extendingExistingWordBuildsFullMainWord() {
        Board board = new Board();
        //BoardLayout layout = new BoardLayout();

        board.setTile(7, 7, 'T');
        board.setTile(7, 8, 'R');
        board.setTile(7, 9, 'Y');

       // RackWordFinder dict = new RackWordFinder("dictionary/csw19Words.txt");

        //Placement p = new Placement("ING", 7, 10, Direction.ACROSS);

        //MoveEvaluation eval = MoveEvaluator.evaluate(board, layout, p, dict, null);

        //assertTrue(eval.legal());
        //assertTrue(eval.wordsFormed().contains("TRYING"));
        //assertEquals(10, eval.score()); // base score only
    }

}
