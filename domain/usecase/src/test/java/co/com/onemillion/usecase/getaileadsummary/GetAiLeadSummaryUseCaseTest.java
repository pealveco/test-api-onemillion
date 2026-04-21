package co.com.onemillion.usecase.getaileadsummary;

import co.com.onemillion.model.lead.Lead;
import co.com.onemillion.model.lead.LeadFilter;
import co.com.onemillion.model.lead.LeadPage;
import co.com.onemillion.model.lead.LeadSource;
import co.com.onemillion.model.lead.exceptions.ValidationException;
import co.com.onemillion.model.lead.gateways.AiSummaryGateway;
import co.com.onemillion.model.lead.gateways.LeadRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class GetAiLeadSummaryUseCaseTest {
    @Mock
    private LeadRepository leadRepository;

    @Mock
    private AiSummaryGateway aiSummaryGateway;

    private GetAiLeadSummaryUseCase useCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        useCase = new GetAiLeadSummaryUseCase(leadRepository, aiSummaryGateway);
    }

    @Test
    void shouldGenerateSummaryUsingExpandedFilterWindow() {
        Lead lead = Lead.builder().id(1L).nombre("Ana").fuente(LeadSource.INSTAGRAM).build();
        LeadFilter filter = LeadFilter.builder()
                .page(2)
                .limit(25)
                .source(LeadSource.INSTAGRAM)
                .startDate(LocalDateTime.parse("2026-04-01T00:00:00"))
                .endDate(LocalDateTime.parse("2026-04-10T23:59:59"))
                .build();

        when(leadRepository.findAll(org.mockito.ArgumentMatchers.any())).thenReturn(LeadPage.builder()
                .data(List.of(lead))
                .page(0)
                .limit(1000)
                .total(1)
                .build());
        when(aiSummaryGateway.generateLeadSummary(anyList())).thenReturn("Resumen ejecutivo");

        String result = useCase.execute(filter);

        ArgumentCaptor<LeadFilter> captor = ArgumentCaptor.forClass(LeadFilter.class);
        verify(leadRepository).findAll(captor.capture());
        verify(aiSummaryGateway).generateLeadSummary(List.of(lead));

        LeadFilter repositoryFilter = captor.getValue();
        assertEquals("Resumen ejecutivo", result);
        assertEquals(0, repositoryFilter.getPage());
        assertEquals(1000, repositoryFilter.getLimit());
        assertEquals(LeadSource.INSTAGRAM, repositoryFilter.getSource());
        assertEquals(filter.getStartDate(), repositoryFilter.getStartDate());
        assertEquals(filter.getEndDate(), repositoryFilter.getEndDate());
    }

    @Test
    void shouldRejectInvalidDateRange() {
        LeadFilter filter = LeadFilter.builder()
                .page(0)
                .limit(10)
                .startDate(LocalDateTime.parse("2026-04-10T00:00:00"))
                .endDate(LocalDateTime.parse("2026-04-01T00:00:00"))
                .build();

        ValidationException exception = assertThrows(ValidationException.class, () -> useCase.execute(filter));

        verifyNoInteractions(leadRepository, aiSummaryGateway);
        assertTrue(exception.getMessage().contains("startDate"));
    }
}
