package co.com.onemillion.model.lead.exceptions;

public class DuplicateLeadException extends LeadBusinessException {
    public DuplicateLeadException(String email) {
        super("DUPLICATE_LEAD", "Ya existe un lead registrado con el email " + email);
    }
}
