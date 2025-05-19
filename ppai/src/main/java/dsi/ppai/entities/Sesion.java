package dsi.ppai.entities;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
@Component
@NoArgsConstructor
@Data

// Se registra como bean de Spring
public class Sesion {
    private Usuario usuarioLogueado;

    public Sesion(Usuario usuario) {
        this.usuarioLogueado = usuario; // ¡Aquí asignamos el usuario!
    }

    /** Inyecta el usuario tras el login */
    public void setUsuarioLogueado(Usuario usuario) {
        this.usuarioLogueado = usuario;
    }

    /** Devuelve el usuario actualmente logueado */
    public Usuario getUsuarioLogueado() {
        return usuarioLogueado;
    }

    /** Devuelve el Empleado asociado al usuario logueado */
    public Empleado obtenerEmpleado() {
        if (usuarioLogueado == null) {
            throw new IllegalStateException("No hay un usuario logueado.");
        }
        return usuarioLogueado.getEmpleado();
    }
}
