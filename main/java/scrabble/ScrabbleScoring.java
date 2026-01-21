
public final class ScrabbleScoring {
    private ScrabbleScoring() {
    }

    private static final int[] SCORE = new int[26];

    static {
        setTileScore("AEILNORSTU", 1);
        setTileScore("DG", 2);
        setTileScore("BCMP", 3);
        setTileScore("FHVWY", 4);
        setTileScore("K", 5);
        setTileScore("JX", 8);
        setTileScore("QZ", 10);
    }

    private static void setTileScore(String letters, int value) {
        for (int i = 0; i < letters.length(); i++) {
            char c = letters.charAt(i);
            SCORE[c - 'A'] = value;
        }
    }

    public static int getTileScore(char c) {
        return SCORE[c - 'A'];
    }
}