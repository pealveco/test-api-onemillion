package co.com.onemillion.api;

import co.com.onemillion.api.dto.CreateLeadRequest;
import co.com.onemillion.api.dto.LeadPageResponse;
import co.com.onemillion.api.dto.LeadResponse;
import co.com.onemillion.api.dto.LeadStatsResponse;
import co.com.onemillion.api.dto.LeadSummaryRequest;
import co.com.onemillion.api.dto.UpdateLeadRequest;
import co.com.onemillion.model.lead.Lead;
import co.com.onemillion.model.lead.LeadFilter;
import co.com.onemillion.model.lead.LeadPage;
import co.com.onemillion.model.lead.LeadSource;
import co.com.onemillion.model.lead.LeadStats;
import co.com.onemillion.usecase.createlead.CreateLeadUseCase;
import co.com.onemillion.usecase.deletelead.DeleteLeadUseCase;
import co.com.onemillion.usecase.getaileadsummary.GetAiLeadSummaryUseCase;
import co.com.onemillion.usecase.getleadbyid.GetLeadByIdUseCase;
import co.com.onemillion.usecase.getleadstats.GetLeadStatsUseCase;
import co.com.onemillion.usecase.listleads.ListLeadsUseCase;
import co.com.onemillion.usecase.updatelead.UpdateLeadUseCase;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ApiRestTest {

    @Test
    void shouldCreateLead() {
        CreateLeadUseCase createLeadUseCase = mock(CreateLeadUseCase.class);
        ApiRest apiRest = newApiRest(
                createLeadUseCase,
                mock(GetLeadByIdUseCase.class),
                mock(ListLeadsUseCase.class),
                mock(UpdateLeadUseCase.class),
                mock(DeleteLeadUseCase.class),
                mock(GetLeadStatsUseCase.class),
                mock(GetAiLeadSummaryUseCase.class)
        );

        when(createLeadUseCase.execute(any(Lead.class))).thenReturn(sampleLead().toBuilder().id(1L).build());

        ResponseEntity<LeadResponse> response = apiRest.createLead(new CreateLeadRequest(
                "Ana Perez",
                "ana@test.com",
                "3001234567",
                "instagram",
                "CRM",
                BigDecimal.valueOf(1000)
        ));

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(1L, response.getBody().id());
    }

    @Test
    void shouldGetLeadById() {
        GetLeadByIdUseCase getLeadByIdUseCase = mock(GetLeadByIdUseCase.class);
        ApiRest apiRest = newApiRest(
                mock(CreateLeadUseCase.class),
                getLeadByIdUseCase,
                mock(ListLeadsUseCase.class),
                mock(UpdateLeadUseCase.class),
                mock(DeleteLeadUseCase.class),
                mock(GetLeadStatsUseCase.class),
                mock(GetAiLeadSummaryUseCase.class)
        );

        when(getLeadByIdUseCase.execute(1L)).thenReturn(sampleLead().toBuilder().id(1L).build());

        ResponseEntity<LeadResponse> response = apiRest.getLeadById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1L, response.getBody().id());
    }

    @Test
    void shouldListLeads() {
        ListLeadsUseCase listLeadsUseCase = mock(ListLeadsUseCase.class);
        ApiRest apiRest = newApiRest(
                mock(CreateLeadUseCase.class),
                mock(GetLeadByIdUseCase.class),
                listLeadsUseCase,
                mock(UpdateLeadUseCase.class),
                mock(DeleteLeadUseCase.class),
                mock(GetLeadStatsUseCase.class),
                mock(GetAiLeadSummaryUseCase.class)
        );

        when(listLeadsUseCase.execute(any())).thenReturn(LeadPage.builder()
                .data(List.of(sampleLead().toBuilder().id(1L).build()))
                .page(0)
                .limit(10)
                .total(1)
                .build());

        ResponseEntity<LeadPageResponse> response = apiRest.listLeads(0, 10, "instagram", null, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().data().size());
        assertEquals(1, response.getBody().total());
    }

    @Test
    void shouldUpdateLead() {
        UpdateLeadUseCase updateLeadUseCase = mock(UpdateLeadUseCase.class);
        ApiRest apiRest = newApiRest(
                mock(CreateLeadUseCase.class),
                mock(GetLeadByIdUseCase.class),
                mock(ListLeadsUseCase.class),
                updateLeadUseCase,
                mock(DeleteLeadUseCase.class),
                mock(GetLeadStatsUseCase.class),
                mock(GetAiLeadSummaryUseCase.class)
        );
        UpdateLeadRequest request = new UpdateLeadRequest();
        request.setNombre("Ana Actualizada");

        when(updateLeadUseCase.execute(any(), any())).thenReturn(sampleLead().toBuilder()
                .id(1L)
                .nombre("Ana Actualizada")
                .build());

        ResponseEntity<LeadResponse> response = apiRest.updateLead(1L, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Ana Actualizada", response.getBody().nombre());
    }

    @Test
    void shouldDeleteLead() {
        DeleteLeadUseCase deleteLeadUseCase = mock(DeleteLeadUseCase.class);
        ApiRest apiRest = newApiRest(
                mock(CreateLeadUseCase.class),
                mock(GetLeadByIdUseCase.class),
                mock(ListLeadsUseCase.class),
                mock(UpdateLeadUseCase.class),
                deleteLeadUseCase,
                mock(GetLeadStatsUseCase.class),
                mock(GetAiLeadSummaryUseCase.class)
        );

        ResponseEntity<Void> response = apiRest.deleteLead(1L);

        verify(deleteLeadUseCase).execute(1L);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void shouldGetLeadStats() {
        GetLeadStatsUseCase getLeadStatsUseCase = mock(GetLeadStatsUseCase.class);
        ApiRest apiRest = newApiRest(
                mock(CreateLeadUseCase.class),
                mock(GetLeadByIdUseCase.class),
                mock(ListLeadsUseCase.class),
                mock(UpdateLeadUseCase.class),
                mock(DeleteLeadUseCase.class),
                getLeadStatsUseCase,
                mock(GetAiLeadSummaryUseCase.class)
        );

        when(getLeadStatsUseCase.execute()).thenReturn(LeadStats.builder()
                .totalLeads(12)
                .leadsBySource(Map.of(LeadSource.INSTAGRAM, 7L))
                .averageBudget(BigDecimal.valueOf(2500))
                .last7DaysLeads(4)
                .build());

        ResponseEntity<LeadStatsResponse> response = apiRest.getStats();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(12, response.getBody().getTotalLeads());
        assertEquals(7L, response.getBody().getLeadsBySource().get(LeadSource.INSTAGRAM));
    }

    @Test
    void shouldGetAiSummaryUsingRequestFilter() {
        GetAiLeadSummaryUseCase getAiLeadSummaryUseCase = mock(GetAiLeadSummaryUseCase.class);
        ApiRest apiRest = newApiRest(
                mock(CreateLeadUseCase.class),
                mock(GetLeadByIdUseCase.class),
                mock(ListLeadsUseCase.class),
                mock(UpdateLeadUseCase.class),
                mock(DeleteLeadUseCase.class),
                mock(GetLeadStatsUseCase.class),
                getAiLeadSummaryUseCase
        );

        when(getAiLeadSummaryUseCase.execute(any())).thenReturn("Resumen IA");

        ResponseEntity<Map<String, String>> response = apiRest.getAiSummary(new LeadSummaryRequest(
                "facebook",
                "2026-04-01",
                "2026-04-10"
        ));

        ArgumentCaptor<LeadFilter> captor = ArgumentCaptor.forClass(LeadFilter.class);
        verify(getAiLeadSummaryUseCase).execute(captor.capture());

        LeadFilter filter = captor.getValue();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Resumen IA", response.getBody().get("summary"));
        assertEquals(0, filter.getPage());
        assertEquals(1000, filter.getLimit());
        assertEquals(LeadSource.FACEBOOK, filter.getSource());
    }

    @Test
    void shouldGetAiSummaryWithEmptyRequestWhenBodyIsNull() {
        GetAiLeadSummaryUseCase getAiLeadSummaryUseCase = mock(GetAiLeadSummaryUseCase.class);
        ApiRest apiRest = newApiRest(
                mock(CreateLeadUseCase.class),
                mock(GetLeadByIdUseCase.class),
                mock(ListLeadsUseCase.class),
                mock(UpdateLeadUseCase.class),
                mock(DeleteLeadUseCase.class),
                mock(GetLeadStatsUseCase.class),
                getAiLeadSummaryUseCase
        );

        when(getAiLeadSummaryUseCase.execute(any())).thenReturn("Resumen sin filtros");

        ResponseEntity<Map<String, String>> response = apiRest.getAiSummary(null);

        ArgumentCaptor<LeadFilter> captor = ArgumentCaptor.forClass(LeadFilter.class);
        verify(getAiLeadSummaryUseCase).execute(captor.capture());

        LeadFilter filter = captor.getValue();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Resumen sin filtros", response.getBody().get("summary"));
        assertNull(filter.getSource());
        assertNull(filter.getStartDate());
        assertNull(filter.getEndDate());
    }

    @Test
    void shouldValidateInvalidRequestName() {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        CreateLeadRequest request = new CreateLeadRequest(
                "A",
                "ana@test.com",
                "3001234567",
                "instagram",
                "CRM",
                BigDecimal.valueOf(1000)
        );

        assertFalse(validator.validate(request).isEmpty());
    }

    @Test
    void shouldValidateInvalidPatchEmail() {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        UpdateLeadRequest request = new UpdateLeadRequest();
        request.setEmail("correo-malo");

        assertFalse(validator.validate(request).isEmpty());
    }

    @Test
    void shouldValidateEmptyPatchRequest() {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        UpdateLeadRequest request = new UpdateLeadRequest();

        assertFalse(validator.validate(request).isEmpty());
    }

    @Test
    void shouldValidateInvalidPatchSource() {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        UpdateLeadRequest request = new UpdateLeadRequest();
        request.setFuente("twitter");

        assertFalse(validator.validate(request).isEmpty());
    }

    private ApiRest newApiRest(
            CreateLeadUseCase createLeadUseCase,
            GetLeadByIdUseCase getLeadByIdUseCase,
            ListLeadsUseCase listLeadsUseCase,
            UpdateLeadUseCase updateLeadUseCase,
            DeleteLeadUseCase deleteLeadUseCase,
            GetLeadStatsUseCase getLeadStatsUseCase,
            GetAiLeadSummaryUseCase getAiLeadSummaryUseCase
    ) {
        return new ApiRest(
                createLeadUseCase,
                getLeadByIdUseCase,
                listLeadsUseCase,
                updateLeadUseCase,
                deleteLeadUseCase,
                getLeadStatsUseCase,
                getAiLeadSummaryUseCase
        );
    }

    private Lead sampleLead() {
        LocalDateTime now = LocalDateTime.now();
        return Lead.builder()
                .nombre("Ana Perez")
                .email("ana@test.com")
                .telefono("3001234567")
                .fuente(LeadSource.INSTAGRAM)
                .productoInteres("CRM")
                .presupuesto(BigDecimal.valueOf(1000))
                .deleted(false)
                .createdAt(now.minusDays(1))
                .updatedAt(now)
                .build();
    }
}
