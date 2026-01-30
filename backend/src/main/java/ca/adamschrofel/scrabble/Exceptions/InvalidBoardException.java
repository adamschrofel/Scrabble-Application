package ca.adamschrofel.scrabble.Exceptions;

public class InvalidBoardException extends RuntimeException{
    public InvalidBoardException(String message){
        super(message);
    }
}
