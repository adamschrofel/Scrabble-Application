package ca.adamschrofel.scrabble.dto;

import java.util.List;

public record WordGroup(int length, List<ScoreWord> words) {
    
}
