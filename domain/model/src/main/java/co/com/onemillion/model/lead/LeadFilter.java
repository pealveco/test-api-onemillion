package co.com.onemillion.model.lead;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder(toBuilder = true)
public class LeadFilter {
    private final int page;
    private final int limit;
    private final LeadSource source;
    private final LocalDateTime startDate;
    private final LocalDateTime endDate;
}
