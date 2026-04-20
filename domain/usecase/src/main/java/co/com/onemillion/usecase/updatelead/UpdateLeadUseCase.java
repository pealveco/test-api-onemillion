package co.com.onemillion.usecase.updatelead;

import co.com.onemillion.model.lead.Lead;
import co.com.onemillion.model.lead.LeadPatch;
import co.com.onemillion.model.lead.exceptions.DuplicateLeadException;
import co.com.onemillion.model.lead.exceptions.LeadNotFoundException;
import co.com.onemillion.model.lead.exceptions.ValidationException;
import co.com.onemillion.model.lead.gateways.LeadRepository;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.regex.Pattern;

@RequiredArgsConstructor
public class UpdateLeadUseCase {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}$",
            Pattern.CASE_INSENSITIVE);

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
        if (patch.isNombrePresent() && (isBlank(patch.getNombre()) || patch.getNombre().trim().length() < 2)) {
            throw new ValidationException("El nombre debe tener al menos 2 caracteres");
        }
        if (patch.isEmailPresent()) {
            validateEmail(patch.getEmail());
        }
        if (patch.isFuentePresent() && patch.getFuente() == null) {
            throw new ValidationException("La fuente debe ser una de: instagram, facebook, landing_page, referido, otro");
        }
    }

    private void validateId(Long id) {
        if (id == null || id <= 0) {
            throw new ValidationException("El id del lead debe ser un numero positivo");
        }
    }

    private void validateEmail(String email) {
        if (isBlank(email)) {
            throw new ValidationException("El email no puede estar vacio");
        }
        if (!EMAIL_PATTERN.matcher(email.trim()).matches()) {
            throw new ValidationException("El email no tiene un formato valido");
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
