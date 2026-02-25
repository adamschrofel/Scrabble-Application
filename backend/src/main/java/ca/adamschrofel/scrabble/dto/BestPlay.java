package ca.adamschrofel.scrabble.dto;

import java.util.List;
/**
 * A fully evaluated, legal move suggestion produced by the board solver.
 *
 * @param placement the chosen placement
 * @param evaluation evaluation details including score and tiles placed
 */
public record BestPlay(Placement placement, int score, List<String> wordsFormed, List<PlacedTile> tilesPlaced) {

}
