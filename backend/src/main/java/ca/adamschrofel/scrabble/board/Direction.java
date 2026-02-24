package ca.adamschrofel.scrabble.board;

public enum Direction {
    ACROSS(0, 1), 
    DOWN(1, 0);

    public final int directionRow;
    public final int directionColumn;

    Direction(int directionRow, int directionColumn){
        this.directionRow = directionRow;
        this.directionColumn = directionColumn;
    }
}
