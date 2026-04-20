package co.com.onemillion.model.lead;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
@Builder(toBuilder = true)
public class LeadPatch {
    private final boolean nombrePresent;
    private final String nombre;
    private final boolean emailPresent;
    private final String email;
    private final boolean telefonoPresent;
    private final String telefono;
    private final boolean fuentePresent;
    private final LeadSource fuente;
    private final boolean productoInteresPresent;
    private final String productoInteres;
    private final boolean presupuestoPresent;
    private final BigDecimal presupuesto;

    public boolean hasAnyFieldPresent() {
        return nombrePresent || emailPresent || telefonoPresent || fuentePresent || productoInteresPresent
                || presupuestoPresent;
    }
}
