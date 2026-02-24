package ca.adamschrofel.scrabble.exceptions;

public class InvalidBoardException extends RuntimeException{
    public InvalidBoardException(String message){
        super(message);
    }
}
