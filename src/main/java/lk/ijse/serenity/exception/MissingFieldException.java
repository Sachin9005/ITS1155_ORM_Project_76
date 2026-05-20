package lk.ijse.serenity.exception;

public class MissingFieldException extends SerenityException {
    public MissingFieldException(String fieldName) {
        super("Required field is missing: " + fieldName);
    }
}
