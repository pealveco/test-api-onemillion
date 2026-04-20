package co.com.onemillion.api.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record LeadResponse(
        Long id,
        String nombre,
        String email,
        String telefono,
        String fuente,
        String productoInteres,
        BigDecimal presupuesto,
        boolean deleted,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
