package ca.adamschrofel.scrabble;

import org.springframework.stereotype.Service;

@Service
public class BoardService {
    public static final int SIZE = 15;

    private final char[][] grid = new char[SIZE][SIZE];

    public BoardService() {
        reset();
    }

    public synchronized Board setupStandardBoard() {
        Board b = new Board();
        for (int row = 0; row < SIZE; row++) {
            for (int column = 0; column < SIZE; column++) {
                b.setTile(row, column, grid[row][column]);
            }
        }
        return b;

    }

    public synchronized void reset() {
        for (int row = 0; row < SIZE; row++) {
            for (int column = 0; column < SIZE; column++) {
                grid[row][column] = '.';
            }
        }
    }

    public synchronized char get(int row, int col) {
        check(row, col);
        return grid[row][col];
    }

    public synchronized void set(int row, int col, char tile) {
        check(row, col);
        if (tile == '.') {
            grid[row][col] = '.';
            return;
        }
        if (tile < 'A' || tile > 'Z') {
            throw new IllegalArgumentException("Tile must be A-Z or '.' to clear.");
        }
        grid[row][col] = tile;
    }

    public synchronized String[] rowsAsStrings() {
        String[] rows = new String[SIZE];
        for (int r = 0; r < SIZE; r++) {
            rows[r] = new String(grid[r]);
        }
        return rows;
    }

    private void check(int row, int col) {
        if (row < 0 || row >= SIZE || col < 0 || col >= SIZE) {
            throw new IllegalArgumentException("Out of bounds: (" + row + "," + col + ")");
        }
    }
}
