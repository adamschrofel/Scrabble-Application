package ca.adamschrofel.scrabble.dto;

/**
 * Describes a single board square update.
 *
 * <p>The {@code tile} may be {@code null}, empty, or "." to clear the square.</p>
 *
 * @param row zero-based row index
 * @param column zero-based column index
 * @param tile tile text (first character is used)
 */
public record BoardTileUpdate(int row, int column, String tile) {}
