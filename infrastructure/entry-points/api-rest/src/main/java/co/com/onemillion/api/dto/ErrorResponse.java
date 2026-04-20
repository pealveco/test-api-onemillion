package co.com.onemillion.api.dto;

public record ErrorResponse(
        String code,
        String message
) {
}
