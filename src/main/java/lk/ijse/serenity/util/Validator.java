package lk.ijse.serenity.util;


import lk.ijse.serenity.exception.MissingFieldException;
import lk.ijse.serenity.exception.SerenityException;

import java.util.regex.Pattern;

public class Validator {

    // Email regex
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[\\w._%+\\-]+@[\\w.\\-]+\\.[a-zA-Z]{2,}$");

    // Sri Lanka phone: +94XXXXXXXXX or 0XXXXXXXXX (10 digits starting with 0, or +94 + 9 digits)
    private static final Pattern PHONE_PATTERN =
            Pattern.compile("^(\\+94|0)[0-9]{9}$");

    // Username: 4-30 alphanumeric/underscore chars
    private static final Pattern USERNAME_PATTERN =
            Pattern.compile("^[a-zA-Z0-9_]{4,30}$");

    // Password: min 8 chars, at least one letter and one digit
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d).{8,}$");

    // Program ID e.g. MT1001
    private static final Pattern PROGRAM_ID_PATTERN =
            Pattern.compile("^[A-Z]{2}[0-9]{4}$");

    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    public static boolean isValidPhone(String phone) {
        return phone != null && PHONE_PATTERN.matcher(phone.trim()).matches();
    }

    public static boolean isValidUsername(String username) {
        return username != null && USERNAME_PATTERN.matcher(username.trim()).matches();
    }

    public static boolean isValidPassword(String password) {
        return password != null && PASSWORD_PATTERN.matcher(password).matches();
    }

    public static boolean isValidProgramId(String programId) {
        return programId != null && PROGRAM_ID_PATTERN.matcher(programId.trim()).matches();
    }

    public static void requireNonEmpty(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new MissingFieldException(fieldName);
        }
    }

    public static void requireValidEmail(String email) {
        requireNonEmpty(email, "Email");
        if (!isValidEmail(email)) {
            throw new SerenityException(
                    "Invalid email format. Expected: user@domain.com");
        }
    }

    public static void requireValidPhone(String phone) {
        requireNonEmpty(phone, "Phone");
        if (!isValidPhone(phone)) {
            throw new SerenityException(
                    "Invalid phone format. Expected: 0XXXXXXXXX or +94XXXXXXXXX");
        }
    }

    public static void requireValidPassword(String password) {
        if (!isValidPassword(password)) {
            throw new SerenityException(
                    "Password must be at least 8 characters with at least one letter and one digit.");
        }
    }

    public static String applyEmailStyle(javafx.scene.control.TextField field) {
        String val = field.getText();
        if (val.isEmpty()) {
            field.setStyle("");
            return null;
        }
        boolean valid = isValidEmail(val);
        field.setStyle(valid ? "-fx-border-color: #27ae60;" : "-fx-border-color: #e74c3c;");
        return valid ? null : "Invalid email format";
    }

    public static String applyPhoneStyle(javafx.scene.control.TextField field) {
        String val = field.getText();
        if (val.isEmpty()) {
            field.setStyle("");
            return null;
        }
        boolean valid = isValidPhone(val);
        field.setStyle(valid ? "-fx-border-color: #27ae60;" : "-fx-border-color: #e74c3c;");
        return valid ? null : "Invalid phone number";
    }
}
