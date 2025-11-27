package dsi.ppai.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@Table(name = "sesion")
@NoArgsConstructor
@Data
public class Sesion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(
            name = "id",
            nullable = false,
            unique = true,
            foreignKey = @ForeignKey(name = "fk_sesion_usuario")
    )
    private Usuario usuarioLogueado;

    public Sesion(Usuario usuario) {
        this.usuarioLogueado = usuario;
    }

    public void setUsuarioLogueado(Usuario usuario) {
        this.usuarioLogueado = usuario;
    }

    public Empleado obtenerEmpleadoLogueado() {
        if (usuarioLogueado == null) {
            return null;
        }
        return usuarioLogueado.obtenerEmpleado();
    }
}