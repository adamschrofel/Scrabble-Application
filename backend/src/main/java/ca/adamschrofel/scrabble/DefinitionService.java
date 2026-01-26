package ca.adamschrofel.scrabble;
import java.util.Map;

import org.springframework.stereotype.Service;


//TODO Finish class
@Service
public class DefinitionService {
    private static final String DICT_PATH = "dictionary/csw19Definitions.txt";
    private final Map<String, String> definitions;

    public DefinitionService(){
        this.definitions = DefinitionLoader.loadDefinitions(DICT_PATH);
    }

    public String getDefinition(String word){
        if (word == null) return null;
        String key = word.trim().toUpperCase();
        if (key.isEmpty()) return null;
        return definitions.get(key);
    }
    
}
