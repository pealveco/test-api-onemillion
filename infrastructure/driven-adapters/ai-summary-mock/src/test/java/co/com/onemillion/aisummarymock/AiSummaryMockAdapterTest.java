package co.com.onemillion.aisummarymock;

import co.com.onemillion.model.lead.Lead;
import co.com.onemillion.model.lead.LeadSource;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class AiSummaryMockAdapterTest {
    private final AiSummaryMockAdapter adapter = new AiSummaryMockAdapter();

    @Test
    void shouldReturnFallbackSummaryWhenNoLeadsAreProvided() {
        String result = adapter.generateLeadSummary(List.of());

        assertTrue(result.contains("No se dispone de leads suficientes"));
    }

    @Test
    void shouldGenerateExecutiveSummaryWithTopSourceAndAverageBudget() {
        List<Lead> leads = List.of(
                Lead.builder().fuente(LeadSource.INSTAGRAM).presupuesto(BigDecimal.valueOf(1000)).build(),
                Lead.builder().fuente(LeadSource.INSTAGRAM).presupuesto(BigDecimal.valueOf(3000)).build(),
                Lead.builder().fuente(LeadSource.FACEBOOK).presupuesto(null).build()
        );

        String result = adapter.generateLeadSummary(leads);

        assertTrue(result.contains("3 leads activos"));
        assertTrue(result.contains("INSTAGRAM"));
        assertTrue(result.contains("$2000.00"));
        assertTrue(result.contains("RECOMENDACIONES ESTRATÉGICAS"));
    }

    @Test
    void shouldGenerateSummaryWithoutBudgetSentenceWhenAllBudgetsAreNull() {
        List<Lead> leads = List.of(
                Lead.builder().fuente(LeadSource.FACEBOOK).presupuesto(null).build(),
                Lead.builder().fuente(LeadSource.FACEBOOK).presupuesto(null).build()
        );

        String result = adapter.generateLeadSummary(leads);

        assertTrue(result.contains("2 leads activos"));
        assertTrue(result.contains("FACEBOOK"));
        assertTrue(!result.contains("presupuesto promedio identificado"));
    }
}
