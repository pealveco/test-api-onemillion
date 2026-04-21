package co.com.onemillion.usecase.updatelead;

import co.com.onemillion.model.lead.Lead;
import co.com.onemillion.model.lead.LeadPatch;
import co.com.onemillion.model.lead.LeadSource;
import co.com.onemillion.model.lead.exceptions.DuplicateLeadException;
import co.com.onemillion.model.lead.exceptions.LeadNotFoundException;
import co.com.onemillion.model.lead.exceptions.ValidationException;
import co.com.onemillion.model.lead.gateways.LeadRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class UpdateLeadUseCaseTest {
    @Mock
    private LeadRepository leadRepository;

    private UpdateLeadUseCase useCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        useCase = new UpdateLeadUseCase(leadRepository);
    }

    @Test
    void shouldUpdateOnlyPresentFields() {
        LocalDateTime createdAt = LocalDateTime.now().minusDays(1);
        Lead currentLead = lead(createdAt);
        LeadPatch patch = LeadPatch.builder()
                .nombrePresent(true)
                .nombre("Ana Actualizada")
                .build();

        when(leadRepository.findById(1L)).thenReturn(Optional.of(currentLead));
        when(leadRepository.update(any(Lead.class))).thenAnswer(invocation -> invocation.getArgument(0, Lead.class));

        Lead updatedLead = useCase.execute(1L, patch);

        assertEquals("Ana Actualizada", updatedLead.getNombre());
        assertEquals("ana@test.com", updatedLead.getEmail());
        assertEquals(createdAt, updatedLead.getCreatedAt());
        assertNotEquals(currentLead.getUpdatedAt(), updatedLead.getUpdatedAt());
    }

    @Test
    void shouldRejectDuplicateEmailWhenEmailChanges() {
        Lead currentLead = lead(LocalDateTime.now().minusDays(1));
        LeadPatch patch = LeadPatch.builder()
                .emailPresent(true)
                .email("otra@test.com")
                .build();

        when(leadRepository.findById(1L)).thenReturn(Optional.of(currentLead));
        when(leadRepository.findByEmail("otra@test.com")).thenReturn(Optional.of(Lead.builder()
                .id(2L)
                .email("otra@test.com")
                .build()));

        assertThrows(DuplicateLeadException.class, () -> useCase.execute(1L, patch));
    }

    @Test
    void shouldThrowNotFoundWhenLeadDoesNotExist() {
        when(leadRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(LeadNotFoundException.class, () -> useCase.execute(99L,
                LeadPatch.builder().nombrePresent(true).nombre("Ana").build()));
    }

    @Test
    void shouldRejectEmptyEmailAsDefensiveValidation() {
        assertThrows(ValidationException.class, () -> useCase.execute(1L,
                LeadPatch.builder().emailPresent(true).email(" ").build()));
    }

    private Lead lead(LocalDateTime createdAt) {
        return Lead.builder()
                .id(1L)
                .nombre("Ana")
                .email("ana@test.com")
                .telefono("3001234567")
                .fuente(LeadSource.INSTAGRAM)
                .createdAt(createdAt)
                .updatedAt(createdAt)
                .deleted(false)
                .build();
    }
}
