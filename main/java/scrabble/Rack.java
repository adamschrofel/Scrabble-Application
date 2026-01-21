
public class Rack {

    private final int[] counts;
    private final int blanks;
    private final int totalTiles;

    private Rack(int[] counts, int blanks, int totalTiles) {
        this.counts = counts;
        this.blanks = blanks;
        this.totalTiles = totalTiles;
    }

    public static Rack parseRack(String tiles) {
        if (tiles == null) {
            throw new IllegalArgumentException("Rack cannot be empty");
        }
        String rack = tiles.trim().toUpperCase();
        int[] counts = new int[26];
        int blanks = 0;
        int totalTiles = 0;

        for (int i = 0; i < rack.length(); i++) {
            char c = rack.charAt(i);

            if (c >= 'A' && c <= 'Z') {
                counts[c - 'A']++;
                totalTiles++;
            } else if (c == '?' || c == '*') {
                blanks++;
                totalTiles++;
            } else if (Character.isWhitespace(c)) {
                continue;
            } else {
                throw new IllegalArgumentException(
                        "Invalid tile entered" + c);
            }
            if (totalTiles > 15) {
                throw new IllegalArgumentException(
                        "15 tiles max");
            }

        }
        return new Rack(counts, blanks, totalTiles);
    }

    public int[] getCounts() {
        return counts;
    }

    public int getBlanks() {
        return blanks;
    }

    public int getTotalTiles() {
        return totalTiles;
    }
}
