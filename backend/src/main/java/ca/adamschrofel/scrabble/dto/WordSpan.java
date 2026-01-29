package ca.adamschrofel.scrabble.dto;

import ca.adamschrofel.scrabble.Direction;

public record WordSpan(String word, int startRow, int startColumn, Direction direction) {
    
}
