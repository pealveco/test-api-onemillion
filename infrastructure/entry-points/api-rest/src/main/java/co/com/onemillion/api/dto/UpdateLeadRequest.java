package co.com.onemillion.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

public class UpdateLeadRequest {
    private final Set<String> presentFields = new HashSet<>();
    private String nombre;
    private String email;
    private String telefono;
    private String fuente;
    private String productoInteres;
    private BigDecimal presupuesto;

    @JsonIgnore
    public boolean isPresent(String field) {
        return presentFields.contains(field);
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
}
