import java.util.*;
import java.io.*;

public class WordFinder {
    private final List<String>[] wordsByLength;

    public WordFinder(String wordListPath) {
        this.wordsByLength = (List<String>[])new List[16];
        for (int i = 0; i < wordsByLength.length; i++){
            wordsByLength[i] = new ArrayList<>();
        }
        loadWordList(wordListPath);
    }

    private static List<String> loadWordList(String path) {
        ArrayList<String> list = new ArrayList<>();

        try (Scanner kb = new Scanner(new File(path))) {
            while (kb.hasNextLine()) {
                String line = kb.nextLine().trim();
                if (line.isEmpty())
                    continue;

                String word = line.toUpperCase();
                list.add(word);
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Could not find word list file: " + path, e);
        }
        return list;
    }

    public List<String> findPlayableWords()

    public List<String> getWords(){
        return words;
    }

    

}