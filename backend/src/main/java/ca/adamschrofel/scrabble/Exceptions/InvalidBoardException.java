package ca.adamschrofel.scrabble.exceptions;
/**
 * Thrown when a provided board representation is malformed (wrong size/characters/etc.).
 */
public class InvalidBoardException extends RuntimeException {
    public InvalidBoardException(String message) {
        super(message);
    }
}
