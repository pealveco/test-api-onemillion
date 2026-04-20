package co.com.onemillion.usecase.createlead;

import co.com.onemillion.model.lead.Lead;
import co.com.onemillion.model.lead.LeadSource;
import co.com.onemillion.model.lead.exceptions.DuplicateLeadException;
import co.com.onemillion.model.lead.exceptions.ValidationException;
import co.com.onemillion.model.lead.gateways.LeadRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class CreateLeadUseCaseTest {
    @Mock
    private LeadRepository leadRepository;

    private CreateLeadUseCase useCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        useCase = new CreateLeadUseCase(leadRepository);
    }

    @Test
    void shouldCreateLead() {
        when(leadRepository.existsByEmail("ana@test.com")).thenReturn(false);
        when(leadRepository.save(any(Lead.class))).thenAnswer(invocation -> invocation.getArgument(0, Lead.class)
                .toBuilder()
                .id(1L)
                .build());

        Lead created = useCase.execute(validLead());

        assertEquals(1L, created.getId());
        assertEquals("ana@test.com", created.getEmail());
        assertFalse(created.isDeleted());
        assertNotNull(created.getCreatedAt());
        assertNotNull(created.getUpdatedAt());
    }

    @Test
    void shouldRejectDuplicatedEmail() {
        when(leadRepository.existsByEmail("ana@test.com")).thenReturn(true);

        assertThrows(DuplicateLeadException.class, () -> useCase.execute(validLead()));
    }

    @Test
    void shouldRejectLeadWithoutSource() {
        Lead invalidLead = validLead().toBuilder().fuente(null).build();

        assertThrows(ValidationException.class, () -> useCase.execute(invalidLead));
    }

    private Lead validLead() {
        return Lead.builder()
                .nombre("Ana Perez")
                .email("Ana@Test.com")
                .telefono("3001234567")
                .fuente(LeadSource.INSTAGRAM)
                .productoInteres("CRM")
                .presupuesto(BigDecimal.valueOf(1000))
                .build();
    }
}
