package ca.adamschrofel.scrabble;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import ca.adamschrofel.scrabble.scoring.ScrabbleScoring;

class ScrabbleScoringTest {

    @Test
    void scoreLetter_basicValues() {
        assertEquals(1, ScrabbleScoring.scoreLetter('A'));
        assertEquals(10, ScrabbleScoring.scoreLetter('Q'));
        assertEquals(0, ScrabbleScoring.scoreLetter('?'));
        assertEquals(0, ScrabbleScoring.scoreLetter('*'));
    }

    @Test
    void scoreWord_sumsLetters_caseInsensitive() {
        // H=4, I=1 => 5
        assertEquals(5, ScrabbleScoring.scoreWord("hi"));
    }
}
