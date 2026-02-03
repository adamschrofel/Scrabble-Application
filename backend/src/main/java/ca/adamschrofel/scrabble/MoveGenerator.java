package ca.adamschrofel.scrabble;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Comparator;
import java.util.List;
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
        long candidatesStart = 0L;
        long evaluationStart = 0L;

        candidatesStart = System.nanoTime();
        
        // Generates Candidate Placements
        List<Placement> candidates = generateCandidatePlacements(board, dictionary, rack);

        evaluationStart = System.nanoTime();
        System.out.println(
                "Timing - generateCandidatePlacements: " + formatDuration(evaluationStart - candidatesStart));
        // evaluate candidates fully (Board legality, scoring, corsswords)
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

        long end = System.nanoTime();
        System.out.println("Timing - evaluateCandidates: " + formatDuration(end - evaluationStart));
        System.out.println("Timing - totalGenerateBestPlays: " + formatDuration(end - candidatesStart));

        if (limit > 0 && plays.size() > limit) {
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
        RackInfo rackInfo = RackInfo.fromRack(rackNorm);
        for (Anchor a : anchors) {
            generateFromAnchor(board, dictionary, rackInfo, a, Direction.ACROSS, out);
            generateFromAnchor(board, dictionary, rackInfo, a, Direction.DOWN, out);
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
            RackInfo rackInfo,
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
                    char[] pattern = buildPattern(board, ar, startCol, dir, len);
                    for (String word : dictionary.wordsOfLength(len)) {
                        if (!matchesPattern(word, pattern)){
                            continue;
                        }
                        if (fitsRackWithPattern(rackInfo, word, pattern)) {
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
                    char[] pattern = buildPattern(board, startRow, ac, dir, len);
                    for (String word : dictionary.wordsOfLength(len)) {
                        if (!matchesPattern(word, pattern)){
                            continue;
                        }
                        if (fitsRackWithPattern(rackInfo, word, pattern)) {
                            out.add(new Placement(word, startRow, ac, dir));
                        }
                    }
                }
            }
        }
    }

   /**
     * Checks if the rack can supply the letters needed for the '.' slots in the pattern.
     * Fixed letters are already enforced by matchesPattern, so we only count blanks/letters
     * for empty board squares.
     */
    private boolean fitsRackWithPattern(RackInfo rackInfo, String word, char[] pattern) {
        String w = word.toUpperCase();
        int len = pattern.length;
        int[] neededCounts = new int[26];

        for (int i = 0; i < len; i++) {
            if (pattern[i] != '.') {
                continue; // fixed board letter, no rack tile needed
            }
            char neededChar = w.charAt(i);
            neededCounts[neededChar - 'A']++;
        }

        int blanksUsed = 0;
        for (int i = 0; i < 26; i++) {
            int shortage = neededCounts[i] - rackInfo.counts[i];
            if (shortage > 0) {
                blanksUsed += shortage;
                if (blanksUsed > rackInfo.blanks) {
                    return false;
                }
            }
        }
        return blanksUsed <= rackInfo.blanks;
    }
    /**
     * Builds a fixed-letter pattern ('.' for empty, letter for filled) along a span.
     */
    private char[] buildPattern(Board board, int row, int col, Direction dir, int len) {
        char[] pattern = new char[len];
        int r = row;
        int c = col;
        int dr = dir.directionRow;
        int dc = dir.directionColumn;

        for (int i = 0; i < len; i++) {
            char existing = board.getTile(r, c);
            pattern[i] = existing == '.' ? '.' : existing;
            r += dr;
            c += dc;
        }
        return pattern;
    }
    /**
     * Checks whether a dictionary word matches all fixed letters in the pattern.
     */
    private boolean matchesPattern(String word, char[] pattern) {
        String w = word.toUpperCase();
        int len = pattern.length;
        for (int i = 0; i < len; i++) {
            char expected = pattern[i];
            if (expected != '.' && w.charAt(i) != expected) {
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

    private static final class RackInfo {
        private final int[] counts;
        private final int blanks;

        private RackInfo(int[] counts, int blanks) {
            this.counts = counts;
            this.blanks = blanks;
        }

        private static RackInfo fromRack(String rack) {
            int[] counts = new int[26];
            int blanks = 0;
            for (int i = 0; i < rack.length(); i++) {
                char ch = Character.toUpperCase(rack.charAt(i));
                if (ch == ' ') {
                    continue;
                }
                if (ch == '?' || ch == '*') {
                    blanks++;
                    continue;
                }
                if (ch >= 'A' && ch <= 'Z') {
                    counts[ch - 'A']++;
                }
            }
            return new RackInfo(counts, blanks);
        }
    }

    private boolean hasTileAt(Board board, int row, int col) {
        if (row < 0 || row >= Board.SIZE || col < 0 || col >= Board.SIZE)
            return false;
        return board.getTile(row, col) != '.';
    }

    private static record Anchor(int row, int col) {
    }

    private String formatDuration(long nanos) {
        double ms = nanos / 1_000_000.0;
        return String.format("%.2f ms", ms);
    }
}
