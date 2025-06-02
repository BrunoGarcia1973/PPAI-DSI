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

    public Usuario getUsuarioLogueado() {
        return usuarioLogueado;
    }

    public Empleado obtenerEmpleadoLogueado() {
        if (usuarioLogueado == null) {
            throw new IllegalStateException("No hay un usuario logueado.");
        }
        return usuarioLogueado.obtenerEmpleado();
    }
}