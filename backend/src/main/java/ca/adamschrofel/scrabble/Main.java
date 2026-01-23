package ca.adamschrofel.scrabble;
import java.util.*;

public class Main {

        private static final String DICT_PATH = "dictionary/csw19Words.txt";

        public static List<WordFinder.LengthGroup> solve (String rack) throws Exception{
            WordFinder wf = new WordFinder(DICT_PATH);
            List<String> playable = wf.findPlayableWords(rack);
            return wf.groupPlayableWords(playable);
        }

    public static void main(String[] args) throws Exception {

        System.out.println("Working dir: " + new java.io.File(".").getAbsolutePath());

        Scanner sc = new Scanner(System.in);
        System.out.print("Enter tiles (max 15, ?/* = blank): ");
        String rack = sc.nextLine();
        sc.close();
       
        List<WordFinder.LengthGroup> groups = solve(rack);

        for (WordFinder.LengthGroup g : groups) {
            System.out.println("\n" + g.length + "-letter words:");
            for (String w : g.words) {
                System.out.println("  " + w);
            }

        }
    }
}
