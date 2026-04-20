package co.com.onemillion.api.dto;

public record LeadSummaryRequest(
    String source,
    String startDate,
    String endDate
) {}
