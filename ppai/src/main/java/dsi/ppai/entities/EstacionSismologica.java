package dsi.ppai.entities;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "estaciones_sismologicas")
@Data
@AllArgsConstructor
@NoArgsConstructor

public class EstacionSismologica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_estacion")
    private Integer estacionId;

    private String documentoCertificacionAdq;
    private Date fechaSolicitudCertificacion;
    private double latitud;
    private double longitud;
    private String nombre;
    private Long nroCertificacionAdquisicion;


    public EstacionSismologica(Integer estacionIdstacionId, String documentoCertificacionAdq, Date fechaSolicitudCertificacion,
                               double latitud, double longitud, String nombre, Long nroCertificacionAdquisicion){
        this.estacionId = estacionId;
        this.documentoCertificacionAdq = documentoCertificacionAdq;
        this.fechaSolicitudCertificacion = fechaSolicitudCertificacion;
        this.latitud = latitud;
        this.longitud = longitud;
        this.nombre = nombre;
        this.nroCertificacionAdquisicion = nroCertificacionAdquisicion;
    }

    public String getNombre() {
        return nombre;
    }

      // Este método obtiene el identificador del sismógrafo relacionado
      //COMPLETAR no me queda claro como hacer la dependencia Entre estacion y sismografo
    public Integer obtenerIdentificadorSismografo() {
        Sismografo sismografo = obtenerSismografoRelacionado();
        return Sismografo sismografo.getIdentificador();
    }
}
