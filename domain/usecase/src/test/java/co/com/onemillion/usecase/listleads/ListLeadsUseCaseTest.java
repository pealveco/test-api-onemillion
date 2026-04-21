package co.com.onemillion.usecase.listleads;

import co.com.onemillion.model.lead.LeadFilter;
import co.com.onemillion.model.lead.LeadPage;
import co.com.onemillion.model.lead.exceptions.ValidationException;
import co.com.onemillion.model.lead.gateways.LeadRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

class ListLeadsUseCaseTest {
    @Mock
    private LeadRepository leadRepository;

    private ListLeadsUseCase useCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        useCase = new ListLeadsUseCase(leadRepository);
    }

    @Test
    void shouldListLeads() {
        LeadFilter filter = LeadFilter.builder().page(0).limit(10).build();
        when(leadRepository.findAll(filter)).thenReturn(LeadPage.builder()
                .data(List.of())
                .page(0)
                .limit(10)
                .total(0)
                .build());

        LeadPage result = useCase.execute(filter);

        assertEquals(0, result.getPage());
        assertEquals(10, result.getLimit());
    }

    @Test
    void shouldRejectInvalidPage() {
        LeadFilter filter = LeadFilter.builder().page(-1).limit(10).build();

        assertThrows(ValidationException.class, () -> useCase.execute(filter));
    }

    @Test
    void shouldRejectInvalidDateRange() {
        LeadFilter filter = LeadFilter.builder()
                .page(0)
                .limit(10)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().minusDays(1))
                .build();

        assertThrows(ValidationException.class, () -> useCase.execute(filter));
    }
}
