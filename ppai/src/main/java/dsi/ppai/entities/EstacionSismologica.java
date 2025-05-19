package dsi.ppai.entities;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


@Data
@AllArgsConstructor
@NoArgsConstructor

public class EstacionSismologica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_estacion")
    private Integer estacionId;

    private String documentoCertificacionAdq;
    private LocalDate fechaSolicitudCertificacion;
    private double latitud;
    private double longitud;
    private String nombre;
    private Long nroCertificacionAdquisicion;
    private Sismografo sismografo;


    // Este método obtiene el identificador del sismógrafo relacionado
      //COMPLETAR no me queda claro como hacer la dependencia Entre estacion y sismografo
    public Integer obtenerIdentificadorSismografo() {
        return this.sismografo.getIdentificadorSismografo();

    }
}
