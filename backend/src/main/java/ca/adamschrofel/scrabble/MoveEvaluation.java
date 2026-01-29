package ca.adamschrofel.scrabble;

import java.util.List;

public record MoveEvaluation(boolean legal, int score, List<String> wordsFormed) {
}
