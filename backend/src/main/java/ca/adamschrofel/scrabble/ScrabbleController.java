package ca.adamschrofel.scrabble;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
public class ScrabbleController {
    private final ScrabbleService service;
    private final DefinitionService definitions;

    public ScrabbleController(ScrabbleService service, DefinitionService definitions) {
        this.service = service;
        this.definitions = definitions;
    }

    @GetMapping("/api/solve")
    public Map<String, Object> solve(@RequestParam String tiles) throws InvalidTilesException {
        String tilesNormalized = InputValidator.normalizeTiles(tiles);

        Map<String, Object> res = new LinkedHashMap<>();
        res.put("tiles", tilesNormalized);
        res.put("groups", service.solve(tilesNormalized));
        return res;
    }

    @GetMapping("/api/define")
    public Map<String, Object> define(@RequestParam String word) {
        String definition = definitions.getDefinition(word);

        Map<String, Object> res = new LinkedHashMap<>();
        res.put("word", word == null ? null : word.trim().toUpperCase());
        res.put("found", definition != null);
        res.put("definition", definition);

        return res;

    }
    
    
}
