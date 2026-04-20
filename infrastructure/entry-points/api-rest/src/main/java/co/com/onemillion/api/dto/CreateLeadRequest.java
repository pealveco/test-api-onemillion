package co.com.onemillion.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record CreateLeadRequest(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(min = 2, message = "El nombre debe tener al menos 2 caracteres")
        String nombre,

        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El email no tiene un formato valido")
        String email,

        String telefono,

        @NotBlank(message = "La fuente es obligatoria")
        String fuente,

        String productoInteres,

        BigDecimal presupuesto
) {
}
