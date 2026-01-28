package ca.adamschrofel.scrabble;

/**
 * Custom exception thrown when user input contains invalid tile characters or violates constraints.
 * This exception is caught by GlobalExceptionHandler and returns a 400 BAD_REQUEST response.
 * Invalid scenarios include: non-A-Z characters (except ? and * for blanks), or more than 15 tiles.
 */
public class InvalidTilesException extends Exception {
    /**
     * Creates an InvalidTilesException
     *
     * @param message
     */
    public InvalidTilesException(String message) {
        super(message);
    }
}
