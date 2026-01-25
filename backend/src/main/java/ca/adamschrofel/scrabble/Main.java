package ca.adamschrofel.scrabble;

import java.util.*;

public class Main {
    public static void main(String[] args) throws Exception {

        System.out.println("Working dir: " + new java.io.File(".").getAbsolutePath());

        Scanner sc = new Scanner(System.in);
        System.out.print("Enter tiles (max 15, ?/* = blank): ");
        String rack = sc.nextLine();
        sc.close();
        String tiles = "";
        try {
            tiles = InputValidator.normalizeTiles(rack);
        } catch (InvalidTilesException e) {
            System.out.println(e.getMessage());
        }

        ScrabbleService service = new ScrabbleService();
        List<WordFinder.LengthGroup> groups = service.solve(tiles);

        for (WordFinder.LengthGroup g : groups) {
            System.out.println("\n" + g.length + "-letter words:");
            for (String w : g.words) {
                System.out.println("  " + w);
            }

        }
    }
}
