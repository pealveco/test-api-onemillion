package co.com.onemillion.usecase.getleadbyid;

import co.com.onemillion.model.lead.Lead;
import co.com.onemillion.model.lead.LeadSource;
import co.com.onemillion.model.lead.exceptions.LeadNotFoundException;
import co.com.onemillion.model.lead.exceptions.ValidationException;
import co.com.onemillion.model.lead.gateways.LeadRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

class GetLeadByIdUseCaseTest {
    @Mock
    private LeadRepository leadRepository;

    private GetLeadByIdUseCase useCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        useCase = new GetLeadByIdUseCase(leadRepository);
    }

    @Test
    void shouldGetLeadById() {
        when(leadRepository.findById(1L)).thenReturn(Optional.of(Lead.builder()
                .id(1L)
                .nombre("Ana Perez")
                .email("ana@test.com")
                .fuente(LeadSource.INSTAGRAM)
                .build()));

        Lead lead = useCase.execute(1L);

        assertEquals(1L, lead.getId());
        assertEquals("ana@test.com", lead.getEmail());
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
