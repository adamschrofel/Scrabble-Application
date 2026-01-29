package ca.adamschrofel.scrabble;

import org.junit.jupiter.api.Test;

import ca.adamschrofel.scrabble.dto.Placement;

import static org.junit.jupiter.api.Assertions.*;

public class BoardLegalityTest {

    @Test
    void firstMoveMustCoverCenter() {
        Board b = new Board();
        assertTrue(b.isEmpty());

        assertFalse(b.isLegalPlacement(new Placement("HI", 0, 0, Direction.ACROSS)));
        assertTrue(b.isLegalPlacement(new Placement("HI", 7, 7, Direction.ACROSS)));
        assertTrue(b.isLegalPlacement(new Placement("HI", 6, 7, Direction.DOWN))); // covers (7,7)
    }

    @Test
    void laterMoveMustTouchExisting() {
        Board b = new Board();
        b.place(new Placement("HI", 7, 7, Direction.ACROSS));

        // far away: illegal
        assertFalse(b.isLegalPlacement(new Placement("TO", 0, 0, Direction.ACROSS)));

        // adjacent: legal (touches)
        assertTrue(b.isLegalPlacement(new Placement("TO", 6, 7, Direction.ACROSS))); // touches above H

        // overlap: legal (shares I)
        assertTrue(b.isLegalPlacement(new Placement("IN", 7, 8, Direction.DOWN)));
    }
    @Test
        void directionDeltasAreCorrect() {
        assertEquals(0, Direction.ACROSS.directionRow);
        assertEquals(1, Direction.ACROSS.directionColumn);
        assertEquals(1, Direction.DOWN.directionRow);
        assertEquals(0, Direction.DOWN.directionColumn);
}

}

