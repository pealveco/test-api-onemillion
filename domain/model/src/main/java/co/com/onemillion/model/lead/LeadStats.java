package co.com.onemillion.model.lead;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.Map;

@Getter
@Builder
public class LeadStats {
    private final long totalLeads;
    private final Map<LeadSource, Long> leadsBySource;
    private final BigDecimal averageBudget;
    private final long last7DaysLeads;
}
