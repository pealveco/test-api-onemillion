package co.com.onemillion.api.dto;

import java.util.List;

public record LeadPageResponse(
        List<LeadResponse> data,
        int page,
        int limit,
        long total,
        int totalPages
) {
}
