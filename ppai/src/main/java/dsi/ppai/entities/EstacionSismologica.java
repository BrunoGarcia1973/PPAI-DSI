package dsi.ppai.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "estacion_sismologica")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EstacionSismologica {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long estacionId;

    @Column(name = "codigo_estacion", nullable = false, unique = true)
    private String codigoEstacion;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "latitud", nullable = false, precision = 9, scale = 6)
    private BigDecimal latitud;

    @Column(name = "longitud", nullable = false, precision = 9, scale = 6)
    private BigDecimal longitud;

    @Column(name = "nro_certificacion_adquisicion")
    private String nroCertificacionAdquisicion;

    @Column(name = "documento_certificacion_adq")
    private String documentoCertificacionAdq;

    @Column(name = "fecha_solicitud_certificado")
    private LocalDate fechaSolicitudCertificacion;

    // Relación Uno a Muchos con Sismografos (Cargada EAGERLY para simplificar la interfaz)
    @OneToMany(mappedBy = "estacionSismologica", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Sismografo> sismografos = new ArrayList<>();

    // Nota: El campo @Transient 'sismografo' ha sido eliminado, ya que el método lo maneja.

    /**
     * Método auxiliar requerido por GestorInspeccion.cerrarOrden
     * para obtener la instancia de Sismógrafo principal de la estación.
     */
    public Sismografo getSismografo() {
        // Devuelve el primer sismógrafo de la lista (asumiendo que es el activo/principal)
        if (sismografos != null && !sismografos.isEmpty()) {
            return sismografos.get(0);
        }
        return null;
    }
}