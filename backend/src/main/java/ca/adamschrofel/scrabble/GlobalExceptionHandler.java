package ca.adamschrofel.scrabble;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidTilesException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidTiles(InvalidTilesException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                Map.of("error", "INVALID_TILES",
                        "message", e.getMessage()));
    }
}
