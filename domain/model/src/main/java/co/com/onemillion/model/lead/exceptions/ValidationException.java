package co.com.onemillion.model.lead.exceptions;

public class ValidationException extends LeadBusinessException {
    public ValidationException(String message) {
        super("VALIDATION_ERROR", message);
    }
}
