package co.com.onemillion.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class UpdateLeadRequest {
    private final Set<String> presentFields = new HashSet<>();
    @Size(min = 2, message = "El nombre debe tener al menos 2 caracteres")
    private String nombre;
    @Email(message = "El email no tiene un formato valido")
    private String email;
    private String telefono;
    private String fuente;
    private String productoInteres;
    private BigDecimal presupuesto;

    @JsonIgnore
    public boolean isPresent(String field) {
        return presentFields.contains(field);
    }

    @JsonIgnore
    @AssertTrue(message = "Debe enviar al menos un campo para actualizar")
    public boolean isNotEmptyPatch() {
        return !presentFields.isEmpty();
    }

    @JsonIgnore
    @AssertTrue(message = "El nombre no puede estar vacio")
    public boolean isNombreValidWhenPresent() {
        return !isPresent("nombre") || !isBlank(nombre);
    }

    @JsonIgnore
    @AssertTrue(message = "El email no puede estar vacio")
    public boolean isEmailValidWhenPresent() {
        return !isPresent("email") || !isBlank(email);
    }

    @JsonIgnore
    @AssertTrue(message = "La fuente debe ser una de: instagram, facebook, landing_page, referido, otro")
    public boolean isFuenteValidWhenPresent() {
        if (!isPresent("fuente")) {
            return true;
        }
        if (isBlank(fuente)) {
            return false;
        }

        return Set.of("instagram", "facebook", "landing_page", "referido", "otro")
                .contains(fuente.trim().toLowerCase(Locale.ROOT));
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        presentFields.add("nombre");
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        presentFields.add("email");
        this.email = email;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        presentFields.add("telefono");
        this.telefono = telefono;
    }

    public String getFuente() {
        return fuente;
    }

    public void setFuente(String fuente) {
        presentFields.add("fuente");
        this.fuente = fuente;
    }

    public String getProductoInteres() {
        return productoInteres;
    }

    public void setProductoInteres(String productoInteres) {
        presentFields.add("productoInteres");
        this.productoInteres = productoInteres;
    }

    public BigDecimal getPresupuesto() {
        return presupuesto;
    }

    public void setPresupuesto(BigDecimal presupuesto) {
        presentFields.add("presupuesto");
        this.presupuesto = presupuesto;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
