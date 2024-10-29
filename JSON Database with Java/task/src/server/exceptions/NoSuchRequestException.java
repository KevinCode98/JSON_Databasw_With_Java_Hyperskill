package server.exceptions;

public class NoSuchRequestException extends RuntimeException{
    public NoSuchRequestException() {
        super("Bad request");
    }
}
