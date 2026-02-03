package ca.adamschrofel.scrabble;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ca.adamschrofel.scrabble.Exceptions.InvalidTilesException;
import ca.adamschrofel.scrabble.dto.BestPlay;
import ca.adamschrofel.scrabble.dto.BoardResponse;
import ca.adamschrofel.scrabble.dto.DefinitionResponse;
import ca.adamschrofel.scrabble.dto.ScoreWord;
import ca.adamschrofel.scrabble.dto.SetTileRequest;
import ca.adamschrofel.scrabble.dto.SetTilesRequest;
import ca.adamschrofel.scrabble.dto.SolveResponse;
import ca.adamschrofel.scrabble.dto.WordGroup;

import java.util.*;

/**
 * REST controller that handles HTTP requests for word solving and word
 * definitions.
 * Exposes two endpoints: /api/solve for finding valid Scrabble words from
 * tiles,
 * and /api/define for retrieving word definitions from the CSW dictionary.
 */
@RestController
public class ScrabbleController {
    private final ScrabbleService service;
    private final DefinitionService definitions;
    private final BoardService board;

    /**
     * Constructor that injects dependencies via Spring autowiring.
     * ScrabbleService handles word finding logic and DefinitionService
     * provides word definitions.
     *
     * @param service     The service for solving Scrabble words from a tile rack
     * @param definitions The service for retrieving word definitions
     */
    public ScrabbleController(ScrabbleService service, DefinitionService definitions, BoardService board) {
        this.service = service;
        this.definitions = definitions;
        this.board = board;
    }

    /**
     * Finds all valid Scrabble words that can be formed from a given set of tiles.
     * Accepts tile input including blank tiles (? or *) and returns results grouped
     * by word length.
     *
     * @param tiles A string of tile characters (A-Z, ?, or *) representing the
     *              player's rack
     * @return A map containing the normalized tiles and groups of words organized
     *         by length
     * @throws InvalidTilesException If the input contains invalid characters or
     *                               exceeds 15 tiles
     */
    @GetMapping("/api/solve")
    public SolveResponse solve(@RequestParam String tiles) throws InvalidTilesException {
        // Validate and normalize the tile input (uppercase, no spaces, check for valid
        // chars)
        String tilesNormalized = InputValidator.normalizeTiles(tiles);

        List<WordGroup> groups = new ArrayList<>();

        for (WordFinder.LengthGroup g : service.solve(tilesNormalized)) {
            List<ScoreWord> scored = new ArrayList<>();
            for (String w : g.words) {
                scored.add(new ScoreWord(w, ScrabbleScoring.scoreWord(w)));
            }
            groups.add(new WordGroup(g.length, scored));
        }
        return new SolveResponse(tilesNormalized, groups);

    }

    /**
     * Retrieves the definition of a given word from the CSW dictionary.
     * Returns whether the word was found and its definition if available.
     *
     * @param word The word
     * @return A map containing the word, whether it was found, and its definition
     *         (or null if not found)
     */
    @GetMapping("/api/define")
    public DefinitionResponse define(@RequestParam String word) {
        // Look up the word in the definitions dictionary

        String normalized = word == null ? null : word.trim().toUpperCase();
        String definition = (normalized == null) ? null : definitions.getDefinition(normalized);
        return new DefinitionResponse(normalized, definition != null, definition);
    }

    @GetMapping("/api/bestplays")
    public List<BestPlay> bestPlays(
            @RequestParam String tiles,
            @RequestParam(defaultValue = "25") int limit) throws InvalidTilesException {
        Board b = board.setupStandardBoard();

        // Delegate to service (which knows dictionary/layout)
        return service.bestPlays(b, tiles, limit);
    }

    @GetMapping("/api/board")
    public BoardResponse getBoard() {
        return new BoardResponse(BoardService.SIZE, board.rowsAsStrings());
    }

    @PostMapping("/api/board/reset")
    public BoardResponse resetBoard() {
        board.reset();
        return new BoardResponse(BoardService.SIZE, board.rowsAsStrings());
    }

    @PostMapping("/api/board/tile")
    public BoardResponse setTile(@RequestBody SetTileRequest req) {
        char tile = normalizeTile(req.tile());
        board.set(req.row(), req.column(), tile);
        return new BoardResponse(BoardService.SIZE, board.rowsAsStrings());
    }

    @PostMapping("/api/board/tiles")
    public BoardResponse setTiles(@RequestBody SetTilesRequest req) {
        if (req.tiles() != null) {
            for (SetTileRequest t : req.tiles()) {
                char tile = normalizeTile(t.tile());
                board.set(t.row(), t.column(), tile);
            }
        }
        return new BoardResponse(BoardService.SIZE, board.rowsAsStrings());
    }

    private char normalizeTile(String tile) {
        if (tile == null)
            return '.';
        String s = tile.trim();
        if (s.isEmpty() || s.equals("."))
            return '.';
        char ch = Character.toUpperCase(s.charAt(0));
        return ch;
    }

}
