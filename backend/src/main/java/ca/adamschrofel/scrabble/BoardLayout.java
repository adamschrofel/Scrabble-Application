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

    public TileType getTileType(int row, int column){
        if (row< 0 || row >= Board.SIZE || column < 0 || column >= Board.SIZE){
            throw new IllegalArgumentException("Out of bounds "+ row + ", " + column);
        }
        return tileType[row][column];
    }

    public void setTileType(int row, int column, TileType tt){
        if (row< 0 || row >= Board.SIZE || column < 0 || column >= Board.SIZE){
            throw new IllegalArgumentException("Out of bounds "+ row + ", " + column);
        }
        tileType[row][column] = tt;
    }

    public static BoardLayout standardLayout(){
        BoardLayout layout = new BoardLayout();
        layout.setTileType(7, 7, TileType.DOUBLE_WORD);
        return layout;
    }
}
