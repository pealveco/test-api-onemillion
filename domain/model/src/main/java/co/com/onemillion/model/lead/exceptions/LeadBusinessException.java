package co.com.onemillion.model.lead.exceptions;

public class LeadBusinessException extends RuntimeException {
    private final String code;

    public LeadBusinessException(String code, String message) {
        super(message);
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
