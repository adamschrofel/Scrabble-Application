package ca.adamschrofel.scrabble.dto;
import java.util.List;
public record BestPlay(Placement placement, int score, List<String> wordsFormed, List<PlacedTile> tilesPlaced) {
    
}
