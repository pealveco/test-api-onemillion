package co.com.onemillion.model.lead;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LeadModelTest {

    @Test
    void shouldBuildLeadAndAllowMutations() {
        LocalDateTime now = LocalDateTime.parse("2026-04-20T20:00:00");
        Lead lead = Lead.builder()
                .id(1L)
                .nombre("Ana")
                .email("ana@test.com")
                .telefono("3001234567")
                .fuente(LeadSource.INSTAGRAM)
                .productoInteres("CRM")
                .presupuesto(BigDecimal.valueOf(1200))
                .deleted(false)
                .createdAt(now.minusDays(1))
                .updatedAt(now)
                .build();

        lead.setNombre("Ana Maria");
        lead.setDeleted(true);

        assertEquals(1L, lead.getId());
        assertEquals("Ana Maria", lead.getNombre());
        assertTrue(lead.isDeleted());
    }

    @Test
    void shouldBuildLeadFilterAndPatch() {
        LocalDateTime start = LocalDateTime.parse("2026-04-01T00:00:00");
        LocalDateTime end = LocalDateTime.parse("2026-04-20T23:59:59");
        LeadFilter filter = LeadFilter.builder()
                .page(1)
                .limit(20)
                .source(LeadSource.FACEBOOK)
                .startDate(start)
                .endDate(end)
                .build();
        LeadPatch patch = LeadPatch.builder()
                .nombrePresent(true)
                .nombre("Ana")
                .emailPresent(false)
                .telefonoPresent(false)
                .fuentePresent(true)
                .fuente(LeadSource.REFERIDO)
                .productoInteresPresent(false)
                .presupuestoPresent(false)
                .build();

        assertEquals(1, filter.getPage());
        assertEquals(20, filter.getLimit());
        assertEquals(LeadSource.FACEBOOK, filter.getSource());
        assertEquals(start, filter.getStartDate());
        assertEquals(end, filter.getEndDate());
        assertTrue(patch.hasAnyFieldPresent());
        assertEquals(LeadSource.REFERIDO, patch.getFuente());
    }

    @Test
    void shouldCalculateLeadPageTotalPages() {
        LeadPage page = LeadPage.builder()
                .data(List.of())
                .page(0)
                .limit(10)
                .total(21)
                .build();
        LeadPage emptyLimitPage = LeadPage.builder()
                .data(List.of())
                .page(0)
                .limit(0)
                .total(21)
                .build();

        assertEquals(3, page.getTotalPages());
        assertEquals(0, emptyLimitPage.getTotalPages());
    }

    @Test
    void shouldBuildLeadStats() {
        LeadStats stats = LeadStats.builder()
                .totalLeads(12)
                .leadsBySource(Map.of(LeadSource.OTRO, 3L))
                .averageBudget(BigDecimal.valueOf(2300))
                .last7DaysLeads(4)
                .build();

        assertEquals(12, stats.getTotalLeads());
        assertEquals(3L, stats.getLeadsBySource().get(LeadSource.OTRO));
        assertEquals(BigDecimal.valueOf(2300), stats.getAverageBudget());
        assertEquals(4, stats.getLast7DaysLeads());
        assertFalse(LeadPatch.builder().build().hasAnyFieldPresent());
    }
}
