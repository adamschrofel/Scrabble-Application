package ca.adamschrofel.scrabble;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ca.adamschrofel.scrabble.dto.BestPlay;
import ca.adamschrofel.scrabble.dto.MoveEvaluation;
import ca.adamschrofel.scrabble.dto.PlacedTile;
import ca.adamschrofel.scrabble.dto.Placement;

public class MoveGenerator {

    /**
     * Generates legal, scored plays sorted by score descending (top N via limit).
     */
    public List<BestPlay> generateBestPlays(
            Board board,
            BoardLayout layout,
            WordFinder dictionary,
            String rack,
            int limit) {

        List<Placement> candidates = generateCandidatePlacements(board, dictionary, rack);

        List<BestPlay> plays = new ArrayList<>();
        Set<String> seen = new HashSet<>();

        for (Placement p : candidates) {
            // de-dupe identical placements
            String key = p.word() + "@" + p.row() + "," + p.column() + ":" + p.direction();
            if (!seen.add(key)) {
                continue;
            }
            MoveEvaluation eval = MoveEvaluator.evaluate(board, layout, p, dictionary);
            if (!eval.legal()) {
                continue;
            }
            List<PlacedTile> tilesPlaced = computeTilesPlaced(board, p);
            if (tilesPlaced.isEmpty()) {
                continue;
            }
            plays.add(new BestPlay(p, eval.score(), eval.wordsFormed(), tilesPlaced));
        }

        plays.sort(Comparator.comparingInt(BestPlay::score).reversed());
        if(limit > 0 && plays.size()> limit){
            return new ArrayList<>(plays.subList(0, limit));
        }
        return plays;
    }

    /**
     * Generates candidate placements in BOTH directions (ACROSS + DOWN).
     * These are candidates only — legality/scoring happens in generateBestPlays.
     */
    public List<Placement> generateCandidatePlacements(
            Board board,
            WordFinder dictionary,
            String rack) {
        List<Placement> out = new ArrayList<>();
        List<Anchor> anchors = findAnchors(board);

        String rackNorm = (rack == null) ? "" : rack.trim().toUpperCase();

        for (Anchor a : anchors) {
            generateFromAnchor(board, dictionary, rackNorm, a, Direction.ACROSS, out);
            generateFromAnchor(board, dictionary, rackNorm, a, Direction.DOWN, out);
        }

        return out;
    }

    /*
     * ======================
     * PRIVATE ENGINE HELPERS
     * ======================
     */

    private List<Anchor> findAnchors(Board board) {
        List<Anchor> anchors = new ArrayList<>();

        if (board.isEmpty()) {
            anchors.add(new Anchor(7, 7));
            return anchors;
        }

        for (int r = 0; r < Board.SIZE; r++) {
            for (int c = 0; c < Board.SIZE; c++) {
                if (board.getTile(r, c) != '.')
                    continue;

                if (hasTileAt(board, r - 1, c)
                        || hasTileAt(board, r + 1, c)
                        || hasTileAt(board, r, c - 1)
                        || hasTileAt(board, r, c + 1)) {
                    anchors.add(new Anchor(r, c));
                }
            }
        }
        return anchors;
    }

    private void generateFromAnchor(
            Board board,
            WordFinder dictionary,
            String rack,
            Anchor anchor,
            Direction dir,
            List<Placement> out) {
        int ar = anchor.row();
        int ac = anchor.col();

        int minStartRow = ar;
        int minStartCol = ac;

        if (dir == Direction.ACROSS) {
            while (minStartCol > 0 && board.getTile(ar, minStartCol - 1) == '.') {
                minStartCol--;
            }
        } else { // DOWN
            while (minStartRow > 0 && board.getTile(minStartRow - 1, ac) == '.') {
                minStartRow--;
            }
        }

        if (dir == Direction.ACROSS) {
            for (int startCol = minStartCol; startCol <= ac; startCol++) {
                int maxLenByBounds = Math.min(15, Board.SIZE - startCol);
                int minLenToCoverAnchor = Math.max(2, (ac - startCol) + 1);

                for (int len = minLenToCoverAnchor; len <= maxLenByBounds; len++) {
                    for (String word : dictionary.wordsOfLength(len)) {
                        if (fitsBoardAndRack(board, rack, ar, startCol, dir, word)) {
                            out.add(new Placement(word, ar, startCol, dir));
                        }
                    }
                }
            }
        } else { // DOWN
            for (int startRow = minStartRow; startRow <= ar; startRow++) {
                int maxLenByBounds = Math.min(15, Board.SIZE - startRow);
                int minLenToCoverAnchor = Math.max(2, (ar - startRow) + 1);

                for (int len = minLenToCoverAnchor; len <= maxLenByBounds; len++) {
                    for (String word : dictionary.wordsOfLength(len)) {
                        if (fitsBoardAndRack(board, rack, startRow, ac, dir, word)) {
                            out.add(new Placement(word, startRow, ac, dir));
                        }
                    }
                }
            }
        }
    }

    private boolean fitsBoardAndRack(Board board, String rack, int row, int col, Direction dir, String word) {
        String w = word.toUpperCase();
        int len = w.length();

        if (dir == Direction.ACROSS) {
            if (col < 0 || col + len > Board.SIZE)
                return false;
        } else {
            if (row < 0 || row + len > Board.SIZE)
                return false;
        }

        Map<Character, Integer> counts = rackCounts(rack);
        int blanks = counts.getOrDefault('?', 0) + counts.getOrDefault('*', 0);

        for (int i = 0; i < len; i++) {
            int r = (dir == Direction.ACROSS) ? row : row + i;
            int c = (dir == Direction.ACROSS) ? col + i : col;

            char needed = w.charAt(i);
            char existing = board.getTile(r, c);

            if (existing != '.') {
                if (existing != needed)
                    return false;
                continue;
            }

            int have = counts.getOrDefault(needed, 0);
            if (have > 0) {
                counts.put(needed, have - 1);
            } else if (blanks > 0) {
                blanks--;
            } else {
                return false;
            }
        }

        return true;
    }

    /**
     * Compute which tiles would be newly placed for this placement (without
     * mutating board).
     * This is UI-critical (so we can highlight the squares to place).
     */
    private List<PlacedTile> computeTilesPlaced(Board board, Placement placement) {
        List<PlacedTile> tiles = new ArrayList<>();

        int dr = placement.direction().directionRow;
        int dc = placement.direction().directionColumn;

        String w = placement.word().toUpperCase();

        int r = placement.row();
        int c = placement.column();

        for (int i = 0; i < w.length(); i++) {
            char existing = board.getTile(r, c);
            if (existing == '.') {
                tiles.add(new PlacedTile(r, c, w.charAt(i)));
            }
            r += dr;
            c += dc;
        }

        return tiles;
    }

    private Map<Character, Integer> rackCounts(String rack) {
        Map<Character, Integer> m = new HashMap<>();
        for (int i = 0; i < rack.length(); i++) {
            char ch = Character.toUpperCase(rack.charAt(i));
            if (ch == ' ')
                continue;
            m.put(ch, m.getOrDefault(ch, 0) + 1);
        }
        return m;
    }

    private boolean hasTileAt(Board board, int row, int col) {
        if (row < 0 || row >= Board.SIZE || col < 0 || col >= Board.SIZE)
            return false;
        return board.getTile(row, col) != '.';
    }

    private static record Anchor(int row, int col) {
    }
}
