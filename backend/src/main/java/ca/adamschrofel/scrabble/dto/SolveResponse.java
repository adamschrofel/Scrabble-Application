package ca.adamschrofel.scrabble.dto;

import java.util.List;
/**
 * Response for rack-only solving.
 *
 * @param tilesNormalized normalized rack string used for solving
 * @param groups results grouped by length
 */
public record SolveResponse(String tiles, List<WordGroup> groups) {

}
