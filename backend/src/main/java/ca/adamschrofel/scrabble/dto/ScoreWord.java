package ca.adamschrofel.scrabble.dto;
/**
 * A word paired with its Scrabble score.
 *
 * @param word the word
 * @param score the computed Scrabble score for the word
 */
public record ScoreWord(String word, int score) {

}
