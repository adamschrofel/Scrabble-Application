package ca.adamschrofel.scrabble;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * REST controller that handles HTTP requests for word solving and word definitions.
 * Exposes two endpoints: /api/solve for finding valid Scrabble words from tiles,
 * and /api/define for retrieving word definitions from the CSW dictionary.
 */
@RestController
public class ScrabbleController {
    private final ScrabbleService service;
    private final DefinitionService definitions;

    /**
     * Constructor that injects dependencies via Spring autowiring.
     * ScrabbleService handles word finding logic and DefinitionService
     * provides word definitions.
     *
     * @param service The service for solving Scrabble words from a tile rack
     * @param definitions The service for retrieving word definitions
     */
    public ScrabbleController(ScrabbleService service, DefinitionService definitions) {
        this.service = service;
        this.definitions = definitions;
    }

    /**
     * Finds all valid Scrabble words that can be formed from a given set of tiles.
     * Accepts tile input including blank tiles (? or *) and returns results grouped by word length.
     *
     * @param tiles A string of tile characters (A-Z, ?, or *) representing the player's rack
     * @return A map containing the normalized tiles and groups of words organized by length
     * @throws InvalidTilesException If the input contains invalid characters or exceeds 15 tiles
     */
    @GetMapping("/api/solve")
    public Map<String, Object> solve(@RequestParam String tiles) throws InvalidTilesException {
        // Validate and normalize the tile input (uppercase, no spaces, check for valid chars)
        String tilesNormalized = InputValidator.normalizeTiles(tiles);

        // Build the response object with tiles and grouped word results
        Map<String, Object> res = new LinkedHashMap<>();
        res.put("tiles", tilesNormalized);

        // Convert WordFinder.LengthGroup (which contains List<String> words)
        // into API-friendly groups where each word includes its score
        List<Map<String, Object>> apiGroups = new ArrayList<>();
        for (WordFinder.LengthGroup g : service.solve(tilesNormalized)) {
            Map<String, Object> groupMap = new LinkedHashMap<>();
            groupMap.put("length", g.length);

            // Create a list of WordScore DTOs (word + score)
            List<ScoreWord> scoredWords = new ArrayList<>();
            for (String w : g.words) {
                int score = ScrabbleScoring.scoreWord(w);
                // Use the ScrabbleScoring helper to ensure scoring logic is centralized
                scoredWords.add(new ScoreWord(w, score));
            }

            groupMap.put("words", scoredWords);
            apiGroups.add(groupMap);
        }

        res.put("groups", apiGroups);
        return res;
    }

    /**
     * Retrieves the definition of a given word from the CSW dictionary.
     * Returns whether the word was found and its definition if available.
     *
     * @param word The word 
     * @return A map containing the word, whether it was found, and its definition (or null if not found)
     */
    @GetMapping("/api/define")
    public Map<String, Object> define(@RequestParam String word) {
        // Look up the word in the definitions dictionary
        String definition = definitions.getDefinition(word);

        // Build the response object with definition results
        Map<String, Object> res = new LinkedHashMap<>();
        res.put("word", word == null ? null : word.trim().toUpperCase());
        res.put("found", definition != null);
        res.put("definition", definition);

        return res;
    }
}
