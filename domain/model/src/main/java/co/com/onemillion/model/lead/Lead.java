package co.com.onemillion.model.lead;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@Builder(toBuilder = true)
public class Lead {
    private Long id;
    private String nombre;
    private String email;
    private String telefono;
    private LeadSource fuente;
    private String productoInteres;
    private BigDecimal presupuesto;
    private boolean deleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
