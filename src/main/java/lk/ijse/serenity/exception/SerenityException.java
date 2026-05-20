package lk.ijse.serenity.exception;

/**
 * Base application exception for all Serenity system errors.
 */
public class SerenityException extends RuntimeException {
    public SerenityException(String message) {
        super(message);
    }

    public SerenityException(String message, Throwable cause) {
        super(message, cause);
    }
}
