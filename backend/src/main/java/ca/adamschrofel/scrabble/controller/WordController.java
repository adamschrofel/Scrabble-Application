package ca.adamschrofel.scrabble.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ca.adamschrofel.scrabble.dto.DefinitionResponse;
import ca.adamschrofel.scrabble.service.DefinitionService;

@RestController
@RequestMapping("/api")
public class WordController {
    private final DefinitionService definitions;

    public WordController(DefinitionService definitions){
        this.definitions = definitions;
    }

    /**
     * Retrieves the definition of a given word from the CSW dictionary.
     * Returns whether the word was found and its definition if available.
     *
     * @param word The word
     * @return A map containing the word, whether it was found, and its definition
     *         (or null if not found)
     */
    /**
     * Dictionary definition lookup.
     *
     * <p>
     * Primary route: {@code GET /api/word/define?word=...}
     * (Alias: {@code GET /api/define?word=...})
     * </p>
     */
    @GetMapping("/words/{word}")
    public DefinitionResponse define(@PathVariable String word) {
       
        String normalized = word == null ? null : word.trim().toUpperCase();
        String definition = (normalized == null) ? null : definitions.getDefinition(normalized);
        return new DefinitionResponse(normalized, definition != null, definition);
    }

    // Legacy alias 
    @GetMapping("/define")
    public DefinitionResponse defineLegacy(@RequestParam String word) {
        return define(word);
    }
    
}
