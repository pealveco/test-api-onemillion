package co.com.onemillion.usecase.listleads;

import co.com.onemillion.model.lead.LeadFilter;
import co.com.onemillion.model.lead.LeadPage;
import co.com.onemillion.model.lead.exceptions.ValidationException;
import co.com.onemillion.model.lead.gateways.LeadRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ListLeadsUseCase {
    private static final int MAX_LIMIT = 100;

    private final LeadRepository leadRepository;

    public LeadPage execute(LeadFilter filter) {
        validate(filter);
        return leadRepository.findAll(filter);
    }

    private void validate(LeadFilter filter) {
        if (filter == null) {
            throw new ValidationException("Los filtros de busqueda son obligatorios");
        }
        if (filter.getPage() < 0) {
            throw new ValidationException("El parametro page debe ser mayor o igual a 0");
        }
        if (filter.getLimit() < 1 || filter.getLimit() > MAX_LIMIT) {
            throw new ValidationException("El parametro limit debe estar entre 1 y " + MAX_LIMIT);
        }
        if (filter.getStartDate() != null && filter.getEndDate() != null
                && filter.getStartDate().isAfter(filter.getEndDate())) {
            throw new ValidationException("startDate no puede ser mayor que endDate");
        }
    }
}
