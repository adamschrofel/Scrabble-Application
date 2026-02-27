package ca.adamschrofel.scrabble;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import ca.adamschrofel.scrabble.exceptions.InvalidTilesException;

class InputValidatorTest {

    @Test
    void normalizeTiles_trimsWhitespaceAndUppercases() throws Exception {
        assertEquals("ABC", InputValidator.normalizeTiles(" a b c "));
    }

    @Test
    void normalizeTiles_rejectsNull() {
        assertThrows(InvalidTilesException.class, () -> InputValidator.normalizeTiles(null));
    }

    @Test
    void normalizeTiles_rejectsTooShort() {
        assertThrows(InvalidTilesException.class, () -> InputValidator.normalizeTiles("A"));
        assertThrows(InvalidTilesException.class, () -> InputValidator.normalizeTiles("   "));
    }

    @Test
    void normalizeTiles_rejectsTooLong() {
        String tooLong = "ABCDEFGHIJKLMNOP"; // 16
        assertThrows(InvalidTilesException.class, () -> InputValidator.normalizeTiles(tooLong));
    }

    @Test
    void normalizeTiles_allowsBlanks() throws Exception {
        assertEquals("A?*", InputValidator.normalizeTiles("a?*"));
    }

    @Test
    void normalizeTiles_rejectsInvalidCharacters() {
        InvalidTilesException ex = assertThrows(InvalidTilesException.class, () -> InputValidator.normalizeTiles("AB1"));
        assertTrue(ex.getMessage().toUpperCase().contains("INVALID"));
    }
}
