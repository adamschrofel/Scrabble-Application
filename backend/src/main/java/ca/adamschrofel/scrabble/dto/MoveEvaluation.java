package ca.adamschrofel.scrabble.dto;

import java.util.List;

public record MoveEvaluation(boolean legal, int score, List<String> wordsFormed) {
}
