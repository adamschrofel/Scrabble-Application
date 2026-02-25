package ca.adamschrofel.scrabble.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ca.adamschrofel.scrabble.dto.DefinitionResponse;
import ca.adamschrofel.scrabble.service.DefinitionService;

@RestController
@RequestMapping("/api/words")
public class WordController {
    private final DefinitionService definitions;

    public WordController(DefinitionService definitions) {
        this.definitions = definitions;
    }

    /**
     * Looks up a dictionary definition for a word.
     *
     * <p>
     * Route: {@code GET /api/words/{word}}
     * </p>
     *
     * @param word Word to define (path variable).
     * @return Definition payload including whether the word was found.
     */
    @GetMapping("/{word}")
    public DefinitionResponse define(@PathVariable String word) {

        String normalized = word == null ? null : word.trim().toUpperCase();
        String definition = (normalized == null) ? null : definitions.getDefinition(normalized);
        return new DefinitionResponse(normalized, definition != null, definition);
    }

}
