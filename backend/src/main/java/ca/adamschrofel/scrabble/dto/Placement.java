package ca.adamschrofel.scrabble.dto;

import ca.adamschrofel.scrabble.board.Direction;
/**
 * Represents a candidate word placement on the board.
 *
 * @param word placed word (uppercase A–Z)
 * @param row zero-based start row
 * @param column zero-based start column
 * @param direction placement direction (ACROSS or DOWN)
 */
public record Placement(String word, int row, int column, Direction direction) {
}
