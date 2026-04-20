package co.com.onemillion.usecase.updatelead;

import co.com.onemillion.model.lead.Lead;
import co.com.onemillion.model.lead.LeadPatch;
import co.com.onemillion.model.lead.exceptions.DuplicateLeadException;
import co.com.onemillion.model.lead.exceptions.LeadNotFoundException;
import co.com.onemillion.model.lead.exceptions.ValidationException;
import co.com.onemillion.model.lead.gateways.LeadRepository;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@RequiredArgsConstructor
public class UpdateLeadUseCase {
    private final LeadRepository leadRepository;

    public Lead execute(Long id, LeadPatch patch) {
        validateId(id);
        validatePatch(patch);

        Lead currentLead = leadRepository.findById(id)
                .orElseThrow(() -> new LeadNotFoundException(id));

        Lead updatedLead = applyPatch(currentLead, patch);
        return leadRepository.update(updatedLead);
    }

    private Lead applyPatch(Lead currentLead, LeadPatch patch) {
        Lead.LeadBuilder builder = currentLead.toBuilder();

        if (patch.isNombrePresent()) {
            builder.nombre(patch.getNombre().trim());
        }
        if (patch.isEmailPresent()) {
            String email = patch.getEmail().trim().toLowerCase();
            validateEmailUniqueness(currentLead, email);
            builder.email(email);
        }
        if (patch.isTelefonoPresent()) {
            builder.telefono(patch.getTelefono());
        }
        if (patch.isFuentePresent()) {
            builder.fuente(patch.getFuente());
        }
        if (patch.isProductoInteresPresent()) {
            builder.productoInteres(patch.getProductoInteres());
        }
        if (patch.isPresupuestoPresent()) {
            builder.presupuesto(patch.getPresupuesto());
        }

        return builder.updatedAt(LocalDateTime.now()).build();
    }

    private void validatePatch(LeadPatch patch) {
        if (patch == null) {
            throw new ValidationException("El cuerpo de la solicitud es obligatorio");
        }
        if (!patch.hasAnyFieldPresent()) {
            throw new ValidationException("Debe enviar al menos un campo para actualizar");
        }
        if (patch.isEmailPresent() && isBlank(patch.getEmail())) {
            throw new ValidationException("El email no puede estar vacio");
        }
    }

    private void validateId(Long id) {
        if (id == null || id <= 0) {
            throw new ValidationException("El id del lead debe ser un numero positivo");
        }
    }

    private void validateEmailUniqueness(Lead currentLead, String email) {
        if (email.equalsIgnoreCase(currentLead.getEmail())) {
            return;
        }

        leadRepository.findByEmail(email)
                .filter(existingLead -> !existingLead.getId().equals(currentLead.getId()))
                .ifPresent(existingLead -> {
                    throw new DuplicateLeadException(email);
                });
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
