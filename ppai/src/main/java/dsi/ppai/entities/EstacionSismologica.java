package dsi.ppai.entities;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.util.List;

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

    public void ponerSismografoFueraServicio(List<MotivoFueraServicio> motivos, Empleado logueado, Estado estadoFueraDeServicio) {
        this.sismografo.fueraDeServicio(motivos, logueado, estadoFueraDeServicio);
    }
}
