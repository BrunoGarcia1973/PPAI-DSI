package dsi.ppai.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EstacionSismologica {
    private Long estacionId;
    private String documentoCertificacionAdq;
    private LocalDate fechaSolicitudCertificacion;
    private Double latitud;
    private Double longitud;
    private Long nroCertificacionAdquisicion; // <-- CAMBIO AQUÍ: de Integer a Long
    private String nombre;
    private Sismografo sismografo;
}