package co.com.onemillion.api.mapper;

import co.com.onemillion.api.dto.CreateLeadRequest;
import co.com.onemillion.api.dto.LeadPageResponse;
import co.com.onemillion.api.dto.LeadResponse;
import co.com.onemillion.api.dto.LeadStatsResponse;
import co.com.onemillion.api.dto.UpdateLeadRequest;
import co.com.onemillion.model.lead.Lead;
import co.com.onemillion.model.lead.LeadFilter;
import co.com.onemillion.model.lead.LeadPage;
import co.com.onemillion.model.lead.LeadPatch;
import co.com.onemillion.model.lead.LeadSource;
import co.com.onemillion.model.lead.LeadStats;
import co.com.onemillion.model.lead.exceptions.ValidationException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
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

    public static LeadPageResponse toPageResponse(LeadPage leadPage) {
        return new LeadPageResponse(
                leadPage.getData().stream().map(LeadRestMapper::toResponse).toList(),
                leadPage.getPage(),
                leadPage.getLimit(),
                leadPage.getTotal(),
                leadPage.getTotalPages()
        );
    }

    public static LeadStatsResponse toStatsResponse(LeadStats stats) {
        return LeadStatsResponse.builder()
                .totalLeads(stats.getTotalLeads())
                .leadsBySource(stats.getLeadsBySource())
                .averageBudget(stats.getAverageBudget())
                .last7DaysLeads(stats.getLast7DaysLeads())
                .build();
    }

    public static LeadFilter toFilter(int page, int limit, String source, String startDate, String endDate) {
        return LeadFilter.builder()
                .page(page)
                .limit(limit)
                .source(toLeadSource(source))
                .startDate(toStartDate(startDate))
                .endDate(toEndDate(endDate))
                .build();
    }

    public static LeadPatch toPatch(UpdateLeadRequest request) {
        if (request == null) {
            throw new ValidationException("El cuerpo de la solicitud es obligatorio");
        }

        return LeadPatch.builder()
                .nombrePresent(request.isPresent("nombre"))
                .nombre(request.getNombre())
                .emailPresent(request.isPresent("email"))
                .email(request.getEmail())
                .telefonoPresent(request.isPresent("telefono"))
                .telefono(request.getTelefono())
                .fuentePresent(request.isPresent("fuente"))
                .fuente(toLeadSource(request.getFuente()))
                .productoInteresPresent(request.isPresent("productoInteres"))
                .productoInteres(request.getProductoInteres())
                .presupuestoPresent(request.isPresent("presupuesto"))
                .presupuesto(request.getPresupuesto())
                .build();
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

    private static LocalDateTime toStartDate(String value) {
        LocalDateTime dateTime = toDateTime(value);
        return dateTime != null ? dateTime : toDate(value, false);
    }

    private static LocalDateTime toEndDate(String value) {
        LocalDateTime dateTime = toDateTime(value);
        return dateTime != null ? dateTime : toDate(value, true);
    }

    private static LocalDateTime toDateTime(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return LocalDateTime.parse(value.trim());
        } catch (DateTimeParseException exception) {
            return null;
        }
    }

    private static LocalDateTime toDate(String value, boolean endOfDay) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            LocalDate date = LocalDate.parse(value.trim());
            return endOfDay ? date.atTime(23, 59, 59, 999_999_999) : date.atStartOfDay();
        } catch (DateTimeParseException exception) {
            throw new ValidationException("Las fechas deben tener formato ISO: yyyy-MM-dd o yyyy-MM-ddTHH:mm:ss");
        }
    }
}
