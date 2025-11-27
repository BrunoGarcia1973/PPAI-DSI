package dsi.ppai.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "usuario")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "nombre_usuario", nullable = false, unique = true)
    private String nombreUsuario;
    
    @Column(name = "contrasenia", nullable = false)
    private String contrasenia;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "empleado_id", nullable = false)
    private Empleado empleado;
    
    @ManyToMany
    @JoinTable(
        name = "usuario_rol",
        joinColumns = @JoinColumn(name = "usuario_id"),
        inverseJoinColumns = @JoinColumn(name = "rol_id")
    )
    private Set<Rol> roles = new HashSet<>();

    public Usuario(String nombreUsuario, Empleado empleado) {
        this.nombreUsuario = nombreUsuario;
        this.empleado = empleado;
    }

    public Empleado obtenerEmpleado() {
        return this.empleado;
    }
}