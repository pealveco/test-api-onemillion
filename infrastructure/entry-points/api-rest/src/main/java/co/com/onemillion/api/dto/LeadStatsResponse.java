package co.com.onemillion.api.dto;

import co.com.onemillion.model.lead.LeadSource;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.Map;

@Getter
@Builder
public class LeadStatsResponse {
    private final long totalLeads;
    private final Map<LeadSource, Long> leadsBySource;
    private final BigDecimal averageBudget;
    private final long last7DaysLeads;
}
