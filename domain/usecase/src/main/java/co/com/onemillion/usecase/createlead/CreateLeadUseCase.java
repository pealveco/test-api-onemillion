package co.com.onemillion.usecase.createlead;

import co.com.onemillion.model.lead.Lead;
import co.com.onemillion.model.lead.exceptions.DuplicateLeadException;
import co.com.onemillion.model.lead.exceptions.ValidationException;
import co.com.onemillion.model.lead.gateways.LeadRepository;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@RequiredArgsConstructor
public class CreateLeadUseCase {
    private final LeadRepository leadRepository;

    public Lead execute(Lead lead) {
        validate(lead);

        String email = lead.getEmail().trim().toLowerCase();
        if (leadRepository.existsByEmail(email)) {
            throw new DuplicateLeadException(email);
        }

        LocalDateTime now = LocalDateTime.now();
        Lead leadToCreate = lead.toBuilder()
                .nombre(lead.getNombre().trim())
                .email(email)
                .deleted(false)
                .createdAt(now)
                .updatedAt(now)
                .build();

        return leadRepository.save(leadToCreate);
    }

    private void validate(Lead lead) {
        if (lead == null) {
            throw new ValidationException("El lead es obligatorio");
        }
        if (isBlank(lead.getEmail())) {
            throw new ValidationException("El email es obligatorio");
        }
        if (lead.getFuente() == null) {
            throw new ValidationException("La fuente es obligatoria");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
