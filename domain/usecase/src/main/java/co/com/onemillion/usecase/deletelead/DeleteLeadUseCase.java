package co.com.onemillion.usecase.deletelead;

import co.com.onemillion.model.lead.Lead;
import co.com.onemillion.model.lead.exceptions.LeadNotFoundException;
import co.com.onemillion.model.lead.exceptions.ValidationException;
import co.com.onemillion.model.lead.gateways.LeadRepository;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@RequiredArgsConstructor
public class DeleteLeadUseCase {
    private final LeadRepository leadRepository;

    public void execute(Long id) {
        validateId(id);

        Lead lead = leadRepository.findById(id)
                .orElseThrow(() -> new LeadNotFoundException(id));

        Lead deletedLead = lead.toBuilder()
                .deleted(true)
                .updatedAt(LocalDateTime.now())
                .build();

        leadRepository.update(deletedLead);
    }

    private void validateId(Long id) {
        if (id == null || id <= 0) {
            throw new ValidationException("El id del lead debe ser un numero positivo");
        }
    }
}
