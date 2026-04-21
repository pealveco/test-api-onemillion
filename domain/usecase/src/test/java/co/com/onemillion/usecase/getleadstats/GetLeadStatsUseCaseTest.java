package co.com.onemillion.usecase.getleadstats;

import co.com.onemillion.model.lead.LeadSource;
import co.com.onemillion.model.lead.LeadStats;
import co.com.onemillion.model.lead.gateways.LeadRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class GetLeadStatsUseCaseTest {
    @Mock
    private LeadRepository leadRepository;

    private GetLeadStatsUseCase useCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        useCase = new GetLeadStatsUseCase(leadRepository);
    }

    @Test
    void shouldBuildLeadStatsFromRepositoryAggregates() {
        when(leadRepository.countAll()).thenReturn(15L);
        when(leadRepository.countByFuente()).thenReturn(Map.of(LeadSource.INSTAGRAM, 8L, LeadSource.FACEBOOK, 7L));
        when(leadRepository.averagePresupuesto()).thenReturn(BigDecimal.valueOf(1850));
        when(leadRepository.countLast7Days()).thenReturn(5L);

        LeadStats result = useCase.execute();

        verify(leadRepository).countAll();
        verify(leadRepository).countByFuente();
        verify(leadRepository).averagePresupuesto();
        verify(leadRepository).countLast7Days();
        assertEquals(15L, result.getTotalLeads());
        assertEquals(8L, result.getLeadsBySource().get(LeadSource.INSTAGRAM));
        assertEquals(BigDecimal.valueOf(1850), result.getAverageBudget());
        assertEquals(5L, result.getLast7DaysLeads());
    }
}
