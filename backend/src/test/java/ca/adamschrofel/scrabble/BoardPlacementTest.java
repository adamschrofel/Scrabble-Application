package ca.adamschrofel.scrabble;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

import ca.adamschrofel.scrabble.board.Board;
import ca.adamschrofel.scrabble.board.Direction;
import ca.adamschrofel.scrabble.dto.PlacedTile;
import ca.adamschrofel.scrabble.dto.Placement;

public class BoardPlacementTest {
    @Test
    void placeWritesTilesAndReturnsNewOnes() {
        Board b = new Board();

        List<PlacedTile> placed = b.place(new Placement("HI", 7, 7, Direction.ACROSS));

        assertEquals(2, placed.size());
        assertEquals('H', b.getTile(7, 7));
        assertEquals('I', b.getTile(7, 8));
    }

    @Test
    void placeDoesNotCountOverlapsAsNew() {
        Board b = new Board();
        b.place(new Placement("HI", 7, 7, Direction.ACROSS));

        // Now place "HIT" starting at same H; H and I overlap, only T is new
        List<PlacedTile> placed2 = b.place(new Placement("HIT", 7, 7, Direction.ACROSS));

        assertEquals(1, placed2.size());
        assertEquals(new PlacedTile(7, 9, 'T', false), placed2.get(0));
        assertEquals('T', b.getTile(7, 9));
    }

    @Test
    void placeThrowsOnConflict() {
        Board b = new Board();
        b.setTile(7, 7, 'H');

        assertThrows(IllegalArgumentException.class,
                () -> b.place(new Placement("AXE", 7, 7, Direction.ACROSS)));
    }
}
