package ca.adamschrofel.scrabble.service;

import org.springframework.stereotype.Service;

import ca.adamschrofel.scrabble.InputValidator;
import ca.adamschrofel.scrabble.board.Board;
import ca.adamschrofel.scrabble.board.BoardLayout;
import ca.adamschrofel.scrabble.dictionary.TrieDictionary;
import ca.adamschrofel.scrabble.dictionary.WordListDictionary;
import ca.adamschrofel.scrabble.dto.BestPlay;
import ca.adamschrofel.scrabble.exceptions.InvalidTilesException;
import ca.adamschrofel.scrabble.rack.LengthGroup;
import ca.adamschrofel.scrabble.rack.RackWordFinder;
import ca.adamschrofel.scrabble.solver.MoveGenerator;

import java.util.List;

/**
 * Core application service for Scrabble solving.
 *
 * <p>Provides:</p>
 * <ul>
 *   <li>Rack-only solving (anagrams by length)</li>
 *   <li>Board solving (best plays for a given board + rack)</li>
 * </ul>
 */
@Service
public class ScrabbleService {
    private static final String DICT_PATH = "dictionary/csw19Words.txt";

    private final TrieDictionary dictionary;
    private final RackWordFinder rackWordFinder;
    private final MoveGenerator moveGenerator;
    private final BoardLayout standardLayout;

    public ScrabbleService() {
        this.dictionary = new WordListDictionary(DICT_PATH);
        this.rackWordFinder = new RackWordFinder();
        this.moveGenerator = new MoveGenerator();
        this.standardLayout = BoardLayout.standardLayout();
    }

    /**
     * Solves a Scrabble rack by finding all valid words and grouping them by.
     * length.
     * This is the main entry point for solving operations from the REST controller.
     *
     * @param rack A string of tile characters (e.g., "TRIEDEST" or "TRADE?")
     * @return A list of LengthGroup objects, each containing words of a specific
     *         length,
     *         ordered from longest to shortest for better user experience
     */
    public List<LengthGroup> solve(String rack) {
        List<String> playable = rackWordFinder.findPlayableWords(rack, dictionary);
        return rackWordFinder.groupPlayableWords(playable);
    }

    public List<BestPlay> bestPlays(Board board, String rack, int limit) throws InvalidTilesException {
        String tilesNormalized = InputValidator.normalizeTiles(rack);

        return moveGenerator.generateBestPlays(board, standardLayout, dictionary, tilesNormalized, limit);
    }

}
