package ca.adamschrofel.scrabble;
import java.util.*;

public class Main {
    public static void main(String[] args) throws Exception {
        System.out.println("Starting...");
        System.out.println("Working dir: " + new java.io.File(".").getAbsolutePath());

        String path = "main/dictionary/csw19Words.txt";
        System.out.println("Loading dictionary: " + path);

        WordFinder wf = new WordFinder(path);

        Scanner sc = new Scanner(System.in);
        System.out.print("Enter tiles (max 15, ?/* = blank): ");
        String rack = sc.nextLine();
        sc.close();
        List<String> playable = wf.findPlayableWords(rack);
        List<WordFinder.LengthGroup> groups = wf.groupPlayableWords(playable);

        for (WordFinder.LengthGroup g : groups) {
            System.out.println("\n" + g.length + "-letter words:");
            for (String w : g.words) {
                System.out.println("  " + w);
            }

        }
    }
}
