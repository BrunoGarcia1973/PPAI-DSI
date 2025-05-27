package dsi.ppai.entities;

import lombok.*;

@Data
@NoArgsConstructor
@Getter
@Setter
 // Opcional, si quieres un constructor con todos los campos
public class Usuario {
    private String nombre;
    private String password;
    private Empleado empleado; // <--- ¡Añade esto! Relación con Empleado

    // Constructor si no usas @AllArgsConstructor
    public Usuario(String nombre, String password, Empleado empleado) {
        this.nombre = nombre;
        this.password = password;
        this.empleado = empleado;
    }

    // Método para obtener el empleado asociado al usuario
    public Empleado getEmpleado() {
        return this.empleado;
    }

    public boolean esResponsableDeInspeccion() {
        // Implementa la lógica para determinar si el usuario es RI
        // Por ejemplo, basándote en el rol del empleado o una propiedad del usuario
        return this.empleado != null && this.empleado.getRol().equals("Responsable de Inspeccion");
        // O si tienes un rol en Usuario: return this.rol.equals("Responsable de Inspeccion");
    }
}
