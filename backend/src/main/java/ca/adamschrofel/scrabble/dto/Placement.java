package ca.adamschrofel.scrabble.dto;

import ca.adamschrofel.scrabble.board.Direction;

public record Placement(String word, int row, int column, Direction direction) {
}
