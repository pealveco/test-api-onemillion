package co.com.onemillion.api;

import co.com.onemillion.api.dto.CreateLeadRequest;
import co.com.onemillion.api.dto.LeadPageResponse;
import co.com.onemillion.api.dto.LeadResponse;
import co.com.onemillion.api.dto.UpdateLeadRequest;
import co.com.onemillion.model.lead.Lead;
import co.com.onemillion.model.lead.LeadPage;
import co.com.onemillion.model.lead.LeadSource;
import co.com.onemillion.usecase.createlead.CreateLeadUseCase;
import co.com.onemillion.usecase.getleadbyid.GetLeadByIdUseCase;
import co.com.onemillion.usecase.listleads.ListLeadsUseCase;
import co.com.onemillion.usecase.updatelead.UpdateLeadUseCase;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ApiRestTest {

    @Test
    void shouldCreateLead() {
        CreateLeadUseCase createLeadUseCase = mock(CreateLeadUseCase.class);
        GetLeadByIdUseCase getLeadByIdUseCase = mock(GetLeadByIdUseCase.class);
        ListLeadsUseCase listLeadsUseCase = mock(ListLeadsUseCase.class);
        UpdateLeadUseCase updateLeadUseCase = mock(UpdateLeadUseCase.class);
        ApiRest apiRest = new ApiRest(createLeadUseCase, getLeadByIdUseCase, listLeadsUseCase, updateLeadUseCase);
        LocalDateTime now = LocalDateTime.now();

        when(createLeadUseCase.execute(any(Lead.class))).thenReturn(Lead.builder()
                .id(1L)
                .nombre("Ana Perez")
                .email("ana@test.com")
                .telefono("3001234567")
                .fuente(LeadSource.INSTAGRAM)
                .productoInteres("CRM")
                .presupuesto(BigDecimal.valueOf(1000))
                .deleted(false)
                .createdAt(now)
                .updatedAt(now)
                .build());

        ResponseEntity<?> response = apiRest.createLead(new CreateLeadRequest(
                "Ana Perez",
                "ana@test.com",
                "3001234567",
                "instagram",
                "CRM",
                BigDecimal.valueOf(1000)
        ));

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void shouldGetLeadById() {
        CreateLeadUseCase createLeadUseCase = mock(CreateLeadUseCase.class);
        GetLeadByIdUseCase getLeadByIdUseCase = mock(GetLeadByIdUseCase.class);
        ListLeadsUseCase listLeadsUseCase = mock(ListLeadsUseCase.class);
        UpdateLeadUseCase updateLeadUseCase = mock(UpdateLeadUseCase.class);
        ApiRest apiRest = new ApiRest(createLeadUseCase, getLeadByIdUseCase, listLeadsUseCase, updateLeadUseCase);
        LocalDateTime now = LocalDateTime.now();

        when(getLeadByIdUseCase.execute(1L)).thenReturn(Lead.builder()
                .id(1L)
                .nombre("Ana Perez")
                .email("ana@test.com")
                .fuente(LeadSource.INSTAGRAM)
                .createdAt(now)
                .updatedAt(now)
                .build());

        ResponseEntity<LeadResponse> response = apiRest.getLeadById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1L, response.getBody().id());
    }

    @Test
    void shouldListLeads() {
        CreateLeadUseCase createLeadUseCase = mock(CreateLeadUseCase.class);
        GetLeadByIdUseCase getLeadByIdUseCase = mock(GetLeadByIdUseCase.class);
        ListLeadsUseCase listLeadsUseCase = mock(ListLeadsUseCase.class);
        UpdateLeadUseCase updateLeadUseCase = mock(UpdateLeadUseCase.class);
        ApiRest apiRest = new ApiRest(createLeadUseCase, getLeadByIdUseCase, listLeadsUseCase, updateLeadUseCase);
        LocalDateTime now = LocalDateTime.now();

        when(listLeadsUseCase.execute(any())).thenReturn(LeadPage.builder()
                .data(List.of(Lead.builder()
                        .id(1L)
                        .nombre("Ana Perez")
                        .email("ana@test.com")
                        .fuente(LeadSource.INSTAGRAM)
                        .createdAt(now)
                        .updatedAt(now)
                        .build()))
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
        CreateLeadUseCase createLeadUseCase = mock(CreateLeadUseCase.class);
        GetLeadByIdUseCase getLeadByIdUseCase = mock(GetLeadByIdUseCase.class);
        ListLeadsUseCase listLeadsUseCase = mock(ListLeadsUseCase.class);
        UpdateLeadUseCase updateLeadUseCase = mock(UpdateLeadUseCase.class);
        ApiRest apiRest = new ApiRest(createLeadUseCase, getLeadByIdUseCase, listLeadsUseCase, updateLeadUseCase);
        LocalDateTime now = LocalDateTime.now();
        UpdateLeadRequest request = new UpdateLeadRequest();
        request.setNombre("Ana Actualizada");

        when(updateLeadUseCase.execute(any(), any())).thenReturn(Lead.builder()
                .id(1L)
                .nombre("Ana Actualizada")
                .email("ana@test.com")
                .fuente(LeadSource.INSTAGRAM)
                .createdAt(now.minusDays(1))
                .updatedAt(now)
                .build());

        ResponseEntity<LeadResponse> response = apiRest.updateLead(1L, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Ana Actualizada", response.getBody().nombre());
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
}
