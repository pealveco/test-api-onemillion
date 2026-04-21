package co.com.onemillion.usecase.getleadstats;

import co.com.onemillion.model.lead.LeadStats;
import co.com.onemillion.model.lead.gateways.LeadRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GetLeadStatsUseCase {
    private final LeadRepository leadRepository;

    public LeadStats execute() {
        return LeadStats.builder()
                .totalLeads(leadRepository.countAll())
                .leadsBySource(leadRepository.countByFuente())
                .averageBudget(leadRepository.averagePresupuesto())
                .last7DaysLeads(leadRepository.countLast7Days())
                .build();
    }
}
