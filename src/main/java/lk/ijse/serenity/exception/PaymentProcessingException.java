package lk.ijse.serenity.exception;

public class PaymentProcessingException extends SerenityException {
    public PaymentProcessingException(String reason) {
        super("Payment processing failed: " + reason);
    }
}
