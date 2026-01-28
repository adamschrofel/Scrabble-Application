package ca.adamschrofel.scrabble;
import java.util.Map;

import org.springframework.stereotype.Service;

/**
 * Service layer that provides word definitions from the CSW (Collins Scrabble Words) dictionary.
 * Loads the entire definitions file into memory as a HashMap at startup. O(1) lookups
 */
@Service
public class DefinitionService {
    // Path to the definition file
    private static final String DICT_PATH = "dictionary/csw19Definitions.txt";
    // HashMap for definitions
    private final Map<String, String> definitions;

    /**
     * Constructor loads definitions from the dictionary file.
     */
    public DefinitionService(){
        this.definitions = DefinitionLoader.loadDefinitions(DICT_PATH);
    }

    /**
     * Retrieves the definition of a given word from dictionary.
     * Handles null input and performs case-insensitive lookup.
     *
     * @param word The word to look up (case-insensitive)
     * @return The definition string if the word is found, or null if not found or input is null/empty
     */
    public String getDefinition(String word){
        // return null if input is null
        if (word == null) return null;
        
        // trims whitespace and converts to uppercase 
        String key = word.trim().toUpperCase();
        
        // Rejects empty strings 
        if (key.isEmpty()) return null;
        
        // Look up in the HashMap and returns the definition (or null if its not found)
        return definitions.get(key);
    }
    
}
