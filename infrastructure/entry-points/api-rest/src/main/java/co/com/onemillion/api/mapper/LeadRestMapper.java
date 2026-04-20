package co.com.onemillion.api.mapper;

import co.com.onemillion.api.dto.CreateLeadRequest;
import co.com.onemillion.api.dto.LeadResponse;
import co.com.onemillion.model.lead.Lead;
import co.com.onemillion.model.lead.LeadSource;
import co.com.onemillion.model.lead.exceptions.ValidationException;

import java.util.Locale;

public final class LeadRestMapper {
    private LeadRestMapper() {
    }

    public static Lead toDomain(CreateLeadRequest request) {
        if (request == null) {
            throw new ValidationException("El cuerpo de la solicitud es obligatorio");
        }

        return Lead.builder()
                .nombre(request.nombre())
                .email(request.email())
                .telefono(request.telefono())
                .fuente(toLeadSource(request.fuente()))
                .productoInteres(request.productoInteres())
                .presupuesto(request.presupuesto())
                .build();
    }

    public static LeadResponse toResponse(Lead lead) {
        return new LeadResponse(
                lead.getId(),
                lead.getNombre(),
                lead.getEmail(),
                lead.getTelefono(),
                toApiSource(lead.getFuente()),
                lead.getProductoInteres(),
                lead.getPresupuesto(),
                lead.getCreatedAt(),
                lead.getUpdatedAt()
        );
    }

    private static LeadSource toLeadSource(String source) {
        if (source == null || source.trim().isEmpty()) {
            return null;
        }
        try {
            return LeadSource.valueOf(source.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            throw new ValidationException("La fuente debe ser una de: instagram, facebook, landing_page, referido, otro");
        }
    }

    private static String toApiSource(LeadSource source) {
        return source == null ? null : source.name().toLowerCase(Locale.ROOT);
    }
}
