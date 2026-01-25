package ca.adamschrofel.scrabble;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

//TODO finish class

public final class DefinitionLoader {

    private DefinitionLoader() {
    }

    public static Map<String, String> load(String path) {
        Map<String, String> map = new HashMap<>();

        InputStream in = DefinitionLoader.class.getResourceAsStream("/" + path);
        if (in == null) {
            throw new RuntimeException("Cant find definitions" + path);
        }

        try (Scanner sc = new Scanner(in)) {
            while (sc.hasNextLine()) {
                String line = sc.nextLine().trim();
                if (line.isEmpty())
                    continue;
            }
        }
        return map;
    }

}
