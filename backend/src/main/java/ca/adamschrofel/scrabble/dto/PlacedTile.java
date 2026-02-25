package ca.adamschrofel.scrabble.dto;
/**
 * A single tile to be placed on the board as part of a move.
 *
 * @param row zero-based row index
 * @param column zero-based column index
 * @param letter uppercase tile letter; blanks are represented by their assigned letter
 */
public record PlacedTile(int row, int column, char tile, boolean isBlank) {

}
