package ca.adamschrofel.scrabble.dto;

import java.util.List;
/**
 * Groups scored words by a common property (typically word length) for API responses.
 *
 * @param length the word length
 * @param words list of words and their scores
 */
public record WordGroup(int length, List<ScoreWord> words) {

}
