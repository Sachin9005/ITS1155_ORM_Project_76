package lk.ijse.serenity.exception;

public class InvalidCredentialsException extends SerenityException {
    public InvalidCredentialsException() {
        super("Invalid username or password. Please try again.");
    }
}
