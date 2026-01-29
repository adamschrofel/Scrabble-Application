package ca.adamschrofel.scrabble;

import org.junit.jupiter.api.Test;

import ca.adamschrofel.scrabble.dto.Placement;

import static org.junit.jupiter.api.Assertions.*;

public class PlacementTest {

    @Test
    void canPlaceRejectsOutOfBounds() {
        Board b = new Board();
        assertFalse(b.canPlace(new Placement("HELLO", 0, 14, Direction.ACROSS))); // spills off right edge
    }

    @Test
    void canPlaceRejectsConflicts() {
        Board b = new Board();
        b.setTile(7, 7, 'H');
        assertFalse(b.canPlace(new Placement("AXE", 7, 7, Direction.ACROSS))); // conflicts at H vs A
    }

    @Test
    void canPlaceAllowsOverlapSameLetter() {
        Board b = new Board();
        b.setTile(7, 7, 'H');
        assertTrue(b.canPlace(new Placement("HI", 7, 7, Direction.ACROSS))); // overlaps H ok
    }
}
