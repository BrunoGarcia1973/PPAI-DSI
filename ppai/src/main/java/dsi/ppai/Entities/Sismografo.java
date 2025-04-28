package dsi.ppai.Entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Table(name = "sismografos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Sismografo {

    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "identificador_sismografo")
    private Integer identificadorSismografo;

    @Column(name = "fecha_adquisicion")
    @Temporal(TemporalType.DATE)
    private Date fechaAdquisicion;

    @Column(name = "numero_serie")
    private Integer numeroSerie;

    // Métodos adicionales según el diagrama

    // El método new() sería el constructor, que ya está cubierto por @NoArgsConstructor y @AllArgsConstructor

    // Método serEstadoActual() - necesitarías implementar la lógica específica
    public String serEstadoActual() {
        // Implementación del estado actual
        return "Estado actual del sismógrafo";
    }
}