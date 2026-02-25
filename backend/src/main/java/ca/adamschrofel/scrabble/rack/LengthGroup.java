package ca.adamschrofel.scrabble.rack;

import java.util.List;

/**
 * Internal grouping of rack-only playable words by length.
 *
 * <p>This is not an API response type; the controller converts these into scored {@code WordGroup}
 * objects for JSON output.</p>
 *
 * @param length word length
 * @param words words of that length
 */
public record LengthGroup(int length, List<String> words) {}
