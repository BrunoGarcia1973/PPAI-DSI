package dsi.ppai.entities;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
@Data
public class Sesion {
    private Usuario usuarioLogueado;

    public Sesion(Usuario usuario) {
        this.usuarioLogueado = usuario;
    }

    public void setUsuarioLogueado(Usuario usuario) {
        this.usuarioLogueado = usuario;
    }

    /**
     * Obtiene el Empleado asociado al usuario logueado.
     * * CORRECCIÓN: Devuelve null si no hay un usuario logueado en lugar
     * de lanzar una excepción. Esto permite a la Interfaz de Inspección
     * verificar la sesión sin fallar.
     *
     * @return El Empleado asociado o null si no hay un usuario logueado.
     */
    public Empleado obtenerEmpleadoLogueado() {
        if (usuarioLogueado == null) {
            return null; // <--- CAMBIO CRUCIAL: Devuelve null en lugar de lanzar excepción
        }
        // Asumiendo que usuarioLogueado.obtenerEmpleado() nunca devuelve null si usuarioLogueado no lo es.
        return usuarioLogueado.obtenerEmpleado();
    }
}