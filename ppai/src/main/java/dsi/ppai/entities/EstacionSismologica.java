package dsi.ppai.entities;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class EstacionSismologica {
    private Long estacionId;
    private String nombre;
    private double latitud;
    private double longitud;
    private String documentoCertificacionAdq;
    private Long nroCertificacionAdquisicion;
    private LocalDate fechaSolicitudCertificacion;
    private Sismografo sismografo; // Referencia al Sismografo asociado

    // Getter explícito para sismografo, aunque Lombok @Data ya lo crea,
    // lo mantengo para claridad en el diagrama de clases.
    public Sismografo getSismografo() {
        return this.sismografo;
    }

    public boolean esSismografoEnMantenimiento() {
        return sismografo != null && sismografo.getEstadoActual() != null &&
                sismografo.getEstadoActual().esEnMantenimiento();
    }
    public boolean esSismografoFueraDeServicio() {
        return sismografo != null && sismografo.getEstadoActual() != null &&
                sismografo.getEstadoActual().esFueraDeServicio();
    }
}