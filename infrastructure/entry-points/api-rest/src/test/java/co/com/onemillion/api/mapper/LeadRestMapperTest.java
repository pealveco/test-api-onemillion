package co.com.onemillion.api.mapper;

import co.com.onemillion.api.dto.CreateLeadRequest;
import co.com.onemillion.api.dto.LeadPageResponse;
import co.com.onemillion.api.dto.LeadStatsResponse;
import co.com.onemillion.api.dto.UpdateLeadRequest;
import co.com.onemillion.model.lead.Lead;
import co.com.onemillion.model.lead.LeadFilter;
import co.com.onemillion.model.lead.LeadPage;
import co.com.onemillion.model.lead.LeadPatch;
import co.com.onemillion.model.lead.LeadSource;
import co.com.onemillion.model.lead.LeadStats;
import co.com.onemillion.model.lead.exceptions.ValidationException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LeadRestMapperTest {

    @Test
    void shouldMapCreateRequestToDomainLead() {
        Lead lead = LeadRestMapper.toDomain(new CreateLeadRequest(
                "Ana Perez",
                "ana@test.com",
                "3001234567",
                "instagram",
                "CRM",
                BigDecimal.valueOf(1000)
        ));

        assertEquals("Ana Perez", lead.getNombre());
        assertEquals(LeadSource.INSTAGRAM, lead.getFuente());
    }

    @Test
    void shouldRejectNullCreateRequest() {
        ValidationException exception = assertThrows(ValidationException.class, () -> LeadRestMapper.toDomain(null));

        assertEquals("VALIDATION_ERROR", exception.getCode());
    }

    @Test
    void shouldMapLeadPageAndStatsResponses() {
        LocalDateTime now = LocalDateTime.parse("2026-04-20T10:15:30");
        Lead lead = Lead.builder()
                .id(9L)
                .nombre("Ana Perez")
                .email("ana@test.com")
                .telefono("3001234567")
                .fuente(LeadSource.LANDING_PAGE)
                .productoInteres("CRM")
                .presupuesto(BigDecimal.valueOf(1000))
                .createdAt(now.minusDays(1))
                .updatedAt(now)
                .build();

        LeadPageResponse pageResponse = LeadRestMapper.toPageResponse(LeadPage.builder()
                .data(List.of(lead))
                .page(1)
                .limit(5)
                .total(9)
                .build());

        LeadStatsResponse statsResponse = LeadRestMapper.toStatsResponse(LeadStats.builder()
                .totalLeads(9)
                .leadsBySource(Map.of(LeadSource.LANDING_PAGE, 4L))
                .averageBudget(BigDecimal.valueOf(1800))
                .last7DaysLeads(3)
                .build());

        assertEquals("landing_page", pageResponse.data().getFirst().fuente());
        assertEquals(2, pageResponse.totalPages());
        assertEquals(4L, statsResponse.getLeadsBySource().get(LeadSource.LANDING_PAGE));
    }

    @Test
    void shouldMapFilterUsingDateAndDateTimeFormats() {
        LeadFilter filter = LeadRestMapper.toFilter(2, 25, "facebook", "2026-04-01", "2026-04-20T12:30:45");

        assertEquals(2, filter.getPage());
        assertEquals(25, filter.getLimit());
        assertEquals(LeadSource.FACEBOOK, filter.getSource());
        assertEquals(LocalDateTime.parse("2026-04-01T00:00:00"), filter.getStartDate());
        assertEquals(LocalDateTime.parse("2026-04-20T12:30:45"), filter.getEndDate());
    }

    @Test
    void shouldRejectInvalidDateFormat() {
        ValidationException exception = assertThrows(ValidationException.class,
                () -> LeadRestMapper.toFilter(0, 10, null, "04/20/2026", null));

        assertTrue(exception.getMessage().contains("yyyy-MM-dd"));
    }

    @Test
    void shouldMapPatchOnlyForPresentFields() {
        UpdateLeadRequest request = new UpdateLeadRequest();
        request.setNombre("Ana Actualizada");
        request.setFuente("otro");

        LeadPatch patch = LeadRestMapper.toPatch(request);

        assertTrue(patch.isNombrePresent());
        assertTrue(patch.isFuentePresent());
        assertEquals("Ana Actualizada", patch.getNombre());
        assertEquals(LeadSource.OTRO, patch.getFuente());
    }

    @Test
    void shouldRejectNullPatchRequest() {
        ValidationException exception = assertThrows(ValidationException.class, () -> LeadRestMapper.toPatch(null));

        assertEquals("VALIDATION_ERROR", exception.getCode());
    }
}
