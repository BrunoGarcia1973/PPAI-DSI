package dsi.ppai.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.HashSet;
import java.util.Set; // <--- Import necesario

@Entity
@Table(name = "rol")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Rol {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre", nullable = false, unique = true)
    private String nombre;

    @Column(name = "descripcion_rol")
    private String descripcion;

    // AÑADIR ESTO: Relación inversa ManyToMany
    @ManyToMany(mappedBy = "roles") // 'roles' debe coincidir con el nombre de la variable en Usuario.java
    private Set<Usuario> usuarios = new HashSet<>();

    // ... (restos de constructores)
}