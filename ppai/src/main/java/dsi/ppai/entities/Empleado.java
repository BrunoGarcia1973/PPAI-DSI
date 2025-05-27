// En Empleado.java
package dsi.ppai.entities;

import lombok.Data;
// import lombok.Getter; // @Data ya incluye @Getter
// import lombok.Setter; // @Data ya incluye @Setter
import lombok.NoArgsConstructor; // Necesario para un constructor sin argumentos

@Data
@NoArgsConstructor // Si necesitas un constructor sin argumentos
public class Empleado {
    private String legajo;
    private String nombre;
    private String apellido;
    private String mail;
    private String telefono;
    private Rol rol;

    public Empleado(String legajo, String apellido, String mail, String nombre, String telefono, Rol rol) {
        this.legajo = legajo;
        this.apellido = apellido;
        this.mail = mail;
        this.nombre = nombre;
        this.telefono = telefono;
        this.rol = rol;
    }

    @Override
    public String toString() {
        return nombre + " " + apellido + " (" + legajo + ")";
    }

    // Es una buena práctica sobreescribir equals y hashCode si Empleado va a ser clave en colecciones o usado en ComboBox
    // @Data ya lo hace, pero si usas el legajo como identificador único, podría ser así:
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Empleado empleado = (Empleado) o;
        return legajo != null ? legajo.equals(empleado.legajo) : empleado.legajo == null;
    }

    @Override
    public int hashCode() {
        return legajo != null ? legajo.hashCode() : 0;
    }
}