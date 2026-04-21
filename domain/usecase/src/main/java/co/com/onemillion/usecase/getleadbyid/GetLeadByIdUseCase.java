package co.com.onemillion.usecase.getleadbyid;

import co.com.onemillion.model.lead.Lead;
import co.com.onemillion.model.lead.exceptions.LeadNotFoundException;
import co.com.onemillion.model.lead.exceptions.ValidationException;
import co.com.onemillion.model.lead.gateways.LeadRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GetLeadByIdUseCase {
    private final LeadRepository leadRepository;

    public Lead execute(Long id) {
        if (id == null || id <= 0) {
            throw new ValidationException("El id del lead debe ser un numero positivo");
        }

        return leadRepository.findById(id)
                .orElseThrow(() -> new LeadNotFoundException(id));
    }
}
