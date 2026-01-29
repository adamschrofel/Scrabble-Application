package ca.adamschrofel.scrabble;

import java.util.ArrayList;
import java.util.List;

public class Board {
    public static final int SIZE = 15;
    private final char[][] grid = new char[SIZE][SIZE];

    public Board() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                grid[i][j] = '.';
            }
        }
    }

    public char getTile(int row, int column) {
        checkBoundary(row, column);
        return grid[row][column];
    }

    public void setTile(int row, int column, char tile) {
        checkBoundary(row, column);
        grid[row][column] = Character.toUpperCase(tile);

    }

    private void checkBoundary(int row, int column) {
        if (row < 0 || row > SIZE || column < 0 || column > SIZE) {
            throw new IllegalArgumentException("Out of bounds " + row + ", " + column);
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                sb.append(grid[i][j]);
            }

        }
        return sb.toString();
    }

    // Placement stuff
    public boolean canPlace(Placement p) {
        String w = p.word();
        // converts direction into tile placement

        // compute board coordinates
        int dr = p.direction().directionRow;
        int dc = p.direction().directionColumn;

        for (int i = 0; i < w.length(); i++) {
            int x = p.row() + dr * i;
            int y = p.column() + dc * i;

            // check if it fits on board
            if (x < 0 || x >= SIZE || y < 0 || y >= SIZE) {
                return false;
            }
            char existingTile = grid[x][y];
            char placedTile = w.charAt(i);
            // if theres nothing there we can place
            if (existingTile != '.' && existingTile != placedTile)
                return false;
        }
        return true;

    }

    public List<PlacedTile> place(Placement p) {
        if (!canPlace(p)) {
            throw new IllegalArgumentException("Illegal Placement");
        }

        String word = p.word();

        int dr = p.direction().directionRow;
        int dc = p.direction().directionColumn;

        List<PlacedTile> newlyPlaced = new ArrayList<>();

        for (int i = 0; i < word.length(); i++) {
            int x = p.row() + dr * i;
            int y = p.column() + dc * i;

            char existingTile = grid[x][y];
            char placedTile = word.charAt(i);
            if (existingTile == '.') {
                grid[x][y] = placedTile;
                newlyPlaced.add(new PlacedTile(x, y, placedTile));
            }
        }
        return newlyPlaced;

    }

    // checks if tile is empty
    public boolean isEmpty() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (grid[i][j] != '.') {
                    return false;
                }
            }
        }
        return true;
    }

    // checks if tile is being placed on the centre
    // which would signify double word score and start of game
    public boolean coversCenter(Placement p) {
        final int center = 7;
        String word = p.word();

        int dr = p.direction().directionRow;
        int dc = p.direction().directionColumn;

        for (int i = 0; i < word.length(); i++) {
            int x = p.row() + dr * i;
            int y = p.column() + dc * i;
            if (x == center && y == center) {
                return true;
            }
        }
        return false;

    }

    public boolean touchesExistingTile(Placement p) {
        String word = p.word();

        int dr = p.direction().directionRow;
        int dc = p.direction().directionColumn;

        for (int i = 0; i < word.length(); i++) {
            int row = p.row() + dr * i;
            int column = p.column() + dc * i;

            if (grid[row][column] != '.') {
                return true;
            }

            if (hasTileAt(row - 1, column) || hasTileAt(row + 1, column)
                    || hasTileAt(row, column - 1) || hasTileAt(row, column + 1)) {
                return true;
            }
        }
        return false;
    }


    private boolean hasTileAt(int row , int column){
        if ( row< 0 || row >= SIZE || column < 0 || column >= SIZE){
            return false; 
        }
        return grid[row][column] != '.';
    }

    public boolean isLegalPlacement(Placement p){
        if (!canPlace(p)   ){
            return false;
        }

        if (isEmpty()){
            return coversCenter(p);
        } else { 
            return touchesExistingTile(p);
        }
    }

    public void unplace(List<PlacedTile> newlyPlacedTiles){
        for (PlacedTile t : newlyPlacedTiles){
            grid[t.row()][t.column()] = '.';
        }
    }

}
