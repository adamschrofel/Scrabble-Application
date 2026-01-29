package ca.adamschrofel.scrabble;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class BoardLayoutTest {
    
    @Test
    void testcenter(){
        BoardLayout layout = BoardLayout.standardLayout();
        assertEquals(TileType.DOUBLE_WORD, layout.getTileType(7, 7));
        System.out.println(layout.getTileType(7, 7));
        assertEquals(TileType.NORMAL, layout.getTileType(0, 0));
    }
}
