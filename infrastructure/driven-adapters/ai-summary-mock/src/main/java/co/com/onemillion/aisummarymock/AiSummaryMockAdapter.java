package co.com.onemillion.aisummarymock;

import co.com.onemillion.model.lead.Lead;
import co.com.onemillion.model.lead.LeadSource;
import co.com.onemillion.model.lead.gateways.AiSummaryGateway;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class AiSummaryMockAdapter implements AiSummaryGateway {

    @Override
    public String generateLeadSummary(List<Lead> leads) {
        if (leads == null || leads.isEmpty()) {
            return "RESUMEN EJECUTIVO: No se dispone de leads suficientes para generar un análisis estadístico en este momento.";
        }

        long totalLeads = leads.size();
        Map<LeadSource, Long> countsBySource = leads.stream()
                .collect(Collectors.groupingBy(Lead::getFuente, Collectors.counting()));

        LeadSource topSource = countsBySource.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);

        BigDecimal averageBudget = calculateAverageBudget(leads);

        return buildExecutiveSummary(totalLeads, topSource, averageBudget);
    }

    private BigDecimal calculateAverageBudget(List<Lead> leads) {
        List<BigDecimal> budgets = leads.stream()
                .map(Lead::getPresupuesto)
                .filter(Objects::nonNull)
                .toList();

        if (budgets.isEmpty()) {
            return BigDecimal.ZERO;
        }

        BigDecimal total = budgets.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        return total.divide(BigDecimal.valueOf(budgets.size()), 2, RoundingMode.HALF_UP);
    }

    private String buildExecutiveSummary(long total, LeadSource topSource, BigDecimal avgBudget) {
        StringBuilder summary = new StringBuilder();
        summary.append("--- RESUMEN EJECUTIVO DE LEADS ---\n\n");
        summary.append(String.format("Se han analizado un total de %d leads activos. ", total));
        
        if (topSource != null) {
            summary.append(String.format("La fuente de captación predominante es %s, lo que sugiere una alta efectividad en este canal. ", topSource));
        }

        if (avgBudget.compareTo(BigDecimal.ZERO) > 0) {
            summary.append(String.format("El presupuesto promedio identificado es de $%s, reflejando un perfil de cliente con capacidad de inversión moderada-alta. ", avgBudget));
        }

        summary.append("\n\nRECOMENDACIONES ESTRATÉGICAS:\n");
        summary.append("1. Reforzar la inversión en los canales de mayor conversión para maximizar el ROI.\n");
        summary.append("2. Personalizar las ofertas comerciales para los leads con presupuestos superiores al promedio identificado.");

        return summary.toString();
    }
}
