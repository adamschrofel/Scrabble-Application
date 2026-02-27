package ca.adamschrofel.scrabble;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import ca.adamschrofel.scrabble.board.BoardLayout;
import ca.adamschrofel.scrabble.board.TileType;

public class BoardLayoutTest {
    
    @Test
    void testBoardTiles(){
        BoardLayout layout = BoardLayout.standardLayout();
        assertEquals(TileType.DOUBLE_WORD, layout.getTileType(7, 7));
        assertEquals(TileType.TRIPLE_WORD, layout.getTileType(0, 0));
    }
}
