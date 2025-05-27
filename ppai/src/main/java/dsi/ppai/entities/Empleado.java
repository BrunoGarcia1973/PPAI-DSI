package dsi.ppai.entities;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
public class Empleado {
    private String legajo;
    private String nombre;
    private String apellido;
    private String email;
    private String telefono;
    private Rol rol;

    public Empleado(String legajo, String nombre, String apellido, String email, String telefono, Rol rol) {
        this.legajo = legajo;
        this.nombre = nombre;
        this.apellido = apellido;
        this.telefono = telefono;
        this.email = email;
        this.rol = rol;
    }

    public boolean esResponsableDeInspeccion() {
        return this.rol != null && "RESPONSABLE_DE_INSPECCION".equals(this.rol.getNombre());
    }
}