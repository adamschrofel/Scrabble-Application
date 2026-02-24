package ca.adamschrofel.scrabble.rack;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ca.adamschrofel.scrabble.Board;
import ca.adamschrofel.scrabble.BoardLayout;
import ca.adamschrofel.scrabble.ScrabbleScoring;
import ca.adamschrofel.scrabble.TileType;
import ca.adamschrofel.scrabble.dto.PlacedTile;
import ca.adamschrofel.scrabble.dto.Placement;

/**
 * Computes which newly placed tiles are blanks for a given placement and rack.
 *
 * Notes:
 * - Returns ONLY tiles placed onto empty squares (existing board letters are excluded).
 * - If blanks are required, chooses which specific squares become blanks by minimizing
 *   lost points (place blanks on low-value squares first).
 */
public final class BlankAssigner {
    private BlankAssigner() {}

    public static List<PlacedTile> computePlacedTiles(Board board, BoardLayout layout, Placement placement, Rack rack) {
        String word = placement.word().toUpperCase();
        int dr = placement.direction().directionRow;
        int dc = placement.direction().directionColumn;

        // Collect newly placed squares grouped by letter.
        Map<Character, List<Cell>> newCellsByLetter = new HashMap<>();
        List<Cell> allNewCells = new ArrayList<>();

        int r = placement.row();
        int c = placement.column();
        for (int i = 0; i < word.length(); i++) {
            char boardCh = board.getTile(r, c);
            char wch = word.charAt(i);

            if (boardCh == '.') {
                TileType tt = layout.getTileType(r, c);
                Cell cell = new Cell(r, c, wch, tt);
                allNewCells.add(cell);
                newCellsByLetter.computeIfAbsent(wch, k -> new ArrayList<>()).add(cell);
            }

            r += dr;
            c += dc;
        }

        if (allNewCells.isEmpty()) {
            return List.of();
        }

        // Count required letters.
        int[] neededCounts = new int[26];
        for (Cell cell : allNewCells) {
            neededCounts[cell.letter - 'A']++;
        }

        // Verify rack can cover (including blanks).
        int blanksUsed = 0;
        int[] rackCounts = rack.getCounts();
        for (int i = 0; i < 26; i++) {
            int shortage = neededCounts[i] - rackCounts[i];
            if (shortage > 0) {
                blanksUsed += shortage;
                if (blanksUsed > rack.getBlanks()) {
                    return List.of();
                }
            }
        }

        // Decide which squares become blanks.
        Map<Cell, Boolean> isBlank = new HashMap<>();
        int blanksRemaining = rack.getBlanks();

        for (int li = 0; li < 26; li++) {
            int need = neededCounts[li];
            if (need == 0) continue;

            int have = rackCounts[li];
            int shortage = Math.max(0, need - have);
            if (shortage == 0) continue;

            char letter = (char) ('A' + li);
            List<Cell> cells = newCellsByLetter.getOrDefault(letter, List.of());
            if (cells.isEmpty()) continue;

            // Put blanks on the lowest-lost-value squares.
            cells.sort(Comparator.comparingInt(BlankAssigner::lostValue));

            for (int k = 0; k < shortage; k++) {
                if (blanksRemaining <= 0) break;
                Cell chosen = cells.get(k);
                isBlank.put(chosen, Boolean.TRUE);
                blanksRemaining--;
            }
        }

        // Build PlacedTile list.
        List<PlacedTile> out = new ArrayList<>(allNewCells.size());
        for (Cell cell : allNewCells) {
            boolean blank = isBlank.getOrDefault(cell, Boolean.FALSE);
            out.add(new PlacedTile(cell.row, cell.col, cell.letter, blank));
        }

        return out;
    }

    private static int lostValue(Cell cell) {
        int base = ScrabbleScoring.scoreLetter(cell.letter);
        return base * cell.tileType.letterMultiplier;
    }

    private static final class Cell {
        final int row;
        final int col;
        final char letter;
        final TileType tileType;

        Cell(int row, int col, char letter, TileType tileType) {
            this.row = row;
            this.col = col;
            this.letter = letter;
            this.tileType = tileType;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Cell other)) return false;
            return row == other.row && col == other.col && letter == other.letter;
        }

        @Override
        public int hashCode() {
            int h = row;
            h = 31 * h + col;
            h = 31 * h + letter;
            return h;
        }
    }
}
