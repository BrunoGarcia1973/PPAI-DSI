package dsi.ppai.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "empleado")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Empleado {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "nombre", nullable = false)
    private String nombre;
    
    @Column(name = "apellido", nullable = false)
    private String apellido;
    
    @Column(name = "mail", nullable = false, unique = true)
    private String email;
    
    @Column(name = "telefono")
    private String telefono;
    
    @OneToMany(mappedBy = "empleado", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Usuario> usuarios = new ArrayList<>();
    
    @Transient
    private Rol rol;

    public String getLegajo() {
        return String.valueOf(this.id);
    }

    public Empleado(String nombre, String apellido, String email, String telefono, Rol rol) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.telefono = telefono;
        this.rol = rol;
    }

    public boolean esResponsableDeInspeccion() {
        return this.rol != null && "RESPONSABLE_DE_INSPECCION".equals(this.rol.getNombre());
    }
}