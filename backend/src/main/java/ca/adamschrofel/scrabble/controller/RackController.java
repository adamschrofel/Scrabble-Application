package ca.adamschrofel.scrabble.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ca.adamschrofel.scrabble.InputValidator;
import ca.adamschrofel.scrabble.dto.ScoreWord;
import ca.adamschrofel.scrabble.dto.SolveResponse;
import ca.adamschrofel.scrabble.dto.WordGroup;
import ca.adamschrofel.scrabble.exceptions.InvalidTilesException;
import ca.adamschrofel.scrabble.rack.LengthGroup;
import ca.adamschrofel.scrabble.scoring.ScrabbleScoring;
import ca.adamschrofel.scrabble.service.ScrabbleService;

@RestController
@RequestMapping("/api/rack")
public class RackController {
    private final ScrabbleService service;

    public RackController(ScrabbleService service) {
        this.service = service;
    }

    /**
     * Finds all valid Scrabble words that can be formed from a given set of tiles.
     * Accepts tile input including blank tiles (? or *) and returns results grouped
     * by word length. Rack only solver.
     * <p>
     * Route: {@code GET /api/rack/solve?tiles=...}
     * </p>
     * 
     * @param rack A string of tile characters (A-Z, ?, or *) representing the
     *              player's rack
     * @return A map containing the normalized tiles and groups of words organized
     *         by length
     * @throws InvalidTilesException If the input contains invalid characters or
     *                               exceeds 15 tiles
     */
    @GetMapping("/solve")
    public SolveResponse solveRack(@RequestParam String rack) throws InvalidTilesException {
        // Validate and normalize the tile input (uppercase, no spaces, check for valid
        // chars)
        String normalized = InputValidator.normalizeTiles(rack);

        List<WordGroup> groups = new ArrayList<>();

        for (LengthGroup g : service.solve(normalized)) {
            List<ScoreWord> scored = new ArrayList<>();
            for (String w : g.words()) {
                scored.add(new ScoreWord(w, ScrabbleScoring.scoreWord(w)));
            }
            groups.add(new WordGroup(g.length(), scored));
        }
        return new SolveResponse(normalized, groups);

    }

}
