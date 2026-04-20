package co.com.onemillion.usecase.deletelead;

import co.com.onemillion.model.lead.Lead;
import co.com.onemillion.model.lead.LeadSource;
import co.com.onemillion.model.lead.exceptions.LeadNotFoundException;
import co.com.onemillion.model.lead.exceptions.ValidationException;
import co.com.onemillion.model.lead.gateways.LeadRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DeleteLeadUseCaseTest {
    private LeadRepository leadRepository;
    private DeleteLeadUseCase useCase;

    @BeforeEach
    void setUp() {
        leadRepository = mock(LeadRepository.class);
        useCase = new DeleteLeadUseCase(leadRepository);
    }

    @Test
    void shouldSoftDeleteLead() {
        LocalDateTime createdAt = LocalDateTime.now().minusDays(1);
        Lead lead = Lead.builder()
                .id(1L)
                .nombre("Ana Perez")
                .email("ana@test.com")
                .fuente(LeadSource.INSTAGRAM)
                .deleted(false)
                .createdAt(createdAt)
                .updatedAt(createdAt)
                .build();

        when(leadRepository.findById(1L)).thenReturn(Optional.of(lead));

        useCase.execute(1L);

        verify(leadRepository).update(argThat(updatedLead ->
                updatedLead.getId().equals(1L)
                        && updatedLead.isDeleted()
                        && updatedLead.getCreatedAt().equals(createdAt)
                        && updatedLead.getUpdatedAt().isAfter(createdAt)
        ));
    }

    @Test
    void shouldRejectInvalidId() {
        assertThrows(ValidationException.class, () -> useCase.execute(0L));
    }

    @Test
    void shouldThrowNotFoundWhenLeadDoesNotExist() {
        when(leadRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(LeadNotFoundException.class, () -> useCase.execute(99L));
    }
}
