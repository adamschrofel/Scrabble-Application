package ca.adamschrofel.scrabble.dto;

import java.util.List;

/**
 * Represents the current server-side board state in a JSON-friendly form.
 *
 * <p>The board is represented as a fixed-size list of strings, one per row, where '.' indicates
 * an empty square and 'A'–'Z' indicate placed tiles.</p>
 *
 * @param size board dimension (standard Scrabble is 15)
 * @param rows board rows as strings
 */
public record BoardState(int size, List<String> rows) {}
