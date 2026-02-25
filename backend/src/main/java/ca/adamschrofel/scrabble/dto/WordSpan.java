package ca.adamschrofel.scrabble.dto;

import ca.adamschrofel.scrabble.board.Direction;
/**
 * A continuous run of tiles on the board, used when scoring/evaluating placements.
 *
 * @param word the word text
 * @param row start row (zero-based)
 * @param column start column (zero-based)
 * @param direction span direction (ACROSS or DOWN)
 */
public record WordSpan(String word, int startRow, int startColumn, Direction direction) {

}
