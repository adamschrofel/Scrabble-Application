package ca.adamschrofel.scrabble;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Class for loading word definitions from dictionary files.
 * Reads dictionary with definitions file and populates a hashmap with 
 * its contents
 * 
 */
public final class DefinitionLoader {

    // Suppress default constructor
    private DefinitionLoader() {
    }

    /**
     * Loads word definitions from dictionary file on the classpath within /resources.
     * Processing:
     * 1. Loads the file from classpath resources
     * 2. Reads each line and splits on tab character
     * 3. Converts words to uppercase for case-insensitive lookups
     * 4. Skips empty lines and bad entries
     * 5. Returns a HashMap for word lookup
     *
     * @param path The path to the definition file: dictionary/csw19Definitions.txt
     * @return A HashMap mapping word strings to their definitions (case-insensitive)
     * @throws RuntimeException If the definition file cannot be found on the classpath
     */
    public static Map<String, String> loadDefinitions(String path) {
        // HashMap that will store the word->definition mappings
        Map<String, String> map = new HashMap<>();

        // Load the file from classpath resources
        InputStream in = DefinitionLoader.class.getResourceAsStream("/" + path);
        
        // If the file doesn't exist on the classpath, throw error
        if (in == null) {
            throw new RuntimeException("Cant find definitions " + path);
        }

        // Initialize scanner to read through file
        try (Scanner sc = new Scanner(in)) {

            while (sc.hasNextLine()) {
                
                // Read and trim the current line
                String line = sc.nextLine().trim();
                // Skip empty lines
                if (line.isEmpty())
                    continue;

                // Find the tab character that separates word from definition
                int tab = line.indexOf('\t');
                // Skip if no tab found or tab is at the beginning (invalid format)
                if (tab<=0) continue;

                // Extract the word (before the tab) and convert to uppercase for case-insensitive lookups
                String word = line.substring(0, tab).trim().toUpperCase();
                // Extract the definition (after the tab)
                String def = line.substring(tab + 1).trim();

                // Only add the entry if both word and definition exist
                if (!word.isEmpty() && !def.isEmpty()){
                    map.put(word, def);
                }
            }
        }
        
        // Return the populated HashMap with all valid definitions
        return map;
    }
}
