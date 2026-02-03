package ca.adamschrofel.scrabble;

import org.springframework.stereotype.Service;

import ca.adamschrofel.scrabble.Exceptions.InvalidTilesException;
import ca.adamschrofel.scrabble.dto.BestPlay;

import java.util.List;

/**
 * Service layer for Scrabble word solving logic.
 * Orchestrates the WordFinder algorithm to identify all valid words
 * that can be formed from a given rack of tiles.
 */
@Service
public class ScrabbleService {
    // Path to the CSW (Collins Scrabble Words) dictionary file loaded at startup
    private static final String DICT_PATH = "dictionary/csw19Words.txt";

    // WordFinder handles the core algorithm for finding playable words
    private final WordFinder wf;
    // ScrabbleScoring provides point values for tiles (currently unused)
    private final ScrabbleScoring ss;

    /**
     * Constructor that initializes the service.
     * Loads the dictionary file into memory via WordFinder constructor.
     */
    public ScrabbleService() {
        this.wf = new WordFinder(DICT_PATH);
        this.ss = new ScrabbleScoring();
    }

    /**
     * Solves a Scrabble rack by finding all valid words and grouping them by
     * length.
     * This is the main entry point for solving operations from the REST controller.
     *
     * @param rack A string of tile characters (e.g., "TRIEDEST" or "TRADE?")
     * @return A list of LengthGroup objects, each containing words of a specific
     *         length,
     *         ordered from longest to shortest for better user experience
     */
    public List<WordFinder.LengthGroup> solve(String rack) {
        // Find all playable words that can be formed from the given tiles
        List<String> playable = wf.findPlayableWords(rack);
        // Group the results by word length (e.g., all 5-letter words together)
        return wf.groupPlayableWords(playable);
    }

    public List<BestPlay> bestPlays(Board board, String rack, int limit) throws InvalidTilesException {
        MoveGenerator gen = new MoveGenerator();
        BoardLayout layout = BoardLayout.standardLayout();
        

        String tilesNormalized = InputValidator.normalizeTiles(rack);

        return gen.generateBestPlays(board, layout, this.wf, tilesNormalized, limit);
    }

}
