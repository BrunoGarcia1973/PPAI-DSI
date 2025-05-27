package dsi.ppai.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.List; // Si la estación tiene una lista de algo (ej. historial, etc.)

@Data // Proporciona getters, setters, equals, hashCode, toString de Lombok
@AllArgsConstructor // Para el constructor con todos los argumentos
@NoArgsConstructor // Para el constructor sin argumentos (útil para frameworks o deserialización)
public class EstacionSismologica {

    // Ya no necesitas @Id, @GeneratedValue, @Column si no hay DB
    private Integer estacionId; // Un simple campo para un ID, si lo manejas en memoria
    private String documentoCertificacionAdq;
    private LocalDate fechaSolicitudCertificacion;
    private double latitud;
    private double longitud;
    private String nombre;
    private String nroCertificacionAdquisicion;
    private Sismografo sismografo;


    // public EstacionSismologica(String documentoCertificacionAdq, LocalDate fechaSolicitudCertificacion, double latitud, double longitud, String nombre, Long nroCertificacionAdquisicion, Sismografo sismografo) {
    //     this.documentoCertificacionAdq = documentoCertificacionAdq;
    //     this.fechaSolicitudCertificacion = fechaSolicitudCertificacion;
    //     this.latitud = latitud;
    //     this.longitud = longitud;
    //     this.nombre = nombre;
    //     this.nroCertificacionAdquisicion = nroCertificacionAdquisicion;
    //     this.sismografo = sismografo;
    // }


    public Integer obtenerIdentificadorSismografo() {
        if (this.sismografo != null) {
            return this.sismografo.getIdentificadorSismografo();
        }
        return null; // O lanzar una excepción si el sismógrafo no debería ser nulo
    }

    // Método de delegación a Sismografo (como ya lo teníamos)
    public void marcarSismografoFueraDeServicio(Empleado empleadoQueCierra, List<MotivoFueraServicio> motivosSeleccionados) {
        if (this.sismografo != null) {
            this.sismografo.marcarFueraDeServicio(empleadoQueCierra, motivosSeleccionados);
        } else {
            System.err.println("Advertencia: Estación sismológica '" + nombre + "' sin sismógrafo asociado. No se puede poner fuera de servicio.");
            // Si esto no debería pasar, considera lanzar una RuntimeException
        }
    }
}
