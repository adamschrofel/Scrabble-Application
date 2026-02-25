package ca.adamschrofel.scrabble.solver;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ca.adamschrofel.scrabble.board.Board;
import ca.adamschrofel.scrabble.board.BoardLayout;
import ca.adamschrofel.scrabble.board.Direction;
import ca.adamschrofel.scrabble.dictionary.TrieDictionary;
import ca.adamschrofel.scrabble.dictionary.TrieNode;
import ca.adamschrofel.scrabble.dto.BestPlay;
import ca.adamschrofel.scrabble.dto.MoveEvaluation;
import ca.adamschrofel.scrabble.dto.Placement;
import ca.adamschrofel.scrabble.rack.Rack;

/**
 * High-level move generation for the board solver.
 *
 * <p>Generates candidate placements using a trie-backed dictionary and then
 * evaluates, filters, and sorts them into best plays.
 */
public class MoveGenerator {

    private final AnchorFinder anchorFinder = new AnchorFinder();
    private final CrossCheckCalculator crossCheckCalculator = new CrossCheckCalculator();
    private final TriePlacementGenerator triePlacementGenerator = new TriePlacementGenerator();

    /**
     * Generates the best legal plays for the current board state.
     *
     * <p>Pipeline:
     * <ol>
     *   <li>Generate candidate {@link Placement}s using the dictionary trie (both directions).</li>
     *   <li>Evaluate each candidate for legality + score (including cross-words).</li>
     *   <li>Sort by score descending and return the top {@code limit} results.</li>
     * </ol>
     */
    public List<BestPlay> generateBestPlays(
            Board board,
            BoardLayout layout,
            TrieDictionary dictionary,
            String rack,
            int limit) {
        String normalizedRack = normalizeRack(rack);
        Rack rackInfo = Rack.parseRack(normalizedRack);

        List<Placement> candidatePlacements = generateCandidatePlacements(board, dictionary, rackInfo);

        // Evaluate candidates fully (board legality, scoring, crosswords).
        List<BestPlay> plays = new ArrayList<>();
        Set<Placement> seenPlacements = new HashSet<>();
        for (Placement placement : candidatePlacements) {
            // De-dupe identical placements.
            if (!seenPlacements.add(placement)) {
                continue;
            }

            MoveEvaluation evaluation = MoveEvaluator.evaluate(board, layout, placement, dictionary, rackInfo);
            if (!evaluation.legal()) {
                continue;
            }

            // Candidate generation should already guarantee at least one rack tile is used,
            // but keep this as a defensive check.
            if (evaluation.tilesPlaced().isEmpty()) {
                continue;
            }

            plays.add(new BestPlay(placement, evaluation.score(), evaluation.wordsFormed(), evaluation.tilesPlaced()));
        }

        plays.sort(Comparator.comparingInt(BestPlay::score).reversed());

        if (limit > 0 && plays.size() > limit) {
            return new ArrayList<>(plays.subList(0, limit));
        }
        return plays;
    }

    /**
     * Generates candidate placements in BOTH directions (ACROSS + DOWN).
     * These are candidates only; legality/scoring happens in {@link #generateBestPlays}.
     */
    public List<Placement> generateCandidatePlacements(Board board, TrieDictionary dictionary, String rack) {
        Rack rackInfo = Rack.parseRack(normalizeRack(rack));
        return generateCandidatePlacements(board, dictionary, rackInfo);
    }

    private List<Placement> generateCandidatePlacements(Board board, TrieDictionary dictionary, Rack rackInfo) {
        List<Placement> placements = new ArrayList<>();

        List<AnchorFinder.Anchor> anchors = anchorFinder.findAnchors(board);

        // Cross-check pruning: for each empty square, precompute which letters are allowed
        // there based on the perpendicular word that would be formed.
        TrieNode trieRoot = dictionary.root();

        BitSet[][] crossChecksAcross = crossCheckCalculator.compute(board, trieRoot, Direction.ACROSS);
        BitSet[][] crossChecksDown = crossCheckCalculator.compute(board, trieRoot, Direction.DOWN);

        for (AnchorFinder.Anchor a : anchors) {
            triePlacementGenerator.generateFromAnchor(board, trieRoot, rackInfo, a, Direction.ACROSS, crossChecksAcross, placements);
            triePlacementGenerator.generateFromAnchor(board, trieRoot, rackInfo, a, Direction.DOWN, crossChecksDown, placements);
        }

        return placements;
    }

    private static String normalizeRack(String rack) {
        return (rack == null) ? "" : rack.trim().toUpperCase();
    }
}
