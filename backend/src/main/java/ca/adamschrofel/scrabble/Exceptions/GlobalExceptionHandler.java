package ca.adamschrofel.scrabble.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

/**
 * Global exception handler for the entire Spring application.
 * Marked with @RestControllerAdvice so it applies to all REST controllers.
 * Catches exceptions and converts them to appropriate HTTP responses.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles InvalidTilesException thrown by the /api/solve endpoint.
     * Converts this application exception into a structured JSON error response.
     * 
     * Returns HTTP 400 BAD_REQUEST because the client provided invalid input.
     *
     * Response format:
     * {
     * "error": "INVALID_TILES",
     * "message": "Too many tiles! Please enter between 2 and 15 tiles."
     * }
     *
     * @param e The InvalidTilesException containing user-friendly error details
     * @return A ResponseEntity with HTTP 400 and error details in JSON format
     */
    @ExceptionHandler(InvalidTilesException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidTiles(InvalidTilesException e) {
        // Return a structured error response as JSON
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                Map.of("error", "INVALID_TILES",
                        "message", e.getMessage()));
    }

    @ExceptionHandler(InvalidBoardException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidBoard(InvalidBoardException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                Map.of("error", "INVALID_BOARD",
                        "message", e.getMessage()));
    }
}
