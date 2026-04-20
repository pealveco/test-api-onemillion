package co.com.onemillion.model.lead.exceptions;

public class LeadNotFoundException extends LeadBusinessException {
    public LeadNotFoundException(Long id) {
        super("LEAD_NOT_FOUND", "No existe un lead activo con id " + id);
    }
}
