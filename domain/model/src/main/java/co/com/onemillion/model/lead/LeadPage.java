package co.com.onemillion.model.lead;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder(toBuilder = true)
public class LeadPage {
    private final List<Lead> data;
    private final int page;
    private final int limit;
    private final long total;

    public int getTotalPages() {
        if (limit <= 0) {
            return 0;
        }
        return (int) Math.ceil((double) total / limit);
    }
}
