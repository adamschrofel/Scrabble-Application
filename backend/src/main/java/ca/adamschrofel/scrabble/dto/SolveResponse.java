package ca.adamschrofel.scrabble.dto;

import java.util.List;

public record SolveResponse(String tiles, List<WordGroup> groups) {
    
}
