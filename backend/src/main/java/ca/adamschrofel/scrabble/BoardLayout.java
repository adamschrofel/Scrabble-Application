package ca.adamschrofel.scrabble;

public class BoardLayout {
    private final TileType[][] tileType = new TileType[Board.SIZE][Board.SIZE];

    public BoardLayout() {
        for (int i = 0; i < Board.SIZE; i++) {
            for (int j = 0; j < Board.SIZE; j++) {
                tileType[i][j] = TileType.NORMAL;
            }
        }
    }

    public TileType getTileType(int row, int column) {
        if (row < 0 || row >= Board.SIZE || column < 0 || column >= Board.SIZE) {
            throw new IllegalArgumentException("Out of bounds " + row + ", " + column);
        }
        return tileType[row][column];
    }

    public void setTileType(int row, int column, TileType tt) {
        if (row < 0 || row >= Board.SIZE || column < 0 || column >= Board.SIZE) {
            throw new IllegalArgumentException("Out of bounds " + row + ", " + column);
        }
        tileType[row][column] = tt;
    }

    public static BoardLayout standardLayout() {
        BoardLayout layout = new BoardLayout();
        // Triple Word
        int[][] TW = {
                { 0, 0 }, { 0, 7 }, { 0, 14 }, { 7, 0 }, { 7, 14 },
                { 14, 0 }, { 14, 7 }, { 14, 14 }
        };
        // Double Word
        int[][] DW = { { 1, 1 }, { 2, 2 }, { 3, 3 }, { 4, 4 },
                { 7, 7 }, { 10, 10 }, { 11, 11 }, { 12, 12 }, { 13, 13 },
                 { 1, 13 }, { 2, 12 }, { 3, 11 }, { 4, 10 },
                { 10, 4 }, { 11, 3 }, { 12, 2 }, { 13, 1 } };
        // Triple Letter
        int[][] TL = {
                { 1, 5 }, { 1, 9 },
                { 5, 1 }, { 5, 5 }, { 5, 9 }, { 5, 13 },
                { 9, 1 }, { 9, 5 }, { 9, 9 }, { 9, 13 },
                { 13, 5 }, { 13, 9 }
        };

        // Double Letter
        int[][] DL = {
                { 0, 3 }, { 0, 11 },
                { 2, 6 }, { 2, 8 },
                { 3, 0 }, { 3, 7 }, { 3, 14 },
                { 6, 2 }, { 6, 6 }, { 6, 8 }, { 6, 12 },
                { 7, 3 }, { 7, 11 },
                { 8, 2 }, { 8, 6 }, { 8, 8 }, { 8, 12 },
                { 11, 0 }, { 11, 7 }, { 11, 14 },
                { 12, 6 }, { 12, 8 },
                { 14, 3 }, { 14, 11 }
        };

        // Map tiles to board
        for (int[] p : TW)
            layout.setTileType(p[0], p[1], TileType.TRIPLE_WORD);
        for (int[] p : DW)
            layout.setTileType(p[0], p[1], TileType.DOUBLE_WORD);
        for (int[] p : TL)
            layout.setTileType(p[0], p[1], TileType.TRIPLE_LETTER);
        for (int[] p : DL)
            layout.setTileType(p[0], p[1], TileType.DOUBLE_LETTER);
        return layout;
    }
}
