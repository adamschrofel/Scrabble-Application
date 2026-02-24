package ca.adamschrofel.scrabble.scoring;

import ca.adamschrofel.scrabble.board.Board;
import ca.adamschrofel.scrabble.board.BoardLayout;
import ca.adamschrofel.scrabble.board.TileType;
import ca.adamschrofel.scrabble.dto.WordSpan;

/**
 * Unified scorer for any WordSpan (main word or cross word).
 *
 * Rules:
 * - Letter/word multipliers apply only on newly placed squares.
 * - Newly placed blanks score 0 for the letter value.
 * - Existing board tiles score their normal face value and receive no multipliers.
 */
public final class WordScorer {

    private WordScorer() {
    }

    /**
     * @param isNewSquare       length 225 (15*15), true if square (r,c) is newly placed.
     * @param isBlankOnSquare   length 225 (15*15), true if newly placed square (r,c) is a blank.
     */
    public static int scoreWord(
            Board board,
            BoardLayout layout,
            WordSpan span,
            boolean[] isNewSquare,
            boolean[] isBlankOnSquare
    ) {
        int dr = span.direction().directionRow;
        int dc = span.direction().directionColumn;

        int wordMultiplier = 1;
        int total = 0;

        for (int i = 0; i < span.word().length(); i++) {
            int row = span.startRow() + dr * i;
            int col = span.startColumn() + dc * i;
            int idx = idx(row, col);

            char letter = board.getTile(row, col);

            if (isNewSquare[idx]) {
                int base = isBlankOnSquare[idx] ? 0 : ScrabbleScoring.scoreLetter(letter);
                TileType tt = layout.getTileType(row, col);
                total += base * tt.letterMultiplier;
                wordMultiplier *= tt.wordMultiplier;
            } else {
                total += ScrabbleScoring.scoreLetter(letter);
            }
        }

        return total * wordMultiplier;
    }

    private static int idx(int r, int c) {
        return r * Board.SIZE + c;
    }
}
