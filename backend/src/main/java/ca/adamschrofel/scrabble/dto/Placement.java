package ca.adamschrofel.scrabble.dto;

import ca.adamschrofel.scrabble.Direction;

public record Placement(String word, int row, int column, Direction direction) {
}
