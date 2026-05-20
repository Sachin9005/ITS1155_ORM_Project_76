package lk.ijse.serenity.exception;

public class DuplicateRegistrationException extends SerenityException {
    public DuplicateRegistrationException(String field, String value) {
        super("Duplicate entry: " + field + " '" + value + "' already exists.");
    }
}
