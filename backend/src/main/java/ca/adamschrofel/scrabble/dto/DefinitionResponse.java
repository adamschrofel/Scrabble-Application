package ca.adamschrofel.scrabble.dto;
/**
 * Response payload for a dictionary definition lookup.
 *
 * @param word the queried word
 * @param definition resolved definition text (or a fallback when unavailable)
 */
public record DefinitionResponse(String word, boolean found, String definition) {

}
