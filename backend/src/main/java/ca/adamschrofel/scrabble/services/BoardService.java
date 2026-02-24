package ca.adamschrofel.scrabble.services;

import org.springframework.stereotype.Service;

import ca.adamschrofel.scrabble.board.Board;
/**
 * In-memory representation of the current board state for the running server.
 *
 * <p>This service exists to support interactive play in the UI: clients can read
 * the current grid, reset it, or set individual squares. It is <strong>not</strong>
 * a persistence layer.
 *
 * <p>All access is synchronized because the backing grid is mutable and the API
 * supports concurrent requests.
 */
@Service
public class BoardService {
    public static final int SIZE = 15;

    private final char[][] grid = new char[SIZE][SIZE];

    public BoardService() {
        reset();
    }
    /**
     * Creates a new {@link Board} populated with the service's current grid.
     */
    public synchronized Board setupStandardBoard() {
        Board b = new Board();
        for (int row = 0; row < SIZE; row++) {
            for (int column = 0; column < SIZE; column++) {
                b.setTile(row, column, grid[row][column]);
            }
        }
        return b;

    }
     /** Clears the board to an all-empty ('.') state. */
    public synchronized void reset() {
        for (int row = 0; row < SIZE; row++) {
            for (int column = 0; column < SIZE; column++) {
                grid[row][column] = '.';
            }
        }
    }
    /** Returns the tile at the given position, or '.' if empty. */
    public synchronized char get(int row, int col) {
        check(row, col);
        return grid[row][col];
    }
    /**
     * Sets a tile on the board.
     *
     * @param tile 'A'..'Z' to place a tile, or '.' to clear a square
     */
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
    /** Returns a 15-element array of row strings ('.' for empty). */
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
