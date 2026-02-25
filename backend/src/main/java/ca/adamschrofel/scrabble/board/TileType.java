package ca.adamschrofel.scrabble.board;
/**
 * Tile types of board used for scoring
 */
public enum TileType {
    NORMAL(1, 1),
    DOUBLE_LETTER(2, 1),
    TRIPLE_LETTER(3, 1),
    DOUBLE_WORD(1, 2),
    TRIPLE_WORD(1, 3);

    public final int letterMultiplier;
    public final int wordMultiplier;

    TileType(int letterMultiplier, int wordMultiplier) {
        this.letterMultiplier = letterMultiplier;
        this.wordMultiplier = wordMultiplier;
    }
}
