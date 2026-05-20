package lk.ijse.serenity.exception;

import java.time.LocalDateTime;

public class SchedulingConflictException extends SerenityException {
    public SchedulingConflictException(String therapistName, LocalDateTime at) {
        super("Therapist '" + therapistName + "' already has a session scheduled at " + at);
    }
}
