package dsi.ppai.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "estado", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"ambito", "nombre_estado"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Estado {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "nombre_estado", nullable = false)
    private String nombre;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "ambito", nullable = false)
    private AmbitoEstado ambito;

    public Estado(String nombre) {
        this.nombre = nombre;
    }

    public boolean esAbierta() {
        return "ABIERTA".equals(this.nombre);
    }
    public boolean esCompletamenteRealizada() {
        return "COMPLETAMENTE_REALIZADA".equals(this.nombre);
    }
    public boolean esFueraDeServicio() {
        return "FUERA_DE_SERVICIO".equals(this.nombre);
    }
    public boolean esEnMantenimiento() {
        return "EN_MANTENIMIENTO".equals(this.nombre);
    }
    public boolean esCerrada() {
        return "CERRADA".equals(this.nombre);
    }
    
    public enum AmbitoEstado {
        SISMOGRAFO,
        ORDEN_INSPECCION
    }
}