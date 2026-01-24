package ca.adamschrofel.scrabble;

import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ScrabbleService {
    private static final String DICT_PATH = "dictionary/csw19Words.txt";
    private final WordFinder wf;

    public ScrabbleService() {
        this.wf = new WordFinder(DICT_PATH);
    }

    public List<WordFinder.LengthGroup> solve(String rack) {
        List<String> playable = wf.findPlayableWords(rack);
        return wf.groupPlayableWords(playable);
    }

}
