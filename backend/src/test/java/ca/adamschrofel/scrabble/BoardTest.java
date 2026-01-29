package ca.adamschrofel.scrabble;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BoardTest {
    @Test
    void settersngetters(){
        Board b = new Board();
        assertEquals('.', b.getTile(7,7));

        b.setTile(7, 7, 'j');
        assertEquals('J', b.getTile(7, 7));
    }
}
