package dsi.ppai.entities;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;
import java.util.Date;

@Entity
@Table(name = "sismografos")
@Data
@NoArgsConstructor
@AllArgsConstructor

public class Sismografo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_sismografo")
    private Integer SismografoId;

    @Column(name = "fecha_adquisicion")
    @Temporal(TemporalType.DATE)
    private Date fechaAdquisicion;

    @Column(name = "numero_serie")
    private Integer numeroSerie;

    private Estacion estacion; //Relacion con la estacion
    private List<CambioEstado> cambiosDeEstados; //RELACION CON CAMBIO DE ESTADO

    // Métodos adicionales según el diagrama

    // El método new() sería el constructor, que ya está cubierto por @NoArgsConstructor y @AllArgsConstructor

    // Método serEstadoActual() - necesitarías implementar la lógica específica
    public String serEstadoActual() {
        // Implementación del estado actual
        return "Estado actual del sismógrafo";
    }
}