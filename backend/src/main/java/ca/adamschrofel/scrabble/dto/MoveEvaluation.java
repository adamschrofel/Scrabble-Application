package ca.adamschrofel.scrabble.dto;

import java.util.List;
/**
 * Result of evaluating a candidate placement: score, tiles placed, and validity details.
 *
 * <p>This DTO is returned by the solver and is primarily consumed by the API layer.
 */
public record MoveEvaluation(boolean legal, int score, List<String> wordsFormed, List<PlacedTile> tilesPlaced) {
}
